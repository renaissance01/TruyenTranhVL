package truyentranh.vl.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import truyentranh.vl.R;
import truyentranh.vl.activity.ChapActivity;
import truyentranh.vl.adapter.LvManga;
import truyentranh.vl.database.Database;
import truyentranh.vl.model.DbItem;
import truyentranh.vl.model.LvMangaItem;

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<LvMangaItem> arrItem = new ArrayList<>();
    private LvManga adapter = null;
    private ListView lvManga = null;
    private LvMangaItem lvMangaItem;

    private Database db;
    private ArrayList<String> arrId = new ArrayList<>();
    private TextView tvHistory, tvHistory2,tvSoTruyen;

    //Refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    private SmoothProgressBar mGoogleNow;

    public HistoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history,
                container, false);
        //Khởi tạo item xóa lịch sử
        setHasOptionsMenu(true);

        //Xử lý database;
        db = new Database(getActivity());
        arrId = db.getLichSu();

        //Truyện
        lvManga = (ListView) rootView.findViewById(R.id.lvManga);

        try {
            //Json
            getJSONData();
        } catch (Exception e) {
        }

        tvHistory = (TextView)rootView.findViewById(R.id.tvHistory);
        tvHistory2 = (TextView)rootView.findViewById(R.id.tvHistory2);
        tvSoTruyen = (TextView)rootView.findViewById(R.id.tvSoTruyen);

        mGoogleNow = (SmoothProgressBar) rootView.findViewById(R.id.google_now);

        if (arrItem.size() > 0) {
            arrItem.clear();
            tvHistory.setVisibility(View.GONE);
            tvHistory2.setVisibility(View.GONE);
        }

        //Refresh
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.doNhe);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
        );

        //Item listview khi click
        lvManga.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Lấy ngày tháng hiện tại kiểu long
                Calendar cal = Calendar.getInstance();
                long time = cal.getTimeInMillis();
                if (!db.getId(arrItem.get(position).getId())) {
                    DbItem item = new DbItem(arrItem.get(position).getId(), "1", "2", "3", time, "1");
                    db.addTruyen(item);
                } else {
                    db.updateTruyen("lichsu", arrItem.get(position).getId(), 1, time);
                    //Toast.makeText(getActivity(), db.getCount(2, arrItem.get(position).getId()) + " -- ", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getActivity(), ChapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(Integer.valueOf(arrItem.get(position).getId()) - 1));
                bundle.putString("tentruyen", String.valueOf(arrItem.get(position).getTentruyen()));
                bundle.putString("tacgia", String.valueOf(arrItem.get(position).getTacgia()));
                bundle.putString("avatar", String.valueOf(arrItem.get(position).getAvatar()));
                bundle.putString("sochap", String.valueOf(arrItem.get(position).getChuong()));
                bundle.putString("nhanbiet", "activity");
                bundle.putString("tab", "2");
                intent.putExtra("key", bundle);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_delete){
            if (arrItem.size() > 0) {
                db.xoaLichSu();
                arrItem.clear();
                adapter = new LvManga(getActivity(), R.layout.manga_listview, arrItem);
                lvManga.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                tvSoTruyen.setText("0 Truyện");
                Toast.makeText(getActivity(), "Xóa Thành Công!", Toast.LENGTH_SHORT).show();

                //Refresh fragment
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }else{
                Toast.makeText(getActivity(), "Lịch Sử Rỗng!", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
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

                for (int i = 0; i < arrId.size(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(Integer.valueOf(arrId.get(i))-1);
                    JSONArray jsonArray2 = jObject.optJSONArray("chap");

                    String id = jObject.getString("id");
                    String avatar = String.valueOf(jObject.getString("avatar"));
                    String tentruyen = String.valueOf(jObject.getString("tentruyen"));
                    String tacgia = String.valueOf(jObject.getString("tacgia"));
                    String luotxem = String.valueOf(jObject.getString("luotxem"));

                    //if (db.getId(id))
                        lvMangaItem = new LvMangaItem(id, avatar, tentruyen, tacgia, jsonArray2.length() + "", luotxem);
                    //else
                        //lvMangaItem = new LvMangaItem(id, avatar, tentruyen, tacgia, jsonArray2.length() + "", "0");
                    arrItem.add(lvMangaItem);
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
                tvSoTruyen.setVisibility(View.VISIBLE);
                adapter = new LvManga(getActivity(), R.layout.manga_listview, arrItem);
                lvManga.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
            if(arrItem.size() == 0){
                tvHistory.setVisibility(View.VISIBLE);
                tvHistory2.setVisibility(View.VISIBLE);
            }
            tvSoTruyen.setText(arrItem.size() + " Truyện");
            swipeRefreshLayout.setRefreshing(false);
        }

    }

}
