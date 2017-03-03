package truyentranh.vl.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
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
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import truyentranh.vl.R;
import truyentranh.vl.activity.ChapActivity;
import truyentranh.vl.activity.ChooseActivity;
import truyentranh.vl.activity.MainActivity;
import truyentranh.vl.activity.TheLoaiActivitry;
import truyentranh.vl.adapter.LvManga;
import truyentranh.vl.database.Database;
import truyentranh.vl.model.DbItem;
import truyentranh.vl.model.LvMangaItem;

public class MangaFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<LvMangaItem> arrItem = new ArrayList<>();
    private LvManga adapter = null;
    private ListView lvManga = null;
    private LvMangaItem lvMangaItem;
    private TextView tvManga, tvSoTruyen;
    private Database db;

    //Refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    private SmoothProgressBar mGoogleNow;

    private MyReceiver r;

    //Session lưu thể loại truyện
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "TheLoaiPrefs";
    //private List<String> arrTheLoai = new ArrayList<>();
    private int tongtheloaiKey = 0;

    public MangaFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manga,
                container, false);

        //Khởi tạo item lọc truyện
        setHasOptionsMenu(true);

        //Xử lý database;
        db = new Database(getActivity());

        //Truyện
        lvManga = (ListView) rootView.findViewById(R.id.lvManga);

        /*for (int i = 0; i < tongtheloaiKey; i++) {
            try {
                arrTheLoai.add(sharedpreferences.getString("theloaiKey" + i, "TamPro"));
                Toast.makeText(getActivity(), arrTheLoai.get(0) + "", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
            }
        }*/

        try {
            //Json
            getJSONData();
        } catch (Exception e) {
        }

        tvManga = (TextView) rootView.findViewById(R.id.tvManga);
        tvSoTruyen = (TextView) rootView.findViewById(R.id.tvSoTruyen);

        mGoogleNow = (SmoothProgressBar) rootView.findViewById(R.id.google_now);

        if (arrItem.size() > 0) {
            arrItem.clear();
            tvManga.setVisibility(View.VISIBLE);
        } else {
            tvManga.setVisibility(View.GONE);
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

        //Item listview khi click
        lvManga.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

/*                for (int i = 0; i < lvManga.getChildCount(); i++) {
                    if (position == i) {
                        lvManga.getChildAt(i).setBackgroundResource(R.color.paleturquoise);
                    } else {
                        lvManga.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
                    }
                }*/

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
                bundle.putString("tab", "0");
                intent.putExtra("key", bundle);
                startActivity(intent);
            }
        });

        lvManga.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), ChooseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", arrItem.get(position).getId());
                intent.putExtra("key", bundle);
                startActivityForResult(intent, 1);

                return true;
            }
        });

        /*lvManga.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;
            boolean hideToolBar = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (hideToolBar) {
                    ((MainActivity) getActivity()).getSupportActionBar().hide();
                } else {
                    ((MainActivity) getActivity()).getSupportActionBar().show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    hideToolBar = true;
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    hideToolBar = false;
                }
                mLastFirstVisibleItem = firstVisibleItem;

            }
        });*/

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_loctruyen, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_loctruyen) {
            startActivity(new Intent(getActivity(), TheLoaiActivitry.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String countryCode = data.getStringExtra(ChooseActivity.RESULT_CONTRYCODE);
            String id = data.getStringExtra(ChooseActivity.RESULT_ID);

            if (id.equals("0")) {
                Calendar cal = Calendar.getInstance();
                long time = cal.getTimeInMillis();
                if (!db.getId(countryCode)) {
                    DbItem item = new DbItem(countryCode, "1", "2", "3", time, "1");
                    db.addYeuThich(item);
                }
                db.updateYeuThich(countryCode, time);
                Toast.makeText(getActivity(), "Thêm Thành Công!", Toast.LENGTH_SHORT).show();
                /*Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);*/
            } else if (id.equals("1")) {

            }
        }
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

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    JSONArray jsonArray2 = jObject.optJSONArray("chap");

                    String id = jObject.getString("id");
                    String avatar = String.valueOf(jObject.getString("avatar"));
                    String tentruyen = String.valueOf(jObject.getString("tentruyen"));
                    String tacgia = String.valueOf(jObject.getString("tacgia"));
                    String luotxem = String.valueOf(jObject.getString("luotxem"));
                    String theloai = String.valueOf(jObject.getString("theloai"));

                    sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    tongtheloaiKey = sharedpreferences.getInt("tongtheloaiKey", 0);
                    try {
                        if (tongtheloaiKey != 0) {
                            String theloai2 = null;
                            for (int j = 0; j < tongtheloaiKey; j++) {
                                try {
                                    theloai2 = sharedpreferences.getString("theloaiKey" + j, "TamPro");
                                } catch (Exception e) {
                                    theloai2 = "TamPro";
                                }
                                if (theloai.equals(theloai2)) {
                                    lvMangaItem = new LvMangaItem(id, avatar, tentruyen, tacgia, jsonArray2.length() + "", luotxem);
                                    arrItem.add(lvMangaItem);
                                }
                            }
                        } else {
                            lvMangaItem = new LvMangaItem(id, avatar, tentruyen, tacgia, jsonArray2.length() + "", luotxem);
                            arrItem.add(lvMangaItem);
                        }
                    } catch (Exception e) {
                        lvMangaItem = new LvMangaItem(id, avatar, tentruyen, tacgia, jsonArray2.length() + "", luotxem);
                        arrItem.add(lvMangaItem);
                    }
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
            if (arrItem.size() == 0) {
                tvManga.setText("CHƯA CÓ TRUYỆN");
                tvManga.setVisibility(View.VISIBLE);
            } else {
                tvManga.setVisibility(View.GONE);
            }
            tvSoTruyen.setText(arrItem.size() + " Truyện");
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(r,
                new IntentFilter("TAB_TRANGCHU"));
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh();
        }
    }
}

