package truyentranh.vl.fragmentschap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import truyentranh.vl.R;
import truyentranh.vl.activity.DialogDownload;
import truyentranh.vl.adapter.LvChap;
import truyentranh.vl.model.LvChapItem;
import truyentranh.vl.slideimages.ShowImages;

public class ChapFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView lvChap;
    private ArrayList<LvChapItem> arrItem = new ArrayList<>();
    private LvChap adapter = null;
    String idtruyen, tentruyen, tacgia, avatar, nhanbiet, idchap, tenchap, zipchap, tab;

   /* private ImageView ivThich;
    private TextView tvThich;
    private String thich;*/

    //Refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    public static final String MESSAGE_PROGRESS = "message_progress";

    private SmoothProgressBar mGoogleNow;

    public ChapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chap,
                container, false);

        //Download Chap
        ButterKnife.bind(getActivity());

        lvChap = (ListView) rootView.findViewById(R.id.lvChap);
        /*ivThich = (ImageView) rootView.findViewById(R.id.ivThich);
        tvThich = (TextView) rootView.findViewById(R.id.tvThich);*/

        idtruyen = getActivity().getIntent().getBundleExtra("key").getString("id");
        tentruyen = getActivity().getIntent().getBundleExtra("key").getString("tentruyen");
        tacgia = getActivity().getIntent().getBundleExtra("key").getString("tacgia");
        avatar = getActivity().getIntent().getBundleExtra("key").getString("avatar");
        nhanbiet = getActivity().getIntent().getBundleExtra("key").getString("nhanbiet");
        tab = getActivity().getIntent().getBundleExtra("key").getString("tab");

        mGoogleNow = (SmoothProgressBar) rootView.findViewById(R.id.google_now);

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

        lvChap.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ShowImages.class);
                Bundle bundle = new Bundle();
                bundle.putString("idtruyen", idtruyen);
                bundle.putString("idchap", String.valueOf(Integer.valueOf(arrItem.get(position).getIdchap()) - 1));
                bundle.putString("tentruyen", tentruyen);
                bundle.putString("tenchap", String.valueOf(arrItem.get(position).getTenchap()));
                bundle.putString("trang", "0");
                bundle.putString("tab", tab);
                bundle.putString("checktaitruyen", "check");
                intent.putExtra("keychap", bundle);
                startActivity(intent);
            }
        });

        try {
            //Json
            getJSONData();
        } catch (Exception e) {
        }

        if (arrItem.size() > 0) {
            arrItem.clear();
        }

        lvChap.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                idchap = String.valueOf(Integer.valueOf(arrItem.get(position).getIdchap()));
                tenchap = String.valueOf(arrItem.get(position).getTenchap());
                zipchap = String.valueOf(arrItem.get(position).getZipchap());

                Intent intent = new Intent(getActivity(), DialogDownload.class);
                Bundle bundle = new Bundle();
                bundle.putString("idtruyen", idtruyen);
                bundle.putString("idchap", String.valueOf(Integer.valueOf(arrItem.get(position).getIdchap()) - 1));
                bundle.putString("tentruyen", tentruyen);
                bundle.putString("tenchap", String.valueOf(arrItem.get(position).getTenchap()));
                intent.putExtra("key", bundle);
                startActivityForResult(intent, 1);

                return true;
            }
        });

        /*ivThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Like.class);
                Bundle bundle = new Bundle();
                bundle.putString("idtruyen", (Integer.parseInt(idtruyen) + 1) + "");
                bundle.putString("tentruyen", tentruyen);
                intent.putExtra("key", bundle);
                startActivity(intent);
            }
        });*/

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

            if (arrItem.size() > 0) {
                arrItem.clear();
            }

            //Thêm Lượt Xem
            try {
                URL url = new URL("http://m.sieuhack.mobi/them-luotxem.php");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("id", Integer.parseInt(idtruyen) + 1);
                postDataParams.put("luotxem", "1");

                Log.e("Dữ Liệu:",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                }
            } catch (Exception e) {
            }

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

                JSONArray jsonArray2 = jObject.optJSONArray("chap");

                //thich = jObject.getString("thich");

                for (int j = 0; j < jsonArray2.length(); j++) {
                    JSONObject jObject2 = jsonArray2.getJSONObject(j);
                    String idchap = String.valueOf(jObject2.optString("id"));
                    String chapten = String.valueOf(jObject2.optString("chap" + String.valueOf(j + 1)));
                    String zipchap = String.valueOf(jObject2.optString("zipchap" + String.valueOf(j + 1)));
                    String chapngay = String.valueOf(jObject2.optString("ngay"));
                    arrItem.add(new LvChapItem(idtruyen, idchap, chapten, chapngay, zipchap));
                }

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
                /*ivThich.setVisibility(View.VISIBLE);
                tvThich.setVisibility(View.VISIBLE);
                tvThich.setText(thich);*/
                adapter = new LvChap(getActivity(), R.layout.chap_listview, arrItem);
                lvChap.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
        }

    }

    public String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> itr = params.keys();
        while (itr.hasNext()) {
            String key = itr.next();
            Object value = params.get(key);
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String countryCode = data.getStringExtra(DialogDownload.RESULT_CONTRYCODE);
            String id = data.getStringExtra(DialogDownload.RESULT_ID);

            if (id.equals("0")) {
                Intent intent = new Intent(getActivity(), DownloadService.class);
                Bundle bundle = new Bundle();
                bundle.putString("idtruyen", String.valueOf(Integer.valueOf(idtruyen) + 1));
                bundle.putString("tentruyen", tentruyen);
                bundle.putString("tacgia", tacgia);
                bundle.putString("avatar", avatar);
                bundle.putString("idchap", String.valueOf(Integer.valueOf(idchap)));
                bundle.putString("tenchap", tenchap);
                bundle.putString("zipchap", zipchap);
                intent.putExtra("keychapdownload", bundle);
                getActivity().startService(intent);
            }
        }
    }

}
