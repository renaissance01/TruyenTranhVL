package truyentranh.vl.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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

import truyentranh.vl.R;
import truyentranh.vl.activity.ChapActivity;
import truyentranh.vl.adapter.LvManga;
import truyentranh.vl.database.Database;
import truyentranh.vl.model.DbItem;
import truyentranh.vl.model.LvMangaItem;

public class FavoriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<LvMangaItem> arrItem = new ArrayList<>();
    private LvManga adapter = null;
    private ListView lvManga = null;
    private LvMangaItem lvMangaItem;

    private Database db;
    private ArrayList<String> arrId = new ArrayList<>();
    private TextView tvYeuThich1, tvYeuThich2, tvSoTruyen;

    //Refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    public FavoriteFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorite,
                container, false);

        //Xử lý database;
        db = new Database(getActivity());
        arrId = db.getDaThich();

        //Truyện
        lvManga = (ListView) rootView.findViewById(R.id.lvManga);

        try {
            //Json
            getJSONData();
        } catch (Exception e) {
        }

        tvYeuThich1 = (TextView)rootView.findViewById(R.id.tvYeuThich1);
        tvYeuThich2 = (TextView)rootView.findViewById(R.id.tvYeuThich2);
        tvSoTruyen = (TextView)rootView.findViewById(R.id.tvSoTruyen);
        if (arrItem.size() > 0) {
            arrItem.clear();
            tvYeuThich1.setVisibility(View.GONE);
            tvYeuThich2.setVisibility(View.GONE);
        }

        //Refresh
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
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
                bundle.putString("tab", "1");
                intent.putExtra("key", bundle);
                startActivity(intent);
            }
        });

        //Item listview khi ấn giữ
        lvManga.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int idxoa = position;
                AlertDialog.Builder b=new AlertDialog.Builder(getActivity());
                b.setTitle("Lựa Chọn");
                b.setMessage("Bạn có muốn xóa " + arrItem.get(position).getTentruyen() + " không?");
                b.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.xoaYeuThich(arrItem.get(idxoa).getId());
                        arrItem.remove(idxoa);
                        adapter = new LvManga(getActivity(), R.layout.manga_listview, arrItem);
                        lvManga.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        tvSoTruyen.setText(arrItem.size() + " Truyện");
                        Toast.makeText(getActivity(), "Xóa Thành Công!", Toast.LENGTH_SHORT).show();
                        arrItem.clear();
                        //Refresh fragment
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(FavoriteFragment.this).attach(FavoriteFragment.this).commit();
                    }
                });

                b.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                });
                b.create().show();
                return true;
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
                    JSONObject jObject = jsonArray.getJSONObject(Integer.valueOf(arrId.get(i)) - 1);
                    JSONArray jsonArray2 = jObject.optJSONArray("chap");

                    String id = String.valueOf(jObject.getString("id"));
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
            try {
                tvSoTruyen.setVisibility(View.VISIBLE);
                adapter = new LvManga(getActivity(), R.layout.manga_listview, arrItem);
                lvManga.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
            if(arrItem.size() == 0){
                tvYeuThich1.setVisibility(View.VISIBLE);
                tvYeuThich2.setVisibility(View.VISIBLE);
            }
            tvSoTruyen.setText(arrItem.size() + " Truyện");
            swipeRefreshLayout.setRefreshing(false);
        }

    }

}
