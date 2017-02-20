package truyentranh.vl.slideimages;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

public class LocalImageAdapter extends PagerAdapter {
    Context context;
    String imageArray[];
    private ProgressBar bar;

    public LocalImageAdapter(Context context, String[] imgArra, ProgressBar bar) {
        imageArray = imgArra;
        this.context = context;
        this.bar = bar;
    }

    public int getCount() {
        return imageArray.length;
    }

    public Object instantiateItem(View collection, int position) {
        ImageView view = new ImageView(context);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        view.setScaleType(ScaleType.FIT_XY);
        try {
            new ImageLoadTask(imageArray[position] + "", view, bar).execute();
        } catch (Exception e) {
        }
        ((ViewPager) collection).addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;
        private ProgressBar bar;

        public ImageLoadTask(String url, ImageView imageView, ProgressBar bar) {
            this.url = url;
            this.imageView = imageView;
            this.bar = bar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                String path = Environment.getExternalStorageDirectory().toString()
                        + "/TruyenTranhVL";
                Bitmap myBitmap = BitmapFactory.decodeFile(path + url);
                try {
                    return myBitmap;
                } catch (Exception e) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                imageView.setImageBitmap(result);
                bar.setVisibility(View.GONE);
            }
        }

    }
}