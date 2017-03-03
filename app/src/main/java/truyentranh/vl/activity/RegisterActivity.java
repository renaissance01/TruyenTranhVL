package truyentranh.vl.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    private Button btnDangKy, btnDangNhap;
    private EditText txtTaiKhoan, txtMatKhau, txtMatKhau2, txtHoTen;
    private String taikhoan, matkhau, hoten;
    private TextView tvDangNhap;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnDangKy = (Button) findViewById(R.id.btnDangKy);
        btnDangNhap = (Button) findViewById(R.id.btnDangNhap);
        txtTaiKhoan = (EditText) findViewById(R.id.txtTaiKhoan);
        txtMatKhau = (EditText) findViewById(R.id.txtMatKhau);
        txtMatKhau2 = (EditText) findViewById(R.id.txtMatKhau2);
        txtHoTen = (EditText) findViewById(R.id.txtHoTen);

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
            showAlertDialog(RegisterActivity.this, "Lỗi Kết Nối!",
                    "Vui Lòng Bật Mạng Để Đăng Ký Tài Khoản", false);
        }
    }

    public void onClick() {
        btnDangKy.setOnClickListener(new View.OnClickListener() {
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
                if (txtMatKhau2.getText().toString().trim().equals("")) {
                    txtMatKhau2.setError("Không được để trống!");
                    i = 1;
                }
                if (txtHoTen.getText().toString().trim().equals("")) {
                    txtHoTen.setError("Không được để trống!");
                    i = 1;
                }
                if (i == 0) {
                    if (txtMatKhau.getText().toString().trim().equals(txtMatKhau2.getText().toString().trim()) == false) {
                        Toast.makeText(getApplication(), "2 Mật Khẩu Phải Khớp Nhau", Toast.LENGTH_SHORT).show();
                    } else {
                        new SendRequest().execute();
                    }
                }
                /*Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);*/
            }
        });
        btnDangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
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
            hoten = txtHoTen.getText().toString();
        }

        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL("http://m.sieuhack.mobi/register.php");

                JSONObject postDataParams = new JSONObject();

                postDataParams.put("taikhoan", taikhoan);
                postDataParams.put("matkhau", matkhau);
                postDataParams.put("hoten", hoten);

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
                Toast.makeText(getApplicationContext(), "Đăng Ký Thành Công Tài Khoản: " + taikhoan,
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                startActivity(intent);
            } else if (result.equals("Tai Khoan Da Ton Tai")) {
                Toast.makeText(getApplicationContext(), "Tài Khoản Đã Tồn Tại",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), result,
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

}
