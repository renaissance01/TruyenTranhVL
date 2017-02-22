package truyentranh.vl.fragmentschap;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import truyentranh.vl.R;
import truyentranh.vl.database.Database;
import truyentranh.vl.model.DbTaiTruyen;
import truyentranh.vl.model.Decompress;
import truyentranh.vl.model.Download;


public class DownloadService extends IntentService {

    // File url to download
    private static String file_url;

    String idtruyen, tentruyen, tacgia, avatar, idchap, tenchap, tenfileurl;

    private Database db;
    private DbTaiTruyen dl;
    private ArrayList<String> arrId = new ArrayList<>();

    private String zipFile, unzipLocation;

    public DownloadService() {
        super("Download Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private int totalFileSize;

    @Override
    protected void onHandleIntent(Intent intent) {

        db = new Database(getApplication());
        file_url = intent.getBundleExtra("keychapdownload").getString("zipchap");
        idtruyen = intent.getBundleExtra("keychapdownload").getString("idtruyen");
        tentruyen = intent.getBundleExtra("keychapdownload").getString("tentruyen");
        tacgia = intent.getBundleExtra("keychapdownload").getString("tacgia");
        avatar = intent.getBundleExtra("keychapdownload").getString("avatar");
        idchap = intent.getBundleExtra("keychapdownload").getString("idchap");
        tenchap = intent.getBundleExtra("keychapdownload").getString("tenchap");

        tenfileurl = file_url.replace("http://m.sieuhack.mobi/", "");

        //Log.d("zip chap: ", file_url);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle(tentruyen + " - Chap " + idchap + ": " + tenchap + ".zip")
                .setContentText("Đã Tải Xong. Bạn Có Thể Đọc Offline Rồi :)")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

        initDownload();

    }

    private void initDownload() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://m.sieuhack.mobi/")
                .build();

        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);

        Call<ResponseBody> request = retrofitInterface.downloadFile(tenfileurl);
        try {
            downloadFile(request.execute().body());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public interface RetrofitInterface {
        @Streaming
        @GET
        Call<ResponseBody> downloadFile(@Url String fileUrl);
    }

    private void downloadFile(ResponseBody body) throws IOException {

        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();

        //Tạo thư mục
        File folder = new File(Environment.getExternalStorageDirectory() + "/TruyenTranhVL");
        boolean success = true;
        if (!folder.exists()) {
            //Toast.makeText(MainActivity.this, "Directory Does Not Exist, Create It", Toast.LENGTH_SHORT).show();
            success = folder.mkdir();
        }
        if (success) {
            //Toast.makeText(MainActivity.this, "Directory Created", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(MainActivity.this, "Failed - Error", Toast.LENGTH_SHORT).show();
        }

        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStorageDirectory(), "/TruyenTranhVL/" + tentruyen + " - Chap " + idchap + ": " + tenchap + ".zip");
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            Download download = new Download();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();

    }

    private void sendNotification(Download download) {

        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(String.format("Đã Tải Được %d/%d MB", download.getCurrentFileSize(), download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendIntent(Download download) {

        Intent intent = new Intent(ChapFragment.MESSAGE_PROGRESS);
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete() {

        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Đã Tải Xong. Bạn Có Thể Đọc Offline Rồi :)");
        notificationManager.notify(0, notificationBuilder.build());

        Calendar cal = Calendar.getInstance();
        long time = cal.getTimeInMillis();
        dl = new DbTaiTruyen(idtruyen, tentruyen, tacgia, avatar, idchap, tenchap, time);
        if (!db.getIdTaiTruyen(idtruyen, idchap)) {
            db.addTaiTruyen(dl);
        }
        //Toast.makeText(getApplication(), idtruyen+"-"+idchap+"-"+tenchap+"-"+time, Toast.LENGTH_SHORT).show();

        zipFile = Environment.getExternalStorageDirectory() + "/TruyenTranhVL/" + tentruyen + " - Chap " + idchap + ": " + tenchap + ".zip";
        unzipLocation = Environment.getExternalStorageDirectory() + "/TruyenTranhVL/" + tentruyen + " - Chap " + idchap + ": " + tenchap + "/";

        try {
            unzip();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

    public void unzip() throws IOException {
        new UnZipTask().execute(zipFile, unzipLocation);
    }

    private class UnZipTask extends AsyncTask<String, Void, Boolean> {
        @SuppressWarnings("rawtypes")
        @Override
        protected Boolean doInBackground(String... params) {
            String filePath = params[0];
            String destinationPath = params[1];

            File archive = new File(filePath);
            try {
                ZipFile zipfile = new ZipFile(archive);
                for (Enumeration e = zipfile.entries(); e.hasMoreElements(); ) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    unzipEntry(zipfile, entry, destinationPath);
                }


                Decompress d = new Decompress(zipFile, unzipLocation);
                d.unzip();

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }


        private void unzipEntry(ZipFile zipfile, ZipEntry entry, String outputDir) throws IOException {

            if (entry.isDirectory()) {
                createDir(new File(outputDir, entry.getName()));
                return;
            }

            File outputFile = new File(outputDir, entry.getName());
            if (!outputFile.getParentFile().exists()) {
                createDir(outputFile.getParentFile());
            }

            // Log.v("", "Extracting: " + entry);
            BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

            try {

            } finally {
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            }
        }

        private void createDir(File dir) {
            if (dir.exists()) {
                return;
            }
            if (!dir.mkdirs()) {
                throw new RuntimeException("Can not create dir " + dir);
            }
        }
    }

}
