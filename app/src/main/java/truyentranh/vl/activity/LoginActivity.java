package truyentranh.vl.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
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

import truyentranh.vl.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnDangKy, btnDangNhap;
    private EditText txtTaiKhoan, txtMatKhau;
    private String taikhoan, matkhau, hoten, check;
    private String taikhoanfb, hotenfb, checkfb;
    private TextView tvDangNhap;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    //Session lưu tài khoản đăng nhập
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "UserPrefs";
    public static final String TaiKhoanKey = "taikhoanKey";
    public static final String TrangThaiKey = "trangthaiKey";
    public static final String HoTenKey = "hotenKey";

    //Facebook
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnDangKy = (Button) findViewById(R.id.btnDangKy);
        btnDangNhap = (Button) findViewById(R.id.btnDangNhap);
        txtTaiKhoan = (EditText) findViewById(R.id.txtTaiKhoan);
        txtMatKhau = (EditText) findViewById(R.id.txtMatKhau);

        tvDangNhap = (TextView) findViewById(R.id.tvDangNhap);
        //Thiết lập font để sử dụng từ assets
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/dangnhap.ttf");
        //Thiết lập font cho TextView
        tvDangNhap.setTypeface(face);

        onClick();

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            showAlertDialog(LoginActivity.this, "Lỗi Kết Nối!",
                    "Vui Lòng Bật Mạng Để Đăng Nhập Tài Khoản", false);
        }

        //Session lưu tài khoản đăng nhập
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        try {
            String taikhoan = sharedpreferences.getString(TaiKhoanKey, null);
            if (taikhoan.equals("") == false) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            }
        } catch (Exception e) {
        }

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(getApplication(), "Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                //Toast.makeText(getApplication(), "Login attempt canceled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                //Toast.makeText(getApplication(), "Login attempt failed.", Toast.LENGTH_SHORT).show();
                Log.d("Lỗi Login FB: ", e.toString());
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
        //nếu đăng nhập thì hiển thị Ảnh đại diện và tên người dùng
        if (isLoggedIn()) {
            //Toast.makeText(getApplication(), "Đã Login", Toast.LENGTH_SHORT).show();
        }
        //Nếu chưa đăng nhập thì ẩn
        else {
            //Toast.makeText(getApplication(), "Chưa Login", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //Hàm kiểm tra trạng thái đăng nhập
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    //Hàm lấy tên người dùng
    private void displayMessage(Profile profile) {
        if (profile != null) {

            taikhoanfb = profile.getId();
            hotenfb = profile.getName();

            //Toast.makeText(getApplication(), profile.getName(), Toast.LENGTH_SHORT).show();
            //Toast.makeText(getApplication(), profile.getId(), Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(TaiKhoanKey, taikhoanfb);
            editor.putString(HoTenKey, hotenfb);
            editor.commit();

            new SendRequestFB().execute();

            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
        }
    }

    public void onClick() {
        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                if (txtTaiKhoan.getText().toString().trim().equals("")) {
                    txtTaiKhoan.setError("Không được để trống!");
                    i = 1;
                }
                if (txtMatKhau.getText().toString().trim().equals("")) {
                    txtMatKhau.setError("Không được để trống!");
                    i = 1;
                }
                if (i == 0) {
                    new SendRequest().execute();
                }
            }
        });
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
            taikhoan = txtTaiKhoan.getText().toString();
            matkhau = txtMatKhau.getText().toString();
        }

        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL("http://m.sieuhack.mobi/login.php");

                JSONObject postDataParams = new JSONObject();


                postDataParams.put("taikhoan", taikhoan);
                postDataParams.put("matkhau", matkhau);

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
                hoten = jsonRoot.getString("hoten");
            } catch (JSONException e) {
                check = "Tam";
            }
            if (result.equals("Tai Khoan Khong Ton Tai")) {
                Toast.makeText(getApplicationContext(), "Tài Khoản Không Tồn Tại",
                        Toast.LENGTH_LONG).show();
            } else if (check.equals("Dung")) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(TaiKhoanKey, taikhoan);
                editor.putString(HoTenKey, hoten);
                editor.putString(TrangThaiKey, "local");
                editor.commit();

                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Mật Khẩu Sai",
                        Toast.LENGTH_LONG).show();
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

    public class SendRequestFB extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {

        }

        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL("http://m.sieuhack.mobi/register.php");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("taikhoan", taikhoanfb);
                postDataParams.put("hoten", hotenfb);

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
            if (result.equals("Thanh Cong")) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            } else if (result.equals("Tai Khoan Da Ton Tai")) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getPostDataStringFB(JSONObject params) throws Exception {

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
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
