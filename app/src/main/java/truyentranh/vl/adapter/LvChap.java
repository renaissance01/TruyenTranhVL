package truyentranh.vl.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import truyentranh.vl.R;
import truyentranh.vl.database.Database;
import truyentranh.vl.model.DbDaXem;
import truyentranh.vl.model.DbView;
import truyentranh.vl.model.LvChapItem;
import truyentranh.vl.model.TabItem;
import truyentranh.vl.slideimages.ShowImages;

public class LvChap extends
        ArrayAdapter<LvChapItem> {
    Activity context = null;
    ArrayList<LvChapItem> myArray = null;
    int layoutId;
    Database db;

    public LvChap(Activity context, int layoutId, ArrayList<LvChapItem> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {

        db = new Database(getContext());

        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);
        if (myArray.size() > 0 && position >= 0) {

            TextView txtStt = (TextView)
                    convertView.findViewById(R.id.txtStt);
            txtStt.setText(myArray.get(position).getIdchap());

            if(db.checkTrangDangXem2((Integer.parseInt(myArray.get(position).getIdtruyen())) + "",(Integer.parseInt(myArray.get(position).getIdchap())-1)+"")){
                //Toast.makeText(getContext(), myArray.get(position).getTentruyen() + " - Chap " + myArray.get(position).getIdchap() + ": " + myArray.get(position).getChuong(), Toast.LENGTH_SHORT).show();
                TextView tvTenChap = (TextView)
                        convertView.findViewById(R.id.tvTenChap);
                tvTenChap.setText(myArray.get(position).getTenchap());
                tvTenChap.setTextColor(Color.MAGENTA);
            }else{
                TextView tvTenChap = (TextView)
                        convertView.findViewById(R.id.tvTenChap);
                tvTenChap.setText(myArray.get(position).getTenchap());
            }

            TextView tvNgay = (TextView)
                    convertView.findViewById(R.id.tvNgay);
            tvNgay.setText(myArray.get(position).getNgay());

            //Toast.makeText(getContext(), (Integer.parseInt(myArray.get(position).getStt()))+"", Toast.LENGTH_SHORT).show();
            if(db.checkIdChapDaXem((Integer.parseInt(myArray.get(position).getIdchap())-1)+"")){
                ImageView ivDaDoc = (ImageView) convertView.findViewById(R.id.ivDaDoc);
                ivDaDoc.setImageResource(R.drawable.check);
            }
        }
        return convertView;
    }

}