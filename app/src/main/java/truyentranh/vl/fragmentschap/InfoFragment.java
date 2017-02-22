package truyentranh.vl.fragmentschap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import truyentranh.vl.R;
import truyentranh.vl.adapter.LvChap;
import truyentranh.vl.model.InfoItem;

public class InfoFragment extends Fragment {

    private ListView lvChap;
    private ArrayList<InfoItem> arrItem = new ArrayList<>();
    private LvChap adapter = null;
    private String idtruyen, sochap, nhanbiet;
    private ImageView tvAvatarInfo;
    private TextView tvTenTruyen, tvTenKhac, tvTacGia, tvChap, tvTinhTrang, tvMoTa;
    private InfoItem infoitem;

    private SmoothProgressBar mGoogleNow;

    private ImageView ivThich;
    private TextView tvThich;
    private String thich;

    public InfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info,
                container, false);

        tvAvatarInfo = (ImageView) rootView.findViewById(R.id.tvAvatarInfo);
        tvTenTruyen = (TextView) rootView.findViewById(R.id.tvTenTruyen);
        tvTenKhac = (TextView) rootView.findViewById(R.id.tvTenKhac);
        tvTacGia = (TextView) rootView.findViewById(R.id.tvTacGia);
        tvChap = (TextView) rootView.findViewById(R.id.tvChap);
        tvTinhTrang = (TextView) rootView.findViewById(R.id.tvTinhTrang);
        tvMoTa = (TextView) rootView.findViewById(R.id.tvMoTa);

        idtruyen = getActivity().getIntent().getBundleExtra("key").getString("id");
        sochap = getActivity().getIntent().getBundleExtra("key").getString("sochap");
        nhanbiet = getActivity().getIntent().getBundleExtra("key").getString("nhanbiet");

        mGoogleNow = (SmoothProgressBar) rootView.findViewById(R.id.google_now);

        ivThich = (ImageView) rootView.findViewById(R.id.ivThich);
        tvThich = (TextView) rootView.findViewById(R.id.tvThich);

        //Json
        try {
            getJSONData();
        } catch (Exception e) {
        }

        ivThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Like.class);
                Bundle bundle = new Bundle();
                bundle.putString("idtruyen", (Integer.parseInt(idtruyen) + 1) + "");
                bundle.putString("tentruyen", infoitem.getTentruyen());
                bundle.putString("trang", "1");
                intent.putExtra("key", bundle);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void getJSONData() {
        new GetJSONAsync().execute();
    }

    class GetJSONAsync extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            //Chuẩn bị thực thi
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL("http://m.sieuhack.mobi/json.php");
                URLConnection conn = url.openConnection();
                InputStream is = conn.getInputStream();
                InputStreamReader inreader = new InputStreamReader(is);
                BufferedReader bufreader = new BufferedReader(inreader);
                StringBuilder builder = new StringBuilder();

                String data;
                while ((data = bufreader.readLine()) != null) {
                    builder.append(data);
                    builder.append("\n");
                }

                JSONArray jsonArray = new JSONArray(builder.toString());
                JSONObject jObject = jsonArray.getJSONObject(Integer.valueOf(idtruyen.toString().trim()));
                String id = jObject.getString("id");
                thich = jObject.getString("thich");
                String avatarinfo = String.valueOf(jObject.getString("avatarinfo"));
                String tentruyen = String.valueOf(jObject.getString("tentruyen"));
                String tenkhac = String.valueOf(jObject.getString("tenkhac"));
                String tacgia = String.valueOf(jObject.getString("tacgia"));
                String tinhtrang = String.valueOf(jObject.getString("tinhtrang"));
                String mota = String.valueOf(jObject.getString("mota"));
                infoitem = new InfoItem(avatarinfo, tentruyen, tenkhac, tacgia, tinhtrang, sochap, mota);
            } catch (Exception e) {
                Log.e("MY_WATCH", "Lỗi: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Ẩn loading
            mGoogleNow.progressiveStop();
            mGoogleNow.setVisibility(View.GONE);
            try {

                ivThich.setVisibility(View.VISIBLE);
                tvThich.setVisibility(View.VISIBLE);
                tvThich.setText(thich);

                new ImageLoadTask(infoitem.getAvatar(), tvAvatarInfo).execute();
                tvTenTruyen.setText(infoitem.getTentruyen());
                tvTenKhac.setText(infoitem.getTenkhac());
                tvTacGia.setText(infoitem.getTacgia());
                tvChap.setText(infoitem.getSochap());
                tvTinhTrang.setText(infoitem.getTinhtrang());
                tvMoTa.setText(infoitem.getMota());
            } catch (Exception e) {
            }
        }
    }

    class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}
