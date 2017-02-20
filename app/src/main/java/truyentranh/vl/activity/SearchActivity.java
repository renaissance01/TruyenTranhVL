package truyentranh.vl.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
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
import truyentranh.vl.adapter.LvSearch;
import truyentranh.vl.database.Database;
import truyentranh.vl.model.DbItem;
import truyentranh.vl.model.LvMangaItem;

public class SearchActivity extends AppCompatActivity  implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private FragmentDrawer drawerFragment;
    private ArrayList<LvMangaItem> countryList = new ArrayList<LvMangaItem>();
    private LvMangaItem lvMangaItem;
    private LvSearch dataAdapter;
    private Database db;
    private  ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tìm Kiếm");

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        listView = (ListView) findViewById(R.id.listView1);

        //Xử lý database;
        db = new Database(this);
        //Json
        try {
            getJSONData();
        }catch (Exception e){}

        //Generate list View from ArrayList
        try {
            displayListView();
        }
        catch (Exception e){}

        //Item listview khi click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Lấy ngày tháng hiện tại kiểu long
                Calendar cal = Calendar.getInstance();
                long time = cal.getTimeInMillis();
                if (!db.getId(countryList.get(position).getId())) {
                    DbItem item = new DbItem(countryList.get(position).getId(), "1", "2", "3", time, "1");
                    db.addTruyen(item);
                } else {
                    db.updateTruyen("lichsu", countryList.get(position).getId(), 1, time);
                    //Toast.makeText(getActivity(), db.getCount(2, arrItem.get(position).getId()) + " -- ", Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplication(), ChapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", String.valueOf(Integer.valueOf(countryList.get(position).getId()) - 1));
                bundle.putString("tentruyen", String.valueOf(countryList.get(position).getTentruyen()));
                bundle.putString("tacgia", String.valueOf(countryList.get(position).getTacgia()));
                bundle.putString("avatar", String.valueOf(countryList.get(position).getAvatar()));
                bundle.putString("sochap", String.valueOf(countryList.get(position).getChuong()));
                bundle.putString("tab", "search");
                bundle.putString("nhanbiet", "activity");
                intent.putExtra("key", bundle);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplication(), CountrycodeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", countryList.get(position).getId());
                intent.putExtra("key", bundle);
                startActivityForResult(intent, 1);

                return true;
            }
        });

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String countryCode = data.getStringExtra(CountrycodeActivity.RESULT_CONTRYCODE);
            String id = data.getStringExtra(CountrycodeActivity.RESULT_ID);

            if (id.equals("0")) {
                Calendar cal = Calendar.getInstance();
                long time = cal.getTimeInMillis();
                if (!db.getId(countryCode)) {
                    DbItem item = new DbItem(countryCode, "1", "2", "3", time, "1");
                    db.addYeuThich(item);
                }
                db.updateYeuThich(countryCode, time);
                Toast.makeText(getApplication(), "Thêm Thành Công!", Toast.LENGTH_SHORT).show();
            } else if (id.equals("1")) {

            }
        }
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

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jObject = jsonArray.getJSONObject(i);
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
                    countryList.add(lvMangaItem);
                }

            } catch (Exception e) {
                Log.e("MY_WATCH", "Lỗi: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataAdapter = new LvSearch(getApplicationContext(),
                    R.layout.manga_listview, countryList);
            listView.setAdapter(dataAdapter);
            dataAdapter.notifyDataSetChanged();
            listView.setTextFilterEnabled(true);
        }

    }

    private void displayListView() {
/*        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                LvMangaItem country = (LvMangaItem) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        country.getTentruyen(), Toast.LENGTH_SHORT).show();
            }
        });*/

        EditText myFilter = (EditText) findViewById(R.id.myFilter);
        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    dataAdapter.getFilter().filter(s.toString());
                }
                catch (Exception e){}
            }
        });

    }

}