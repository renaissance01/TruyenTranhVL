package truyentranh.vl.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;

import java.util.ArrayList;
import java.util.List;

import truyentranh.vl.R;
import truyentranh.vl.database.Database;
import truyentranh.vl.fragments.DownloadFragment;
import truyentranh.vl.fragments.FavoriteFragment;
import truyentranh.vl.fragments.HideFragment;
import truyentranh.vl.fragments.HistoryFragment;
import truyentranh.vl.fragments.MangaFragment;
import truyentranh.vl.model.DbView;
import truyentranh.vl.slideimages.ShowImages;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentDrawer drawerFragment;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    //Session lưu tài khoản đăng nhập
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "UserPrefs";
    public static final String HoTenKey = "hotenKey";

    private String tab;
    private FloatingActionButton fab;

    private ArrayList<DbView> arrView = new ArrayList<DbView>();
    private Database db;

    private int tab2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new Database(getApplication());

        //Hiệu Ứng Start
        //SplashScreen.show(this, SplashScreen.TERMINAL_ANIMATION);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String hoten = sharedpreferences.getString(HoTenKey, null);
        //Toast.makeText(getApplication(), hoten, Toast.LENGTH_SHORT).show();

        FacebookSdk.sdkInitialize(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Trang Chủ");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            showAlertDialog(MainActivity.this, "Lỗi Kết Nối!",
                    "Vui Lòng Bật Mạng Để Đọc Truyện", false);
        }

        //Check quyền đọc thẻ nhở api 23 trở lên
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                //Toast.makeText(MainActivity.this, "?", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Toast.makeText(MainActivity.this, "nhỏ hơn", Toast.LENGTH_SHORT).show();
        }

        //Select tab nếu nhận được key từ ChapActivity trả về
        try {
            tab = getIntent().getBundleExtra("key").getString("tab");
            //Toast.makeText(getApplication(), tab, Toast.LENGTH_SHORT).show();
            if (tab != null && tab.equals("search") == false) {
                //Trở lại tab comment
                //finish();
                //startActivity(getIntent());
                TabLayout.Tab tabbar = tabLayout.getTabAt(Integer.parseInt(tab));
                tabbar.select();
            }
        } catch (Exception e) {

        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (db.checkTrang()) {
                        arrView = db.getDangXem();
                        Intent intent = new Intent(getApplication(), ShowImages.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("idtruyen", String.valueOf(Integer.valueOf(arrView.get(0).getIdtruyen())));
                        bundle.putString("tentruyen", arrView.get(0).getTentruyen());
                        bundle.putString("idchap", String.valueOf(Integer.valueOf(arrView.get(0).getIdchap())));
                        bundle.putString("tenchap", arrView.get(0).getTenchap());
                        bundle.putString("trang", arrView.get(0).getTrang());
                        bundle.putString("tab", tab2 + "");
                        if (String.valueOf(Integer.valueOf(arrView.get(0).getIdtruyen())).equals("9999999"))
                            bundle.putString("checktaitruyen", "dangxemtai");
                        else
                            bundle.putString("checktaitruyen", "check");
                        intent.putExtra("keychap", bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplication(), "Bạn Chưa Xem Trang Nào!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplication(), "Bạn Chưa Xem Chap Nào!", Toast.LENGTH_SHORT).show();
                    Log.d("ChapActivity", e.toString());
                }
                //Toast.makeText(getApplication(), idtruyen, Toast.LENGTH_SHORT).show();
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

    private void setupTabIcons() {
        int[] tabIcons = {
                R.drawable.ic_tab_manga,
                R.drawable.ic_tab_favorite,
                R.drawable.ic_tab_history,
                R.drawable.ic_tab_hide,
                R.drawable.ic_tab_download,
        };

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#CCCCFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(4).getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab2 = tab.getPosition();
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        toolbar.setTitle("Trang Chủ");
                        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplication());
                        Intent i = new Intent("TAB_TRANGCHU");
                        lbm.sendBroadcast(i);
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        toolbar.setTitle("Yêu Thích");
                        LocalBroadcastManager lbm2 = LocalBroadcastManager.getInstance(getApplication());
                        Intent i2 = new Intent("TAB_YEUTHICH");
                        lbm2.sendBroadcast(i2);
                        break;
                    case 2:
                        viewPager.setCurrentItem(2);
                        toolbar.setTitle("Lịch Sử");
                        LocalBroadcastManager lbm3 = LocalBroadcastManager.getInstance(getApplication());
                        Intent i3 = new Intent("TAB_LICHSU");
                        lbm3.sendBroadcast(i3);
                        break;
                    case 3:
                        viewPager.setCurrentItem(3);
                        toolbar.setTitle("Xem Nhiều");
                        LocalBroadcastManager lbm4 = LocalBroadcastManager.getInstance(getApplication());
                        Intent i4 = new Intent("TAB_XEMNHIEU");
                        lbm4.sendBroadcast(i4);
                        break;
                    case 4:
                        viewPager.setCurrentItem(4);
                        toolbar.setTitle("Đã Tải Về Máy");
                        LocalBroadcastManager lbm5 = LocalBroadcastManager.getInstance(getApplication());
                        Intent i5 = new Intent("TAB_TAI");
                        lbm5.sendBroadcast(i5);
                        break;
                    default:
                        viewPager.setCurrentItem(tab.getPosition());
                        toolbar.setTitle("Trang Chủ");
                        break;
                }
                tab.getIcon().setColorFilter(Color.parseColor("#CCCCFF"), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MangaFragment(), "Trang Chủ");
        adapter.addFrag(new FavoriteFragment(), "Yêu Thích");
        adapter.addFrag(new HistoryFragment(), "Lịch Sử");
        adapter.addFrag(new HideFragment(), "Xem Nhiều");
        adapter.addFrag(new DownloadFragment(), "Đã Tải Về Máy");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
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
            case 3:
                break;
            case 4:
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
