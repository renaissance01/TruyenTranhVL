package truyentranh.vl.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import truyentranh.vl.R;
import truyentranh.vl.model.LvMangaItem;

public class LvSearch extends ArrayAdapter<LvMangaItem> {

    private ArrayList<LvMangaItem> originalList;
    private ArrayList<LvMangaItem> countryList;
    private CountryFilter filter;

    public LvSearch(Context context, int textViewResourceId,
                    ArrayList<LvMangaItem> countryList) {
        super(context, textViewResourceId, countryList);
        this.countryList = new ArrayList<LvMangaItem>();
        this.countryList.addAll(countryList);
        this.originalList = new ArrayList<LvMangaItem>();
        this.originalList.addAll(countryList);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CountryFilter();
        }
        return filter;
    }


    private class ViewHolder {
        ImageView tvAvatar;
        TextView tvTenTruyen;
        TextView tvTacGia;
        TextView tvChuong;
        TextView tvLuotXem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));
        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.manga_listview, null);

            holder = new ViewHolder();
            holder.tvAvatar = (ImageView) convertView.findViewById(R.id.tvAvatar);
            holder.tvTenTruyen = (TextView) convertView.findViewById(R.id.tvTenTruyen);
            holder.tvTacGia = (TextView) convertView.findViewById(R.id.tvTacGia);
            holder.tvChuong = (TextView) convertView.findViewById(R.id.tvChuong);
            holder.tvLuotXem = (TextView) convertView.findViewById(R.id.tvLuotXem);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LvMangaItem country = countryList.get(position);
        new ImageLoadTask(country.getAvatar(), holder.tvAvatar).execute();
        holder.tvTenTruyen.setText(country.getTentruyen());
        holder.tvTacGia.setText(country.getTacgia());
        holder.tvChuong.setText(country.getChuong());
        holder.tvLuotXem.setText(country.getLuotxem());

        if(position % 2 == 0){
            convertView.setBackgroundColor(Color.parseColor("#dde4ed"));
        }
        else {
            convertView.setBackgroundColor(Color.parseColor("#f4f5f6"));
        }

        return convertView;

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

    private class CountryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<LvMangaItem> filteredItems = new ArrayList<LvMangaItem>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    LvMangaItem country = originalList.get(i);
                    if (country.toString().toLowerCase().contains(constraint))
                        filteredItems.add(country);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            countryList = (ArrayList<LvMangaItem>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = countryList.size(); i < l; i++)
                add(countryList.get(i));
            notifyDataSetInvalidated();
        }
    }


}