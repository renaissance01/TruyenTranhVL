package truyentranh.vl.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import java.util.List;

import truyentranh.vl.R;
import truyentranh.vl.adapter.LvManga;
import truyentranh.vl.adapter.TheLoaiAdapter;
import truyentranh.vl.fragments.MangaFragment;
import truyentranh.vl.model.LvMangaItem;
import truyentranh.vl.model.LvTheLoai;

public class TheLoaiActivitry extends AppCompatActivity {

    private ArrayList<LvTheLoai> arrTheLoai = new ArrayList<>();
    private TheLoaiAdapter theLoaiAdapter;
    private ListView lvTheLoai;
    private EditText timTheLoai;
    private Button btnLuu;

    //Lưu thể loại truyện
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "TheLoaiPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitry_theloai);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            //Json
            getJSONData();
        } catch (Exception e) {
        }

        timTheLoai = (EditText) findViewById(R.id.timTheLoai);
        lvTheLoai = (ListView) findViewById(R.id.lvTheLoai);
        btnLuu = (Button) findViewById(R.id.btnLuu);


        timTheLoai.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = String.valueOf(charSequence).toLowerCase();
                theLoaiAdapter.getFilter().filter(text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.putInt("tongtheloaiKey", arrTheLoai.size());
                int i = 0;
                for (LvTheLoai p : theLoaiAdapter.getCheckedItems()) {
                    if (p.isChecked()) {
                        i = 1;
                        editor.putString("theloaiKey" + p.getId(), p.getTheloai());
                    }
                }
                editor.commit();
                if (i == 1) {
                    Toast.makeText(getApplication(), "Chọn Thể Loại Truyện Thành Công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplication(), MainActivity.class));
                } else {
                    Toast.makeText(getApplication(), "Hãy Chọn Thể Loại Truyện", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void getJSONData() {
        new TheLoaiActivitry.GetJSONAsync().execute();
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
                URL url = new URL("http://m.sieuhack.mobi/json-theloai.php");
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
                    String theloai = String.valueOf(jObject.getString("theloai"));
                    arrTheLoai.add(new LvTheLoai(i, theloai));
                }
            } catch (Exception e) {
                Log.e("MY_WATCH", "Lỗi: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            theLoaiAdapter = new TheLoaiAdapter(TheLoaiActivitry.this, arrTheLoai);
            lvTheLoai.setAdapter(theLoaiAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            try {
                finish();
            } catch (Exception e) {
                Log.d("Loi TheLoaiActivity: ", e.toString());
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
