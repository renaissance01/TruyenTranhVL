package truyentranh.vl.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.ArrayList;

import truyentranh.vl.R;
import truyentranh.vl.adapter.LvNavigation;
import truyentranh.vl.database.Database;
import truyentranh.vl.model.DbView;
import truyentranh.vl.model.Decompress;
import truyentranh.vl.model.TabItem;
import truyentranh.vl.slideimages.ShowImages;

public class FragmentDrawer extends Fragment {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;

    private ArrayList<TabItem> arrItem = new ArrayList<>();
    private ArrayList<TabItem> arrItem2 = new ArrayList<>();
    private LvNavigation adapter = null;
    private LvNavigation adapter2 = null;
    private ListView lvTruyen = null;
    private ListView lvHeThong = null;
    private TabItem tabItem, tabItem2;

    private Database db;
    private ArrayList<DbView> arrView = new ArrayList<DbView>();
    private String _path;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    //Session lưu tài khoản đăng nhập
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "UserPrefs";
    public static final String TrangThaiKey = "trangthaiKey";
    private String trangthai;

    //Facebook
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        trangthai = sharedpreferences.getString(trangthai, null);

        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        db = new Database(getActivity());

        //Truyện
        tabItem = new TabItem(R.drawable.icon_home, "Trang chủ");
        arrItem.add(tabItem);
        tabItem = new TabItem(R.drawable.icon_search, "Tìm kiếm truyện");
        arrItem.add(tabItem);
        /*tabItem = new TabItem(R.drawable.icon_book, "Trang đang xem");
        arrItem.add(tabItem);*/
        tabItem = new TabItem(R.drawable.icon_file, "Đọc truyện từ file nén");
        arrItem.add(tabItem);
/*        tabItem = new TabItem(R.drawable.icon_xemnhieu, "Xem nhiều nhất");
        arrItem.add(tabItem);
        tabItem = new TabItem(R.drawable.icon_lichsu, "Lịch sử");
        arrItem.add(tabItem);
        tabItem = new TabItem(R.drawable.icon_yeuthich, "Yêu thích");
        arrItem.add(tabItem);*/
        lvTruyen = (ListView) layout.findViewById(R.id.lvTruyen);
        adapter = new LvNavigation(getActivity(), R.layout.custom_listview, arrItem);
        lvTruyen.setAdapter(adapter);

        // creating connection detector class instance
        cd = new ConnectionDetector(getActivity());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        lvTruyen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
                if (position == 1) {
                    startActivity(new Intent(getActivity(), SearchActivity.class));
                }
                /*if (position == 2) {
                    // check for Internet status
                    if (!isInternetPresent) {
                        showAlertDialog(getActivity(), "Lỗi Kết Nối!",
                                "Vui Lòng Bật Mạng", false);
                    }else if (db.checkTrang()) {
                        arrView = db.getDangXem();
                        Intent intent = new Intent(getActivity(), ShowImages.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("idtruyen", String.valueOf(Integer.valueOf(arrView.get(0).getIdtruyen())));
                        bundle.putString("tentruyen", arrView.get(0).getTentruyen());
                        bundle.putString("idchap", String.valueOf(Integer.valueOf(arrView.get(0).getIdchap())));
                        bundle.putString("tenchap", arrView.get(0).getTenchap());
                        bundle.putString("trang", arrView.get(0).getTrang());
                        if(String.valueOf(Integer.valueOf(arrView.get(0).getIdtruyen())).equals("9999999"))
                            bundle.putString("checktaitruyen", "dangxemtai");
                        else
                            bundle.putString("checktaitruyen", "check");
                        intent.putExtra("keychap", bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Bạn Chưa Xem Trang Nào!", Toast.LENGTH_SHORT).show();
                    }
                }*/
                if (position == 2) {
                    final Context ctx = getActivity();
                    new ChooserDialog().with(ctx)
                    .withFilter(false, false, "zip", "rar")
                    .withStartFile(_path)
                    .withResources(R.string.title_choose_file, R.string.chon, R.string.thoat)
                    .withChosenListener(new ChooserDialog.Result() {
                        @Override
                        public void onChoosePath(String path, File pathFile) {
                            //Unzip file đc chọn
                            String pathFile2 = pathFile.getName().replace(".zip", "");
                            String unzipLocation = Environment.getExternalStorageDirectory() + "/TruyenTranhVL/" + pathFile2 + "/";
                            Decompress d = new Decompress(path, unzipLocation);
                            d.unzip();

                            _path = path;

                            Intent intent = new Intent(getActivity(), ShowImages.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("path", pathFile2);
                            bundle.putString("trang", "0");
                            bundle.putString("checktaitruyen", "docfile");
                            intent.putExtra("keychap", bundle);
                            startActivity(intent);
                        }
                    })
                    .build()
                    .show();
                }
            }
        });

        //Hệ Thống
        tabItem2 = new TabItem(R.drawable.icon_chiase, "Chia sẻ");
        arrItem2.add(tabItem2);
        tabItem2 = new TabItem(R.drawable.icon_thongtin, "Thông tin");
        arrItem2.add(tabItem2);
        tabItem2 = new TabItem(R.drawable.icon_logout, "Đăng xuất");
        arrItem2.add(tabItem2);
        lvHeThong = (ListView) layout.findViewById(R.id.lvHeThong);
        adapter2 = new LvNavigation(getActivity(), R.layout.custom_listview, arrItem2);
        lvHeThong.setAdapter(adapter2);

        lvHeThong.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    startActivity(new Intent(getActivity(), ShareActivity.class));
                }
                if (position == 1) {
                    startActivity(new Intent(getActivity(), InfoActivity.class));
                }
                if (position == 2) {
                    if(trangthai == null){
                        FacebookSdk.sdkInitialize(getContext());
                        LoginManager.getInstance().logOut();
                        AccessToken.setCurrentAccessToken(null);
                    }
                    sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.clear();
                    editor.commit();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });

        return layout;
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

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }
}
