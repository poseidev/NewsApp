package com.example.android.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class NewsAPIUtility {

    private final static String LOG_TAG = NewsAPIUtility.class.getSimpleName();

    private NewsAPIUtility() {
    }

    private static void logError(String message) {
        Log.e(LOG_TAG, message);
    }

    private static URL getURL(String urlAddress) throws MalformedURLException {
        URL url = new URL(urlAddress);

        return url;
    }

    private static HttpURLConnection getURLConnection(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.connect();

        return urlConnection;
    }

    private static InputStream getInputStream(HttpURLConnection connection) throws IOException {

        int responseCode = connection.getResponseCode();

        if(responseCode == 200) {
            return connection.getInputStream();
        }
        else
        {
            logError("getInputStream - Response code = " + String.valueOf(responseCode));
        }

        return null;
    }

    private static  String getJSONResponse(InputStream stream) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(stream, Charset.forName("UTF-8"));
        BufferedReader bufferedReader  = new BufferedReader(streamReader);

        StringBuilder responseBuilder = new StringBuilder();

        String line = bufferedReader.readLine();

        while(line != null) {
            responseBuilder.append(line);
            line = bufferedReader.readLine();
        }

        return responseBuilder.toString();
    }

    private static Date formatDate(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date formattedDate = null;

        formattedDate = dateFormat.parse(dateString);

        return formattedDate;
    }

    private static List<News> getNewsListFromJSON(String jsonResponse) throws JSONException, ParseException {


        JSONObject jsonObject = new JSONObject(jsonResponse);

        JSONObject jsonObjectResult = jsonObject.getJSONObject("response");

        JSONArray newsResults = jsonObjectResult.getJSONArray("results");

        Integer resultsLength = newsResults.length();

        List<News> newsList = resultsLength > 0 ? new ArrayList<News>() : null;

        for (int i = 0; i < resultsLength; i++) {
            JSONObject newsItem = newsResults.getJSONObject(i);

            String title = newsItem.getString(Constants.NEWS_API_FIELD_TITLE);
            String section = newsItem.getString(Constants.NEWS_API_FIELD_SECTION);
            String webUrl = newsItem.getString(Constants.NEWS_API_FIELD_URL);
            String dateString = newsItem.getString(Constants.NEWS_API_FIELD_DATE);

            News news = new News();
            news.setTitle(title);
            news.setSection(section);
            news.setUrl(webUrl);

            Date publishDate = formatDate(dateString);

            news.setPublishDate(publishDate);

            newsList.add(news);
        }

        return newsList;
    }

    public static List<News> getNewsList(String urlAddress) {
        List<News> newsList = null;

        try {
            URL url = getURL(urlAddress);

            HttpURLConnection connection = getURLConnection(url);

            InputStream stream = getInputStream(connection);

            String jsonResponse = getJSONResponse(stream);

            newsList = getNewsListFromJSON(jsonResponse);
        }
        catch (Exception e) {
            logError("getNewsList - " + e.getMessage());
        }

        return newsList;
    }
}
