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
    private static final String BASE_URL = "http://192.168.168.207/yes_tech/controllerClass/";

    // MY IP
//    private static final String BASE_URL = "http://ec2-18-139-228-46.ap-southeast-1.compute.amazonaws.com/yes_tech/controllerClass/";
//    private static final String BASE_URL = "http://10.5.49.37/LearningApplication/yes_tech/controllerClass/";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static SyncHttpClient syncHttpClient = new SyncHttpClient();

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

    public static String getTopicDir(){
        return BASE_URL.replace("controllerClass/", "topic-files/");
    }

}
