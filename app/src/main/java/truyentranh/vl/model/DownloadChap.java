package truyentranh.vl.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import truyentranh.vl.R;
import truyentranh.vl.activity.MainActivity;
import truyentranh.vl.database.Database;

public class DownloadChap extends Activity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;

    // File url to download
    private static String file_url;

    private String idtruyen, tentruyen, tacgia, avatar, idchap, tenchap;
    private Database db;
    private DbTaiTruyen dl;
    private ArrayList<String> arrId = new ArrayList<>();

    private String zipFile, unzipLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new Database(getApplication());
        file_url = getIntent().getBundleExtra("keychapdownload").getString("zipchap");
        idtruyen = getIntent().getBundleExtra("keychapdownload").getString("idtruyen");
        tentruyen = getIntent().getBundleExtra("keychapdownload").getString("tentruyen");
        tacgia = getIntent().getBundleExtra("keychapdownload").getString("tacgia");
        avatar = getIntent().getBundleExtra("keychapdownload").getString("avatar");
        idchap = getIntent().getBundleExtra("keychapdownload").getString("idchap");
        tenchap = getIntent().getBundleExtra("keychapdownload").getString("tenchap");

        new DownloadFileFromURL().execute(file_url);

        //Toast.makeText(getApplicationContext(), tacgia+"-"+tentruyen, Toast.LENGTH_LONG).show();

    }

    /**
     * Showing Dialog
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Đang tải. Xin chờ...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {

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

                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/TruyenTranhVL/" + tentruyen + " - Chap " + idchap + ": " + tenchap + ".zip");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                result = "true";
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

            Calendar cal = Calendar.getInstance();
            long time = cal.getTimeInMillis();
            dl = new DbTaiTruyen(idtruyen, tentruyen, tacgia, avatar, idchap, tenchap, time);
            if (!db.getIdTaiTruyen(idtruyen, idchap)) {
                db.addTaiTruyen(dl);
            }
            //Toast.makeText(getApplication(), idtruyen+"-"+idchap+"-"+tenchap+"-"+time, Toast.LENGTH_SHORT).show();

            zipFile = Environment.getExternalStorageDirectory() + "/TruyenTranhVL/" + tentruyen + " - Chap " + idchap + ": " + tenchap + ".zip";
            unzipLocation = Environment.getExternalStorageDirectory() + "/TruyenTranhVL/" + tentruyen + " - Chap " + idchap + ": " + tenchap + "/";

            if (result.equalsIgnoreCase("true")) {
                try {
                    unzip();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {

            }

        }

    }

    public void unzip() throws IOException {
        pDialog = new ProgressDialog(DownloadChap.this);
        pDialog.setMessage("Đang xử lý chap...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(false);
        pDialog.show();
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
            pDialog.dismiss();

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