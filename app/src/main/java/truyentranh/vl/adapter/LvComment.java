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

public class LvComment extends
        ArrayAdapter<LvCommentItem> {
    Activity context = null;
    ArrayList<LvCommentItem> myArray = null;
    int layoutId;

    public LvComment(Activity context, int layoutId, ArrayList<LvCommentItem> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(layoutId, parent, false);
        if (myArray.size() > 0 && position >= 0) {

            TextView tvStt = (TextView)
                    row.findViewById(R.id.tvStt);
            tvStt.setText(myArray.get(position).getStt());

            TextView tvHoTen = (TextView)
                    row.findViewById(R.id.tvHoTen);
            tvHoTen.setText(myArray.get(position).getHoten());

            TextView tvThoiGian = (TextView)
                    row.findViewById(R.id.tvThoiGian);
            tvThoiGian.setText(myArray.get(position).getThoigian());

            TextView tvNoiDung = (TextView)
                    row.findViewById(R.id.tvNoiDung);
            tvNoiDung.setText(myArray.get(position).getNoidung());

        }
        //Sole Màu Nền
        if (position % 2 == 0) {
            row.setBackgroundColor(Color.parseColor("#f4f5f6"));
        } else {
            row.setBackgroundColor(Color.parseColor("#dde4ed"));
        }

        return row;
    }

}