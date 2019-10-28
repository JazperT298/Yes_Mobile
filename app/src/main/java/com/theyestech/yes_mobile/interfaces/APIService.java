package com.theyestech.yes_mobile.interfaces;

import com.theyestech.yes_mobile.notifications.MyResponse;
import com.theyestech.yes_mobile.notifications.Sender;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA5HKPkLQ:APA91bH2kGd0fDlHm8jKR4D1RG9Tyw4lXerqNfD3yVfEqAqD-OYtAcLNucYLH7wF7DELr3-RbzgILmh4iDeliGjlNWsJmAUyUvUoOixBkArYrfI1DbU1ksL9irX_rKIPKwMCpEWRTRCW"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
