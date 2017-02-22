package truyentranh.vl.fragmentschap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import truyentranh.vl.activity.ChapActivity;
import truyentranh.vl.slideimages.ShowImages;

public class Like extends Activity {

    private String id, tentruyen, taikhoan, check, thich, trang;

    //Session lưu tài khoản đăng nhập
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "UserPrefs";
    public static final String TaiKhoanKey = "taikhoanKey";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        taikhoan = sharedpreferences.getString(TaiKhoanKey, null);

        id = getIntent().getBundleExtra("key").getString("idtruyen");
        tentruyen = getIntent().getBundleExtra("key").getString("tentruyen");
        trang = getIntent().getBundleExtra("key").getString("trang");

        new Like.SendRequest().execute();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL("http://m.sieuhack.mobi/like.php");

                JSONObject postDataParams = new JSONObject();


                postDataParams.put("id", id);
                postDataParams.put("taikhoan", taikhoan);

                Log.e("Dữ Liệu:", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Lấy dữ liệu từ json
            try {
                JSONObject jsonRoot = new JSONObject(result);
                check = jsonRoot.getString("check");
                thich = jsonRoot.getString("thich");
            } catch (JSONException e) {
                check = "Tam";
            }
            //Toast.makeText(getApplication(), id+"-"+taikhoan+"-"+result,Toast.LENGTH_LONG).show();
            if (check.equals("Da Thich")){
                Toast.makeText(getApplicationContext(), "Bạn Đã Thích Truyện Này Rồi :)",
                        Toast.LENGTH_LONG).show();
                finish();
            }else if(check.equals("Thich Thanh Cong")) {
                Intent intent = new Intent(getApplication(), ChapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", (Integer.parseInt(id)-1)+"");
                bundle.putString("tentruyen", tentruyen);
                bundle.putString("sochap", "0");
                bundle.putString("trang", trang);
                bundle.putString("nhanbiet", "activity");
                intent.putExtra("key", bundle);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Lỗi",
                        Toast.LENGTH_LONG).show();
                finish();
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

}
