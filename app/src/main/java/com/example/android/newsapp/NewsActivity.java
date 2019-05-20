package com.example.android.newsapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>{

    private NewsAdapter mAdapter;

    private void setProgressBarVisiblity(boolean isVisible) {
        ProgressBar progressBar = findViewById(R.id.progressBar);

        int visibility = isVisible ? View.VISIBLE : View.GONE;

        progressBar.setVisibility(visibility);
    }

    private void openNewsURL(String url) {
        Uri uri = Uri.parse(url);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void setNewsURLs(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News news = mAdapter.getItem(position);

                String url = news.getUrl();

                if (!TextUtils.isEmpty(url)) {
                    openNewsURL(news.getUrl());
                }
            }
        });
    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        boolean isConnected = networkInfo != null && networkInfo.isConnected();

        return isConnected;
    }

    private void initializeNewsList() {
        ListView newsListView = findViewById(R.id.newsList);

        mAdapter = new NewsAdapter(this, R.layout.news_activity, new ArrayList<News>());

        newsListView.setAdapter(mAdapter);

        setNewsURLs(newsListView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        if(!isConnected()) {
            setProgressBarVisiblity(false);

            return;
        }

        initializeNewsList();

        LoaderManager.getInstance(this).initLoader(1, null, this).forceLoad();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
         return new NewsLoader(NewsActivity.this, Constants.NEWS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        setProgressBarVisiblity(false);

        if(news == null) {
            return;
        }

        mAdapter.addAll(news);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }
}
