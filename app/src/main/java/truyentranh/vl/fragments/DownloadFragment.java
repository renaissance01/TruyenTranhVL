package truyentranh.vl.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.util.ArrayList;

import truyentranh.vl.R;
import truyentranh.vl.adapter.LvDownload;
import truyentranh.vl.database.Database;
import truyentranh.vl.model.DbTaiTruyen;
import truyentranh.vl.model.LvDownloadItem;
import truyentranh.vl.slideimages.ShowImages;

public class DownloadFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<LvDownloadItem> arrItem = new ArrayList<>();
    private LvDownload adapter = null;
    private ListView lvManga = null;
    private LvDownloadItem lvDownloadItem;

    private Database db;
    private ArrayList<DbTaiTruyen> arrId = new ArrayList<>();
    private TextView tvYeuThich1, tvYeuThich2, tvSoTruyen;

    //Refresh
    private SwipeRefreshLayout swipeRefreshLayout;

    public DownloadFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_download,
                container, false);

        //Xử lý database;
        db = new Database(getActivity());
        arrId = db.getTaiTruyen();

        //Truyện
        lvManga = (ListView) rootView.findViewById(R.id.lvManga);

        try {
            //Json
            getJSONData();
        } catch (Exception e) {
        }

        tvYeuThich1 = (TextView) rootView.findViewById(R.id.tvYeuThich1);
        tvYeuThich2 = (TextView) rootView.findViewById(R.id.tvYeuThich2);
        tvSoTruyen = (TextView) rootView.findViewById(R.id.tvSoTruyen);
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
                Intent intent = new Intent(getActivity(), ShowImages.class);
                Bundle bundle = new Bundle();
                bundle.putString("idtruyen", String.valueOf(Integer.valueOf(arrItem.get(position).getId()) - 1));
                bundle.putString("idchap", String.valueOf(arrId.get(position).getIdchap()));
                bundle.putString("tentruyen", String.valueOf(arrItem.get(position).getTentruyen()));
                bundle.putString("tenchap", String.valueOf(arrId.get(position).getTenchap()));
                bundle.putString("trang", "0");
                bundle.putString("checktaitruyen", "checktaitruyen");
                bundle.putString("tab", "4");
                intent.putExtra("keychap", bundle);
                startActivity(intent);
            }
        });

        //Item listview khi ấn giữ
        lvManga.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int idxoa = position;
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle("Lựa Chọn");
                b.setMessage("Bạn có muốn xóa " + arrItem.get(position).getTentruyen() + " không?");
                b.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.xoaTaiTruyen(arrItem.get(idxoa).getId(), arrItem.get(idxoa).getIdchap());
                        arrItem.remove(idxoa);
                        adapter = new LvDownload(getActivity(), R.layout.download_listview, arrItem);
                        lvManga.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        tvSoTruyen.setText(arrItem.size() + " Truyện");

                        //Xóa file zip chap
                        File file = new File(Environment.getExternalStorageDirectory() + "/TruyenTranhVL/" + arrId.get(idxoa).getTentruyen() + " - Chap " + arrId.get(idxoa).getIdchap() + ": " + arrId.get(idxoa).getTenchap() + ".zip");
                        file.delete();

                        //Xóa thư mục chap
                        File file2 = new File(Environment.getExternalStorageDirectory() + "/TruyenTranhVL/" + arrId.get(idxoa).getTentruyen() + " - Chap " + arrId.get(idxoa).getIdchap() + ": " + arrId.get(idxoa).getTenchap());
                        deleteRecursive(file2);
                        Toast.makeText(getActivity(), "Xóa Thành Công!", Toast.LENGTH_SHORT).show();

                        //Refresh fragment
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.detach(DownloadFragment.this).attach(DownloadFragment.this).commit();
                    }
                });

                b.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                b.create().show();
                return true;
            }
        });

        return rootView;
    }

    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
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
            //Toast.makeText(getActivity(), Environment.getExternalStorageDirectory()+"", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < arrId.size(); i++) {
                    lvDownloadItem = new LvDownloadItem(arrId.get(i).getIdtruyen(), arrId.get(i).getIdchap(), arrId.get(i).getAvatar(), arrId.get(i).getTentruyen(), arrId.get(i).getTacgia(), arrId.get(i).getTenchap(), "0");
                    arrItem.add(lvDownloadItem);
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
                adapter = new LvDownload(getActivity(), R.layout.download_listview, arrItem);
                lvManga.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
            }
            if (arrItem.size() == 0) {
                tvYeuThich1.setVisibility(View.VISIBLE);
                tvYeuThich2.setVisibility(View.VISIBLE);
            }
            tvSoTruyen.setText(arrItem.size() + " Chap");
            swipeRefreshLayout.setRefreshing(false);
        }

    }

}
