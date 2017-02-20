package truyentranh.vl.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import truyentranh.vl.R;
import truyentranh.vl.model.TabItem;

public class LvNavigation extends
        ArrayAdapter<TabItem> {
    Activity context = null;
    ArrayList<TabItem> myArray = null;
    int layoutId;

    public LvNavigation(Activity context, int layoutId, ArrayList<TabItem> arr) {
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.myArray = arr;
    }

    public View getView(int position, View convertView,
                        ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(layoutId, null);
        if (myArray.size() > 0 && position >= 0) {
            TextView txtTen = (TextView)
                    convertView.findViewById(R.id.txtTen);
            txtTen.setText(myArray.get(position).getTxtTen());
            try {
                ImageView tvIcon = (ImageView)
                        convertView.findViewById(R.id.tvIcon);
                tvIcon.setImageResource(Integer.valueOf(myArray.get(position).getTvIcon()));
            } catch (Exception e) {
            }

        }
        return convertView;
    }

    @Override
    public TabItem getItem(int position) {
        return myArray.get(position);
    }

    @Override
    public int getCount() {
        return myArray.size();
    }
}