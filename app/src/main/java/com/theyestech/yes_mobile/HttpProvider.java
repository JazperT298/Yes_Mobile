package com.theyestech.yes_mobile;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

public class HttpProvider {

//    private static final String BASE_URL = "https://www.yahshuapayrollonline.com/api/";
    // private static final String BASE_URL = "http://intern.picpacdomisor.org/webapi/api/";
    //private static final String BASE_URL = "http://127.0.0.1/yes/api/api/student/";

    //OFFICE IP
    //private static final String BASE_URL = "http://192.168.168.207/yes_tech/controllerClass/";

    //MY IP
//    private static final String BASE_URL = "http://192.168.254.157/yes_tech/controllerClass/";

    //LIVE IP
   private static final String BASE_URL = "http://theyestech.com/controllerClass/";

    //MICHAEL IP
//    private static final String BASE_URL = "http://192.168.1.111/LearningApplication/yes_tech/controllerClass/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(context, getAbsoluteUrl(url), params, responseHandler);
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

}
