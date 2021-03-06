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
    //private static final String BASE_URL = "http://192.168.1.6/yes_tech/controllerClass/";
    //BACK UP IP
    //private static final String BASE_URL = "http://ec2-3-0-89-51.ap-southeast-1.compute.amazonaws.com/controllerClass/";
    //LIVE IP
    private static final String BASE_URL = "https://theyestech.com/controllerClass/";

    private static final String FILE_URL = "https://yestechfreeium.s3-ap-southeast-1.amazonaws.com/controllerClass/";

    private static AsyncHttpClient client = new AsyncHttpClient();

//    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.post(context, getAbsoluteUrl(url), params, responseHandler);
//    }

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), null, params, null, responseHandler);
    }

    public static void post2(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postLogin(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), null, params, null, responseHandler);
    }

    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(context, getAbsoluteUrl(url), params, responseHandler);
    }


    public static void defaultPost(Context ctx, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        /* Fixed Login problem where second login attempt after logout changes content type of data */
        /* Problem - Can't Login after logout from previous account */
        /* Cause - Content type is changed or has been added other type due to other http call (read_inventory) */
        /* Solution - Create new AssyncHttp Instance to ensure no previous content type or header has been set */

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.post(ctx, getAbsoluteUrl(url), params, responseHandler);
    }


    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    public static String getBaseURL() {
        return BASE_URL.replace("api/", "");
    }

    public static String getNewsfeedDir() {
        return FILE_URL.replace("controllerClass/", "newsfeed-files/");
    }

    public static String getVideoLabDir() {
        return FILE_URL.replace("controllerClass/", "");
    }

    public static String getProfileDir() {
        return FILE_URL.replace("controllerClass/", "user_images/");
    }

    public static String getSubjectDir() {
        return FILE_URL.replace("controllerClass/", "subject-files/");
    }

    public static String getTopicDir() {
        return FILE_URL.replace("controllerClass/", "topic-files/");
    }

    public static String getQuizDir() {
        return FILE_URL.replace("controllerClass/", "");
    }

    public static String getStickerDir() {
        return FILE_URL.replace("controllerClass/", "img/");
    }

    public static String getNotesDir() {
        return FILE_URL.replace("controllerClass/", "notes-files/");
    }

}
