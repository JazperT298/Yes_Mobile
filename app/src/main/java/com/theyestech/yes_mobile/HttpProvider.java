package com.theyestech.yes_mobile;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.theyestech.yes_mobile.models.UserEducator;

public class HttpProvider {

    //OFFICE IP
//    private static final String BASE_URL = "http://192.168.168.7/yes_tech/controllerClass/";

    //MY IP
//    private static final String BASE_URL = "http://192.168.43.8/yes_tech/controllerClass/";

    //LIVE IP
   private static final String BASE_URL = "https://theyestech.com/controllerClass/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void getLogin(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
        client.addHeader("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)");
        client.setEnableRedirects(true);
        client.get(context, getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postLogin(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), null, params, null, responseHandler);
    }

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static String getBaseURL() {
        return BASE_URL.replace("api/", "");
    }

    public static String getNewsfeedDir() {
        return BASE_URL.replace("controllerClass/", "newsfeed-files/");
    }

    public static String getProfileDir() {
        return BASE_URL.replace("controllerClass/", "user_images/");
    }

    public static String getSubjectDir() {
        return BASE_URL.replace("controllerClass/", "subject-files/");
    }

    public static String getTopicDir() {
        return BASE_URL.replace("controllerClass/", "topic-files/");
    }

    public static String getQuizDir() {
        return BASE_URL.replace("controllerClass/", "");
    }

    public static String getStickerDir() {
        return BASE_URL.replace("controllerClass/", "img/");
    }

}
