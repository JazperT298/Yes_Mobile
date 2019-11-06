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
                    "Authorization:key=AAAAo2Mb6M4:APA91bHX7vu4rhC8dNGfgf_7rVifNX_GliqABaQqWigBQpqh82dZh_8h8JhKs7pZStzLGfbM0mXJkJx7JJwspKzXt3VwJo7e945N4bQxGZ1iXJBrJc-0hJxSmjxxHFv0DCiBEdWZvsbi"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
