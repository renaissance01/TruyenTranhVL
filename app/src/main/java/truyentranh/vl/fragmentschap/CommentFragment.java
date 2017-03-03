package truyentranh.vl.fragmentschap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import truyentranh.vl.R;
import truyentranh.vl.adapter.LvComment;
import truyentranh.vl.adapter.LvCommentItem;

public class CommentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView lvComment;
    private ArrayList<LvCommentItem> arrItem = new ArrayList<>();
    private LvComment adapter = null;
    private String idtruyen, tentruyen, sochap, nhanbiet, tab;
    private ImageView tvAvatarInfo;
    private LvCommentItem lvCommentItem;
    private ImageView btnGui;
    private EditText txtNoiDung;
    private TextView tvBinhLuan;
    //Session lưu tài khoản đăng nhập

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "UserPrefs";
    public static final String HoTenKey = "hotenKey";

    //Refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    private SmoothProgressBar mGoogleNow;

    public CommentFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comment,
                container, false);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String hoten = sharedpreferences.getString(HoTenKey, null);

        lvComment = (ListView) rootView.findViewById(R.id.lvComment);
        btnGui = (ImageView) rootView.findViewById(R.id.btnGui);
        txtNoiDung = (EditText) rootView.findViewById(R.id.txtNoiDung);
        tvBinhLuan = (TextView) rootView.findViewById(R.id.tvBinhLuan);

        /*
        //Set màu cho edittext
        Drawable drawable = txtNoiDung.getBackground();
        drawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);

        if(Build.VERSION.SDK_INT > 16) {
            txtNoiDung.setBackground(drawable);
        }else{
            txtNoiDung.setBackgroundDrawable(drawable);
        }*/

        idtruyen = getActivity().getIntent().getBundleExtra("key").getString("id");
        tentruyen = getActivity().getIntent().getBundleExtra("key").getString("tentruyen");
        sochap = getActivity().getIntent().getBundleExtra("key").getString("sochap");
        nhanbiet = getActivity().getIntent().getBundleExtra("key").getString("nhanbiet");
        tab = getActivity().getIntent().getBundleExtra("key").getString("tab");

        mGoogleNow = (SmoothProgressBar) rootView.findViewById(R.id.google_now);

        //Json
        try {
            getJSONData();
        } catch (Exception e) {
        }

        if (arrItem.size() > 0) {
            arrItem.clear();
        }

        //Refresh
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_blue_bright);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
        );

        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNoiDung.getText().toString().trim().equals("")) {
                    txtNoiDung.setError("Không được để trống!");
                } else {
                    Intent intent = new Intent(getActivity(), Comment.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("idtruyen", (Integer.parseInt(idtruyen) + 1) + "");
                    bundle.putString("tentruyen", tentruyen);
                    bundle.putString("noidung", txtNoiDung.getText().toString());
                    bundle.putString("tab", tab);
                    intent.putExtra("key", bundle);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    public void getJSONData() {
        new GetJSONAsync().execute();
    }

    @Override
    public void onRefresh() {
        //Refresh fragment
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
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
                String idtruyen2 = (Integer.parseInt(idtruyen) + 1) + "";
                URL url = new URL("http://m.sieuhack.mobi/json-comment.php?id=" + idtruyen2);
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
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    String hoten = String.valueOf(jObject.getString("hoten"));

                    String thoigian = String.valueOf(jObject.getString("thoigian"));

                    SimpleDateFormat format = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.UK);
                    String dateString = format.format(new Date(Long.parseLong(thoigian)));

                    String noidung = String.valueOf(jObject.getString("noidung"));
                    arrItem.add(new LvCommentItem(i + 1 + "", hoten, dateString, noidung));
                }
            } catch (Exception e) {
                Log.e("MY_WATCH", "Lỗi CommentFragment: " + e.toString());
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
                /*new ImageLoadTask(infoitem.getAvatar(), tvAvatarInfo).execute();
                tvTenTruyen.setText(infoitem.getTentruyen());*/
                tvBinhLuan.setText("Bình Luận (" + arrItem.size() + ")");
                adapter = new LvComment(getActivity(), R.layout.comment_listview, arrItem);
                lvComment.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
            swipeRefreshLayout.setRefreshing(false);
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
