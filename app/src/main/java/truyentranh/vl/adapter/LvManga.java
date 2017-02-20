package truyentranh.vl.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import truyentranh.vl.R;
import truyentranh.vl.database.Database;
import truyentranh.vl.model.LvMangaItem;

public class LvManga extends
        ArrayAdapter<LvMangaItem> {
    Activity context = null;
    ArrayList<LvMangaItem> myArray = null;
    int layoutId;
    Database db;

    public LvManga(Activity context, int layoutId, ArrayList<LvMangaItem> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {

        db = new Database(getContext());

        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(layoutId, parent, false);
        if (myArray.size() > 0 && position >= 0) {

            ImageView tvAvatar = (ImageView)
                    row.findViewById(R.id.tvAvatar);
            new ImageLoadTask(myArray.get(position).getAvatar(), tvAvatar).execute();

            //Toast.makeText(getContext(), (Integer.parseInt(myArray.get(position).getId())-1)+"", Toast.LENGTH_SHORT).show();
            try {
                if (db.checkTrangDangXem3((Integer.parseInt(myArray.get(position).getId()) - 1) + "")) {
                    TextView tvTenTruyen = (TextView)
                            row.findViewById(R.id.tvTenTruyen);
                    tvTenTruyen.setText(myArray.get(position).getTentruyen());
                    tvTenTruyen.setTextColor(Color.MAGENTA);
                } else {
                    TextView tvTenTruyen = (TextView)
                            row.findViewById(R.id.tvTenTruyen);
                    tvTenTruyen.setText(myArray.get(position).getTentruyen());
                }
            } catch (Exception e) {
                TextView tvTenTruyen = (TextView)
                        row.findViewById(R.id.tvTenTruyen);
                tvTenTruyen.setText(myArray.get(position).getTentruyen());
            }

            TextView tvTacGia = (TextView)
                    row.findViewById(R.id.tvTacGia);
            tvTacGia.setText(myArray.get(position).getTacgia());

            TextView tvChuong = (TextView)
                    row.findViewById(R.id.tvChuong);
            tvChuong.setText(myArray.get(position).getChuong());

            TextView tvLuotXem = (TextView)
                    row.findViewById(R.id.tvLuotXem);
            tvLuotXem.setText(myArray.get(position).getLuotxem());

        }
        //Sole Màu Nền
        if (position % 2 == 0) {
            row.setBackgroundColor(Color.parseColor("#f4f5f6"));
        } else {
            row.setBackgroundColor(Color.parseColor("#dde4ed"));
        }

        return row;
    }

    @Override
    public LvMangaItem getItem(int position) {
        return myArray.get(position);
    }

    @Override
    public int getCount() {
        return myArray.size();
    }


    class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}