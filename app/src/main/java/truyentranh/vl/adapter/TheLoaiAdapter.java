package truyentranh.vl.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import truyentranh.vl.R;
import truyentranh.vl.model.LvTheLoai;

/**
 * Created by salih.yalcin on 21.2.2017.
 */

public class TheLoaiAdapter extends ArrayAdapter<LvTheLoai> {
    private ArrayList<LvTheLoai> originalItems;
    private ArrayList<LvTheLoai> filteredItems;
    private ArrayList<LvTheLoai> notFilteredItems;
    private ArrayList<LvTheLoai> checkedItems;
    private Filter filter;
    private int[] rainbow;

    public TheLoaiAdapter(Activity context, ArrayList<LvTheLoai> arrayList) {
        super(context, R.layout.custom_listview_theloai, arrayList);
        this.originalItems = arrayList;
        this.filteredItems = arrayList;
        this.notFilteredItems = new ArrayList<>(arrayList);
        this.filter = new ModelFilter();
        this.rainbow = context.getResources().getIntArray(R.array.rainbow);
        this.checkedItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ModelFilter();
        }
        return filter;
    }

    @Override
    public int getCount() {
        return originalItems.size();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        final LvTheLoai theloaiItem = originalItems.get(position);

        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.custom_listview_theloai, parent, false);
            holder.tvTheLoai = (TextView) convertView.findViewById(R.id.tvTheLoai);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holder.cbTheLoai = (CheckBox) convertView.findViewById(R.id.cbTheLoai);

            convertView.setTag(holder);
            holder.cbTheLoai.setTag(theloaiItem);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.cbTheLoai.getTag();
        }

        holder.tvTheLoai.setText(theloaiItem.getTheloai());
        holder.cbTheLoai.setChecked(theloaiItem.isChecked());
        GradientDrawable bgShape = (GradientDrawable) holder.ivIcon.getBackground().getCurrent();
        bgShape.setColor(rainbow[position % 10]);

        holder.cbTheLoai.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CheckBox cb = (CheckBox) v;
                if (originalItems.get(position).isChecked()) {
                    cb.setSelected(false);
                    originalItems.get(position).setChecked(false);
                    notifyDataSetChanged();
                } else {
                    cb.setSelected(true);
                    originalItems.get(position).setChecked(true);
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }


    @Override
    public LvTheLoai getItem(int i) {
        return originalItems.get(i);
    }


    private class ModelFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            String prefix = constraint.toString().toLowerCase();

            if (prefix.length() == 0) {
                ArrayList<LvTheLoai> list = new ArrayList<>(notFilteredItems);
                results.values = list;
                results.count = list.size();
            } else {
                final ArrayList<LvTheLoai> list = new ArrayList<>(notFilteredItems);
                final ArrayList<LvTheLoai> nlist = new ArrayList<>();
                int count = list.size();

                for (int i = 0; i < count; i++) {
                    final LvTheLoai singleItem = list.get(i);
                    final String value = singleItem.getTheloai().toLowerCase();
                    if (value.contains(prefix)) {
                        nlist.add(singleItem);
                    }
                    results.values = nlist;
                    results.count = nlist.size();
                }
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            filteredItems = (ArrayList<LvTheLoai>) results.values;
            originalItems.clear();
            notifyDataSetChanged();
            int count = filteredItems.size();
            //..
            for (int i = 0; i < count; i++) {
                originalItems.add(filteredItems.get(i));
                notifyDataSetInvalidated();
            }


        }

    }


    @Override
    public boolean hasStableIds() {
        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public long getItemId(int position) {
        return filteredItems.get(position).hashCode();
    }

    @Override
    public int getViewTypeCount() {

        return getCount();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }


    private static class ViewHolder {
        private TextView tvTheLoai;
        private ImageView ivIcon;
        private CheckBox cbTheLoai;

    }

    public ArrayList<LvTheLoai> getCheckedItems() {
        checkedItems.clear();
        for (LvTheLoai theloai : originalItems) {
            if (theloai.isChecked()) {
                checkedItems.add(theloai);
            }
        }
        return checkedItems;
    }


}