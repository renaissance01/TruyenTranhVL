package truyentranh.vl.slideimages;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import truyentranh.vl.R;
import truyentranh.vl.activity.ChapActivity;
import truyentranh.vl.activity.MainActivity;
import truyentranh.vl.database.Database;
import truyentranh.vl.fragmentschap.ChapFragment;
import truyentranh.vl.model.DbDaXem;
import truyentranh.vl.model.DbView;

public class ShowImages extends AppCompatActivity {

    private ViewPager myPager;
    private String id;
    private ViewPagerAdapter adapter;
    private String imageArra[] = null;
    private String idtruyen, idchap, tentruyen, tenchap, trangtruyen;
    private String path;
    private ProgressBar bar;
    private LinearLayout mLinearLayout, layouttop;
    private TextView tvViTri, tvTong, tvTenChap, tvGach;
    private ValueAnimator mAnimator, mAnimator2;

    private ImageView ivBack, ivRotation, ivDenTrang, ivLeft, ivRight;
    private ImageView ivDauTien, ivCuoiCung, ivVaoTrang;
    private int rotation2 = 0;

    private int vitri = 0;
    private static final String TAG = "ShowImages";
    private Boolean doubleTapIsLocked = false;
    private Database db;

    private String tenthumuc, checktaitruyen, tab;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manganew);

        db = new Database(ShowImages.this);

        myPager = (ViewPager) findViewById(R.id.myfivepanelpager);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        /*tvTrangTruoc = (TextView) findViewById(R.id.tvTrangTruoc);
        tvTrangSau = (TextView) findViewById(R.id.tvTrangSau);*/
        tvViTri = (TextView) findViewById(R.id.tvViTri);
        tvTong = (TextView) findViewById(R.id.tvTong);
        tvGach = (TextView) findViewById(R.id.tvGach);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivRotation = (ImageView) findViewById(R.id.ivRotation);
        ivDenTrang = (ImageView) findViewById(R.id.ivDenTrang);
        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivRight = (ImageView) findViewById(R.id.ivRight);
        ivDauTien = (ImageView) findViewById(R.id.ivDauTien);
        ivCuoiCung = (ImageView) findViewById(R.id.ivCuoiCung);
        ivVaoTrang = (ImageView) findViewById(R.id.ivVaoTrang);
        tvTenChap = (TextView) findViewById(R.id.tvTenChap);

        idtruyen = getIntent().getBundleExtra("keychap").getString("idtruyen");
        idchap = getIntent().getBundleExtra("keychap").getString("idchap");
        tentruyen = getIntent().getBundleExtra("keychap").getString("tentruyen");
        tenchap = getIntent().getBundleExtra("keychap").getString("tenchap");
        trangtruyen = getIntent().getBundleExtra("keychap").getString("trang");
        checktaitruyen = getIntent().getBundleExtra("keychap").getString("checktaitruyen");

        path = getIntent().getBundleExtra("keychap").getString("path");
        tab = getIntent().getBundleExtra("keychap").getString("tab");

        //Lấy ngày tháng hiện tại kiểu long
        Calendar cal = Calendar.getInstance();
        time = cal.getTimeInMillis();

        if (checktaitruyen.equals("docfile")) {
            tvTenChap.setText(path);
        } else {
            tvTenChap.setText(tenchap);
        }

        //Json
        try {
            getJSONData();
        } catch (Exception e) {
        }

        mLinearLayout = (LinearLayout) findViewById(R.id.expandable);
        layouttop = (LinearLayout) findViewById(R.id.layouttop);
        //Add onPreDrawListener
        mLinearLayout.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        mLinearLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                        mLinearLayout.setVisibility(View.VISIBLE);
                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        mLinearLayout.measure(widthSpec, heightSpec);

                        mAnimator = slideAnimator(0, mLinearLayout.getMeasuredHeight());
                        return true;
                    }
                });
        layouttop.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {

                    @Override
                    public boolean onPreDraw() {
                        layouttop.getViewTreeObserver().removeOnPreDrawListener(this);
                        layouttop.setVisibility(View.VISIBLE);
                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        layouttop.measure(widthSpec, heightSpec);

                        mAnimator2 = slideAnimator2(0, layouttop.getMeasuredHeight());
                        return true;
                    }
                });

        ivLeft.setVisibility(View.GONE);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPager.setCurrentItem(vitri - 1);
                if (vitri == 0) {
                    ivLeft.setVisibility(View.GONE);
                }
            }
        });
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPager.setCurrentItem(vitri + 1);
            }
        });

        ivDenTrang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowImages.this, NumberPickerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sotrang", imageArra.length + "");
                bundle.putString("idtruyen", idtruyen);
                bundle.putString("idchap", idchap);
                bundle.putString("tentruyen", tentruyen);
                bundle.putString("tenchap", tenchap);
                bundle.putString("vitri", vitri + "");
                if (checktaitruyen.equals("checktaitruyen")) {
                    bundle.putString("checktaitruyen", "checktaitruyen");
                } else if (checktaitruyen.equals("docfile")) {
                    bundle.putString("path", path);
                    bundle.putString("checktaitruyen", "docfile");
                } else {
                    bundle.putString("checktaitruyen", "check");
                }
                intent.putExtra("keychap", bundle);
                startActivity(intent);
            }
        });

        ivDauTien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowImages.this, "Đã Về Trang Đầu Tiên", Toast.LENGTH_SHORT).show();
                myPager.setCurrentItem(0);
            }
        });

        ivCuoiCung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ShowImages.this, "Đã Về Trang Cuối Cùng", Toast.LENGTH_SHORT).show();
                myPager.setCurrentItem(imageArra.length);
            }
        });

        ivVaoTrang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ShowImages.this);
                alert.setTitle("Nhập Trang Truyện Cần Đọc");
                final EditText input = new EditText(ShowImages.this);
                alert.setView(input);

                alert.setPositiveButton("Đến", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String srt = input.getEditableText().toString();
                        String regexStr = "^[+]?[0-9]{1,9999}$";
                        if (srt.equals("")) {
                            Toast.makeText(ShowImages.this, "Vui Lòng Nhập Trang Cần Đọc", Toast.LENGTH_SHORT).show();
                        } else if (srt.matches(regexStr) == false) {
                            Toast.makeText(ShowImages.this, "Vui Lòng Nhập Trang Kiểu Số", Toast.LENGTH_SHORT).show();
                        } else if (Integer.valueOf(srt.trim()) < 1 || Integer.valueOf(srt.trim()) > imageArra.length) {
                            Toast.makeText(ShowImages.this, "Vui lòng nhập trang truyện từ 1 -> " + imageArra.length, Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                Toast.makeText(ShowImages.this, "Đã Đến Trang " + srt, Toast.LENGTH_SHORT).show();
                                myPager.setCurrentItem(Integer.valueOf(srt.trim()) - 1);
                            } catch (Exception e) {
                            }
                        }
                    }
                });
                alert.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            }
        });

        //db.xoaDangXem();
        try {
            if (checktaitruyen.equals("checktaitruyen")) {
                if (db.checkDangXem("9999999"))
                    db.updateDangXem(new DbView("9999999", tentruyen, "9999999", tentruyen + " - Chap " + idchap + ": " + tenchap, String.valueOf(trangtruyen), time));
                else
                    db.addDangXem(new DbView("9999999", tentruyen, "9999999", tentruyen + " - Chap " + idchap + ": " + tenchap, String.valueOf(trangtruyen), time));
            } else if (checktaitruyen.equals("docfile")) {
                if (db.checkDangXemZIP("9999999"))
                    db.updateDangXem(new DbView("9999999", "9999999", "9999999", path, String.valueOf(trangtruyen), time));
                else
                    db.addDangXem(new DbView("9999999", "9999999", "9999999", path, String.valueOf(trangtruyen), time));
            } else {
                if (db.checkDangXem(idtruyen))
                    db.updateDangXem(new DbView(idtruyen, tentruyen, idchap, tenchap, String.valueOf(trangtruyen), time));
                else {
                    db.addDangXem(new DbView(idtruyen, tentruyen, idchap, tenchap, String.valueOf(trangtruyen), time));
                }
            }
        } catch (Exception e) {
        }

        //Thêm vào chap đã xem
        try {
            if (checktaitruyen.equals("checktaitruyen")) {
                db.addDaXem(new DbDaXem("9999999", tentruyen + " - Chap " + idchap + ": " + tenchap));
            } else if (checktaitruyen.equals("docfile")) {

            } else {
                db.addDaXem(new DbDaXem(idtruyen, idchap));
            }
        } catch (Exception e) {
        }

        myPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrollStateChanged(int arg0) {
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageSelected(int currentPage) {
                //db.xoaDangXem();

                if (checktaitruyen.equals("checktaitruyen")) {
                    if (db.checkDangXem("9999999"))
                        db.updateDangXem(new DbView("9999999", tentruyen, "9999999", tentruyen + " - Chap " + idchap + ": " + tenchap, String.valueOf(currentPage), time));
                    else
                        db.addDangXem(new DbView("9999999", tentruyen, "9999999", tentruyen + " - Chap " + idchap + ": " + tenchap, String.valueOf(currentPage), time));
                } else if (checktaitruyen.equals("docfile")) {
                    if (db.checkDangXemZIP("9999999"))
                        db.updateDangXem(new DbView("9999999", "9999999", "9999999", path, String.valueOf(currentPage), time));
                    else
                        db.addDangXem(new DbView("9999999", "9999999", "9999999", path, String.valueOf(currentPage), time));
                } else {
                    if (db.checkDangXem(idtruyen))
                        db.updateDangXem(new DbView(idtruyen, tentruyen, idchap, tenchap, String.valueOf(currentPage), time));
                    else
                        db.addDangXem(new DbView(idtruyen, tentruyen, idchap, tenchap, String.valueOf(currentPage), time));
                }
                collapse();
                vitri = currentPage;
                tvViTri.setText(currentPage + 1 + "");
                tvTong.setText(imageArra.length + "");
                /*if ((currentPage + 1) == imageArra.length) {
                    ivRight.setVisibility(View.GONE);
                    ivLeft.setVisibility(View.VISIBLE);
                } else if (currentPage == 0) {
                    ivRight.setVisibility(View.VISIBLE);
                    ivLeft.setVisibility(View.GONE);
                } else {
                    ivRight.setVisibility(View.VISIBLE);
                    ivLeft.setVisibility(View.VISIBLE);
                }*/
            }

        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (checktaitruyen.equals("checktaitruyen")) {
                        //finish();
                        //Toast.makeText(getApplication(), tab+"", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ShowImages.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("tab", tab);
                        intent.putExtra("key", bundle);
                        startActivity(intent);
                    } else if (checktaitruyen.equals("docfile")) {
                        finish();
                    } else if (checktaitruyen.equals("dangxemtai")) {
                        finish();
                    } else {
                        Intent intent = new Intent(ShowImages.this, ChapActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", idtruyen);
                        bundle.putString("tentruyen", tentruyen);
                        bundle.putString("sochap", "0");
                        bundle.putString("nhanbiet", "activity");
                        bundle.putString("tab", tab);
                        intent.putExtra("key", bundle);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                }
            }
        });

        ivRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rotation2 == 0) {
                    lockScreenOrientation(ShowImages.this);
                    Toast.makeText(ShowImages.this, "Khóa Xoay Màn Hình", Toast.LENGTH_SHORT).show();
                } else {
                    unlockScreenOrientation(ShowImages.this);
                    Toast.makeText(ShowImages.this, "Mở Khóa Xoay Màn Hình", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Sau 3 giây tự ẩn
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                collapse();
            }
        }, 3000);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (!doubleTapIsLocked) {
            gestureDetector.onTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
                doubleTapIsLocked = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {//double tap
            if (mLinearLayout.getVisibility() == View.GONE) {
                try {
                    expand();
                } catch (Exception ee) {
                }
            } else {
                collapse();
                try {
                    collapse();
                } catch (Exception ee) {
                }
            }
            return super.onDoubleTap(e);
        }
    };
    GestureDetector gestureDetector = new GestureDetector(simpleOnGestureListener);

    private void expand() {
        //set Visible
        if (vitri != 0) {
            ivLeft.setVisibility(View.VISIBLE);
        }
        if ((vitri + 1) != imageArra.length) {
            ivRight.setVisibility(View.VISIBLE);
        }
        layouttop.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.VISIBLE);
        mAnimator.start();
        mAnimator2.start();
    }

    private void collapse() {

        ivLeft.setVisibility(View.GONE);
        ivRight.setVisibility(View.GONE);

        int finalHeight = mLinearLayout.getHeight();
        int finalHeight2 = layouttop.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0);
        ValueAnimator mAnimator2 = slideAnimator2(finalHeight2, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                try {
                    mLinearLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                }
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator.start();

        mAnimator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                try {
                    layouttop.setVisibility(View.GONE);
                } catch (Exception e) {
                }
            }

            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        mAnimator2.start();
    }


    private ValueAnimator slideAnimator(int start, int end) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = mLinearLayout.getLayoutParams();
                layoutParams.height = value;
                mLinearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private ValueAnimator slideAnimator2(int start, int end) {

        ValueAnimator animator2 = ValueAnimator.ofInt(start, end);

        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = layouttop.getLayoutParams();
                layoutParams.height = value;
                layouttop.setLayoutParams(layoutParams);
            }
        });
        return animator2;
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
                if (checktaitruyen.equals("checktaitruyen")) {
                    try {
                        File yourDir = new File(Environment.getExternalStorageDirectory().getPath() + "/TruyenTranhVL/" + tentruyen + " - Chap " + idchap + ": " + tenchap);
                        int i = 0;
                        for (File f : yourDir.listFiles()) {
                            if (f.isFile()) {
                                i++;
                            }
                        }
                        imageArra = new String[i];
                        i = 0;
                        for (File f : yourDir.listFiles()) {
                            if (f.isFile()) {
                                tenthumuc = "/" + tentruyen + " - Chap " + idchap + ": " + tenchap + "/" + f.getName();
                                //Toast.makeText(ShowImages.this, tenthumuc, Toast.LENGTH_SHORT).show();
                                imageArra[i] = tenthumuc;
                            }
                            i++;
                        }
                    } catch (Exception e) {
                        Log.e("MY_WATCH", "Lỗi: " + e.getMessage());
                    }
                } else if (checktaitruyen.equals("docfile")) {
                    try {
                        File yourDir = new File(Environment.getExternalStorageDirectory().getPath() + "/TruyenTranhVL/" + path);
                        int i = 0;
                        for (File f : yourDir.listFiles()) {
                            if (f.isFile()) {
                                i++;
                            }
                        }
                        imageArra = new String[i];
                        i = 0;
                        for (File f : yourDir.listFiles()) {
                            if (f.isFile()) {
                                tenthumuc = "/" + path + "/" + f.getName();
                                //Toast.makeText(ShowImages.this, tenthumuc, Toast.LENGTH_SHORT).show();
                                imageArra[i] = tenthumuc;
                            }
                            i++;
                        }
                    } catch (Exception e) {
                        Log.e("MY_WATCH", "Lỗi: " + e.getMessage());
                    }
                } else if (checktaitruyen.equals("dangxemtai")) {
                    try {
                        File yourDir = new File(Environment.getExternalStorageDirectory().getPath() + "/TruyenTranhVL/" + tenchap);
                        int i = 0;
                        for (File f : yourDir.listFiles()) {
                            if (f.isFile()) {
                                i++;
                            }
                        }
                        imageArra = new String[i];
                        i = 0;
                        for (File f : yourDir.listFiles()) {
                            if (f.isFile()) {
                                tenthumuc = "/" + tenchap + "/" + f.getName();
                                //Toast.makeText(ShowImages.this, tenthumuc, Toast.LENGTH_SHORT).show();
                                imageArra[i] = tenthumuc;
                            }
                            i++;
                        }
                    } catch (Exception e) {
                        Log.e("MY_WATCH", "Lỗi: " + e.getMessage());
                    }
                } else if (idtruyen.equals("9999999")) {
                    try {
                        File yourDir = new File(Environment.getExternalStorageDirectory().getPath() + "/TruyenTranhVL/" + tenchap);
                        int i = 0;
                        for (File f : yourDir.listFiles()) {
                            if (f.isFile()) {
                                i++;
                            }
                        }
                        imageArra = new String[i];
                        i = 0;
                        for (File f : yourDir.listFiles()) {
                            if (f.isFile()) {
                                tenthumuc = "/" + tenchap + "/" + f.getName();
                                //Toast.makeText(ShowImages.this, tenthumuc, Toast.LENGTH_SHORT).show();
                                imageArra[i] = tenthumuc;
                            }
                            i++;
                        }
                    } catch (Exception e) {
                        Log.e("MY_WATCH", "Lỗi: " + e.getMessage());
                    }
                } else {
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
                        JSONObject jObject = jsonArray.getJSONObject(Integer.valueOf(idtruyen.trim()));

                        JSONArray jsonArray2 = jObject.optJSONArray("chap");
                        JSONObject jObject2 = jsonArray2.getJSONObject(Integer.valueOf(idchap.trim()));

                        JSONArray jsonArray3 = jObject2.optJSONArray("trang");

                        int total = jsonArray3.length();
                        imageArra = new String[total];
                        for (int j = 0; j < jsonArray3.length(); j++) {
                            JSONObject jObject3 = jsonArray3.getJSONObject(j);
                            String trang = String.valueOf(jObject3.getString("trang" + String.valueOf(j + 1)));
                            imageArra[j] = trang;
                        }
                    } catch (Exception e) {
                        Log.e("MY_WATCH", "Lỗi: " + e.getMessage());
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
            try {
                tvViTri.setVisibility(View.VISIBLE);
                tvTong.setVisibility(View.VISIBLE);
                tvGach.setVisibility(View.VISIBLE);
                if (checktaitruyen.equals("checktaitruyen")) {
                    //Load trang truyện ảnh vào màn hình myPager
                    //Toast.makeText(ShowImages.this, Environment.getExternalStorageDirectory()+"", Toast.LENGTH_SHORT).show();
                    tvViTri.setText("1");
                    tvTong.setText(imageArra.length + "");
                    myPager.setAdapter(new LocalImageAdapter(ShowImages.this, imageArra, bar));

                    myPager.setCurrentItem(Integer.valueOf(trangtruyen));
                } else if (checktaitruyen.equals("docfile")) {
                    tvViTri.setText("1");
                    tvTong.setText(imageArra.length + "");
                    myPager.setAdapter(new LocalImageAdapter(ShowImages.this, imageArra, bar));

                    myPager.setCurrentItem(Integer.valueOf(trangtruyen));
                } else if (checktaitruyen.equals("dangxemtai")) {
                    tvViTri.setText("1");
                    tvTong.setText(imageArra.length + "");
                    myPager.setAdapter(new LocalImageAdapter(ShowImages.this, imageArra, bar));

                    myPager.setCurrentItem(Integer.valueOf(trangtruyen));
                } else if (idtruyen.equals("9999999")) {
                    tvViTri.setText("1");
                    tvTong.setText(imageArra.length + "");
                    myPager.setAdapter(new LocalImageAdapter(ShowImages.this, imageArra, bar));

                    myPager.setCurrentItem(Integer.valueOf(trangtruyen));
                } else {
                    tvViTri.setText("1");
                    tvTong.setText(imageArra.length + "");
                    adapter = new ViewPagerAdapter(ShowImages.this, imageArra, bar);
                    myPager.setAdapter(adapter);

                    myPager.setCurrentItem(Integer.valueOf(trangtruyen));
                }
            } catch (Exception e) {
            }
        }

    }

    public void lockScreenOrientation(Activity activity) {
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Configuration configuration = activity.getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        // Search for the natural position of the device
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ||
                configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
                        (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) {
            // Natural position is Landscape
            switch (rotation) {
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    break;
            }
        } else {
            // Natural position is Portrait
            switch (rotation) {
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
            }
        }
        ivRotation.setImageResource(R.drawable.ic_rotation_unlock);
        rotation2 = 1;
    }

    public void unlockScreenOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        ivRotation.setImageResource(R.drawable.ic_rotation_lock);
        rotation2 = 0;
    }


}
