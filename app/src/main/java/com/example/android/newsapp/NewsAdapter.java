package com.example.android.newsapp;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NewsAdapter extends ArrayAdapter<News> {
    public NewsAdapter(Context context, int resource, List<News> newsList) {
        super(context, resource, newsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_item,
                    parent,
                    false); }

        News currentNews = getItem(position);

        // Title
        TextView titleText = listItemView.findViewById(R.id.textTitle);
        titleText.setText(currentNews.getTitle());

        // PublishDate
        TextView dateText = listItemView.findViewById(R.id.textDate);
        Date publishDate = currentNews.getPublishDate();
        String dateString = formatDate(publishDate);
        dateText.setText(dateString);

        // Author
        String author = currentNews.getAuthor();
        TextView authorText = listItemView.findViewById(R.id.textAuthor);
        if(!TextUtils.isEmpty(author)) {

            authorText.setText(author);
        }
        else{
            authorText.setVisibility(View.GONE);
        }

        // Section
        TextView sectionText = listItemView.findViewById(R.id.textSection);
        sectionText.setText(currentNews.getSection());

        // Image
        /*Bitmap image = currentNews.getImage();
        if(image != null) {
            ImageView imageView = listItemView.findViewById(R.id.imageView);
            imageView.setImageBitmap(image);
        }*/

        // For ImageDownloader AsyncTask
       /* if(!TextUtils.isEmpty(imageUrl)) {
            ImageView imageView = listItemView.findViewById(R.id.imageView);
            ImageDownloader imageDownloader = new ImageDownloader(imageView);
            imageDownloader.execute(imageUrl);
        }*/

        String imageUrl = currentNews.getImageUrl();
        ImageView imageView = listItemView.findViewById(R.id.imageView);

        Glide.with(getContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);

        return listItemView;
    }

    private String  formatDate(Date publishDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                getContext().getString(R.string.dateFormat),
                Locale.getDefault());

        return  dateFormat.format(publishDate);
    }

    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView mImageView;

        public ImageDownloader(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;

            String url = urls[0];

            try {
                InputStream inputStream = new URL (url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            mImageView.setImageBitmap(image);
        }
    }
}
