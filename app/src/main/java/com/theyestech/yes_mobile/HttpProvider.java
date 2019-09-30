package com.theyestech.yes_mobile;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import cz.msebera.android.httpclient.entity.StringEntity;

public class HttpProvider {

//    private static final String BASE_URL = "https://www.yahshuapayrollonline.com/api/";
   // private static final String BASE_URL = "http://intern.picpacdomisor.org/webapi/api/";
    //private static final String BASE_URL = "http://127.0.0.1/yes/api/api/student/";

    //EARL IP
    private static final String BASE_URL = "http://192.168.43.8/yes_tech/controllerClass/";

    // MY IP
//    private static final String BASE_URL = "http://ec2-18-139-228-46.ap-southeast-1.compute.amazonaws.com/yes_tech/controllerClass/";
//    private static final String BASE_URL = "http://10.5.49.37/LearningApplication/yes_tech/controllerClass/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncHttpClient = new SyncHttpClient();

//    public static void post(Context context, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
//        client.addHeader("Authorization", "Token " + UserSessionStudent.getToken(context));
//        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        client.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)");
//        client.post(context, getAbsoluteUrl(url), entity, null, responseHandler);
//    }

//    public static void get(Context context, String url, RequestParams params, JsonHttpResponseHandler jsonHttpResponseHandler) {
//        client.addHeader("Authorization", "Token " + UserSessionStudent.getToken(context));
//        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        client.setMaxRetriesAndTimeout(2, 5);
//        client.get(context, getAbsoluteUrl(url), params, jsonHttpResponseHandler);
//    }

//    public static void postRegister(Context context, String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
//        client.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        client.post(context, getAbsoluteUrl(url), entity, null, responseHandler);
//    }

//    public static void post(Context context, String url, StringEntity entity, boolean possibleLongOperation, AsyncHttpResponseHandler responseHandler) {
//        client.addHeader("Authorization", "Token " + UserSessionStudent.getToken(context));
//        client.addHeader("Content-Type", "application/json;charset=UTF-8");
//        client.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)");
//
//        if (possibleLongOperation) {
//            client.setConnectTimeout(50000);
//            client.setTimeout(50000);
//            client.setResponseTimeout(50000);
//        }
//
//        client.post(context, BASE_URL + (url), entity, null, responseHandler);
//    }

//    public static void postSync(Context context, String url, StringEntity entity, boolean possibleLongOperation, AsyncHttpResponseHandler responseHandler) {
//        syncHttpClient.addHeader("Authorization", "Token " + UserSessionStudent.getToken(context));
//        syncHttpClient.addHeader("Content-Type", "application/json;charset=UTF-8");
//        syncHttpClient.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.2; .NET CLR 1.0.3705;)");
//
//        if (possibleLongOperation) {
//            syncHttpClient.setConnectTimeout(50000);
//            syncHttpClient.setTimeout(50000);
//            syncHttpClient.setResponseTimeout(50000);
//        }
//
//        syncHttpClient.post(context, getAbsoluteUrl(url), entity, null, responseHandler);
//    }

    public static void post(Context ctx, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
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

    public static String getNewsfeedDir(){
        return BASE_URL.replace("controllerClass/", "newsfeed-files/");
    }

    public static String getProfileImageDir(){
        return BASE_URL.replace("controllerClass/", "user_images/");
    }

    public static String getSubjectDir(){
        return BASE_URL.replace("controllerClass/", "subject-files/");
    }

}
