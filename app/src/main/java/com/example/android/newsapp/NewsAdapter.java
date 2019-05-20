package com.example.android.newsapp;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        return listItemView;
    }

    private String  formatDate(Date publishDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                getContext().getString(R.string.dateFormat),
                Locale.getDefault());

        return  dateFormat.format(publishDate);
    }
}
