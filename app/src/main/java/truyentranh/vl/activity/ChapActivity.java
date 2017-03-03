package truyentranh.vl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import truyentranh.vl.R;
import truyentranh.vl.database.Database;
import truyentranh.vl.fragmentschap.ChapFragment;
import truyentranh.vl.fragmentschap.CommentFragment;
import truyentranh.vl.fragmentschap.InfoFragment;
import truyentranh.vl.model.DbView;
import truyentranh.vl.slideimages.ShowImages;

public class ChapActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragmentDrawer drawerFragment;
    private String idtruyen, tentruyen, comment, tab, trang;
    private FloatingActionButton fab;

    //Trang đang xem
    private ArrayList<DbView> arrView = new ArrayList<DbView>();
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chap);

        idtruyen = getIntent().getBundleExtra("key").getString("id");
        tentruyen = getIntent().getBundleExtra("key").getString("tentruyen");
        comment = getIntent().getBundleExtra("key").getString("comment");
        tab = getIntent().getBundleExtra("key").getString("tab");
        trang = getIntent().getBundleExtra("key").getString("trang");

        db = new Database(getApplication());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(tentruyen);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        if (comment != null) {
            //Trở lại tab comment
            TabLayout.Tab tab = tabLayout.getTabAt(2);
            tab.select();
            fab.hide();
        }

        if (trang != null) {
            //Trở lại tab comment
            TabLayout.Tab tab = tabLayout.getTabAt(Integer.parseInt(trang));
            tab.select();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    arrView = db.getDangXemChap(idtruyen);
                    Intent intent = new Intent(getApplication(), ShowImages.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("idtruyen", String.valueOf(Integer.valueOf(arrView.get(0).getIdtruyen())));
                    bundle.putString("tentruyen", arrView.get(0).getTentruyen());
                    bundle.putString("idchap", String.valueOf(Integer.valueOf(arrView.get(0).getIdchap())));
                    bundle.putString("tenchap", arrView.get(0).getTenchap());
                    bundle.putString("trang", arrView.get(0).getTrang());
                    bundle.putString("tab", tab);
                    if(String.valueOf(Integer.valueOf(arrView.get(0).getIdtruyen())).equals("9999999"))
                        bundle.putString("checktaitruyen", "dangxemtai");
                    else
                        bundle.putString("checktaitruyen", "check");
                    intent.putExtra("keychap", bundle);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(getApplication(), "Bạn Chưa Xem Chap Nào!", Toast.LENGTH_SHORT).show();
                    Log.d("ChapActivity", e.toString());
                }
                //Toast.makeText(getApplication(), idtruyen, Toast.LENGTH_SHORT).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupTabIcons() {

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fab.show();
                        viewPager.setCurrentItem(0);
                        toolbar.setTitle(tentruyen);
                        break;
                    case 1:
                        fab.show();
                        viewPager.setCurrentItem(1);
                        toolbar.setTitle(tentruyen);
                        break;
                    case 2:
                        if (comment == null) {
                            fab.hide();
                        }else{
                            fab.hide();
                        }
                        viewPager.setCurrentItem(2);
                        toolbar.setTitle(tentruyen);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ChapFragment(), "CHAP");
        adapter.addFrag(new InfoFragment(), "THÔNG TIN");
        adapter.addFrag(new CommentFragment(), "BÌNH LUẬN");
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
            return mFragmentTitleList.get(position);
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
            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            try {
                if (tab.equals("search")) {
                    finish();
                } else if (tab.equals("0") || tab.equals("1") || tab.equals("2") || tab.equals("3") || tab.equals("4")) {
                    Intent intent = new Intent(ChapActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tab", tab);
                    intent.putExtra("key", bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ChapActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tab", tab);
                    intent.putExtra("key", bundle);
                    startActivity(intent);
                }
            } catch (Exception e) {
                Log.d("Loi ChapActivity: ", e.toString());
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
