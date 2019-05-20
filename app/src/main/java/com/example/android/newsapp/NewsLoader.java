package com.example.android.newsapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;

    private String LOG_TAG = NewsLoader.class.getSimpleName();

    public NewsLoader(Context context, String url) {
        super(context);

        mUrl = url;
    }

    private void logMessage(String message) {
        Log.v(LOG_TAG, message);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();

        logMessage(LOG_TAG + ": onStartLoading");
    }

    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<News> newsList = NewsAPIUtility.getNewsList(mUrl);

        return newsList;
    }
}
