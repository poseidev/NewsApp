package com.example.android.newsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsActivity extends AppCompatActivity
        implements
            LoaderManager.LoaderCallbacks<List<News>>,
            SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int  NEWS_LOADER_ID = 1;

    private NewsAdapter mAdapter;

    private void setProgressBarClarity(boolean isVisible) {
        ProgressBar progressBar = findViewById(R.id.progressBar);

        int visibility = isVisible ? View.VISIBLE : View.GONE;

        progressBar.setVisibility(visibility);
    }

    private void showConfirmationMessage(String message, DialogInterface.OnClickListener positiveButtonListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("Yes", positiveButtonListener);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
                final News news = mAdapter.getItem(position);

                String url = news.getUrl();

                if (!TextUtils.isEmpty(url)) {
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openNewsURL(news.getUrl());
                        }
                    };

                    showConfirmationMessage(getString(R.string.confirmationOpenNews), listener);
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

    private void restartLoader() {
        mAdapter.clear();

        setProgressBarClarity(true);

        LoaderManager.getInstance(this).restartLoader(NEWS_LOADER_ID, null, this);
    }

    private void hideKeyboard() {
        View searchButton = findViewById(R.id.searchButton);
        InputMethodManager imManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imManager.hideSoftInputFromWindow(searchButton.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initializeSearchButtonListener() {
        View searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartLoader();

                hideKeyboard();
            }
        });
    }

    private void initializeNewsList() {
        ListView newsListView = findViewById(R.id.newsList);

        mAdapter = new NewsAdapter(this, R.layout.news_activity, new ArrayList<News>());

        newsListView.setAdapter(mAdapter);

        setNewsURLs(newsListView);
    }

    private void initializePreferenceListener() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    private List<Map<String, String>> getSettings() {
        List<Map<String, String>> settings = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = preferences.getString(getString(R.string.settingsOrderByKey), getString(R.string.settingsOrderByDefault));
        String pageSize = preferences.getString(getString(R.string.settingsPageSizeKey), getString(R.string.settingsPageSizeDefault));

        String orderByKey = getString(R.string.settingsOrderByKey);
        String pageSizeKey = getString(R.string.settingsPageSizeKey);

        Map<String, String> orderByMap = new HashMap<>();
        Map<String, String> pageSizeMap = new HashMap<>();

        orderByMap.put(orderByKey, orderBy);
        pageSizeMap.put(pageSizeKey, pageSize);

        settings.add(orderByMap);
        settings.add(pageSizeMap);

        return settings;
    }

    private String getUrl(String baseUrl, String searchText) {
        Uri uri = Uri.parse(baseUrl);

        Uri.Builder uriBuilder = uri.buildUpon();

        boolean isEmptySearchText = TextUtils.isEmpty(searchText);
        if(!isEmptySearchText) {
            uriBuilder.appendQueryParameter(getString(R.string.apiFieldSearchText), searchText);
        }

        List<Map<String, String>> settings = getSettings();
        for (Map<String, String> settingItem : settings) {

            String key = settingItem.keySet().toArray()[0].toString();
            String value = settingItem.get(key);

            // If search text is NOT PROVIDED results are sorted by latest date
            if(isEmptySearchText &&
                    key.equals(getString(R.string.settingsOrderByKey)) &&
                    value.equals(getString(R.string.settingsOrderByValueRelevance))) {

                value = getString(R.string.settingsOrderByValueLatest);
            }

            uriBuilder.appendQueryParameter(key, value);
        }

        uriBuilder.appendQueryParameter(getString(R.string.apiFieldShowFields), getString(R.string.apiFieldShowFieldsThumbnail));
        uriBuilder.appendQueryParameter(getString(R.string.apiFieldAPIKey), getString(R.string.apiFieldAPIKeyDefault));

        return uriBuilder.toString();
    }

    private String getUrl(String baseUrl) {
        return getUrl(baseUrl, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        if(!isConnected()) {
            setProgressBarClarity(false);

            return;
        }

        initializeSearchButtonListener();

        initializeNewsList();

        initializePreferenceListener();

        LoaderManager.getInstance(this).initLoader(NEWS_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId)
        {
            case R.id.menuItemSettings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        TextView searchTextView = findViewById(R.id.searchText);

        String searchKeyword = searchTextView.getText().toString();

        String url = TextUtils.isEmpty(searchKeyword) ?
                getUrl(Constants.NEWS_REQUEST_URL) :
                getUrl(Constants.NEWS_REQUEST_URL, searchKeyword);

       /* String url = getUrl(Constants.NEWS_REQUEST_URL);*/

        return new NewsLoader(NewsActivity.this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        setProgressBarClarity(false);

        if(news == null) {
            return;
        }

        mAdapter.clear();

        mAdapter.addAll(news);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        restartLoader();
    }
}
