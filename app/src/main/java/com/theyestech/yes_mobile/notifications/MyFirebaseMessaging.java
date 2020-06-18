package com.theyestech.yes_mobile.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.theyestech.yes_mobile.MainActivity;
import com.theyestech.yes_mobile.activities.ChatConversationActivity;
import com.theyestech.yes_mobile.activities.MessageActivity;
import com.theyestech.yes_mobile.models.ChatThread;
import com.theyestech.yes_mobile.models.Contact;
import com.theyestech.yes_mobile.models.Sticker;
import com.theyestech.yes_mobile.utils.Debugger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert sented != null;
        if (firebaseUser != null && sented.equals(firebaseUser.getUid())){
            assert currentUser != null;
            if (!currentUser.equals(user)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    sendOreoNotification(remoteMessage);
                } else {
                    sendNotification(remoteMessage);
                }
            }
        }

//        String title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
//        String message = remoteMessage.getNotification().getBody();
//
//        Bundle b=new Bundle();
//        b.putString("title",title);
//        b.putString("message",message);
//        Intent intent = new Intent(this,Notification.class);
//        intent.putExtra("notificationData", "This is the message from the notification.");
//        intent.putExtras(b);
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//        notificationBuilder.setContentTitle(title);
//        notificationBuilder.setContentText(message);
//        notificationBuilder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm);
//        notificationBuilder.setAutoCancel(true);
//        notificationBuilder.setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0,notificationBuilder.build());

        //Notification for student
//        String CHANNEL_ID = "myChannelID1";
//        String title = getIntent().getExtras().getString("title");
//        String message = getIntent().getExtras().getString("message");
//        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID);
//        notification.setAutoCancel(true);
//        notification.setSmallIcon(android.R.drawable.ic_lock_idle_alarm);
//        notification.setContentTitle(title);
//        notification.setContentText(message);
    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String name = remoteMessage.getData().get("name");
        String threadId = remoteMessage.getData().get("threadId");
        String photo_name = remoteMessage.getData().get("photo_name");
        //String contact = remoteMessage.getData().get("contact");
        //String thread = remoteMessage.getData().get("thread");
        Class<? extends Map> contact1 = remoteMessage.getData().getClass();
        Class<? extends Map> thread1 = remoteMessage.getData().getClass();

        //String role = "EDUCATOR";
        Debugger.logD("contact" + user);
        Debugger.logD("body " + body);
        Debugger.logD("title " + title);
        Debugger.logD("contact1 " + contact1);
        Debugger.logD(" threadId " + threadId);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatConversationActivity.class);
        //intent.putExtra("ROLE", role);
        Bundle bundle = new Bundle();
//        bundle.putString("CONTACT", remoteMessage.getData().get("contact"));
//        bundle.putString("THREAD", remoteMessage.getData().get("thread"));
        intent.putExtra("RECEIVER_ID", user);
        intent.putExtra("RECEIVER_NAME", name);
        intent.putExtra("RECEIVER_PHOTO", photo_name);
        intent.putExtra("THREAD_ID", threadId);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;
        if (j > 0){
            i = j;
        }

        oreoNotification.getManager().notify(i, builder.build());

    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String date = remoteMessage.getData().get("date");
        String name = remoteMessage.getData().get("name");
        String threadId = remoteMessage.getData().get("threadId");
        String photo_name = remoteMessage.getData().get("photo_name");
        Debugger.logD("contact" );
        Debugger.logD("body " + body);
        Debugger.logD("title " + title);
        Debugger.logD(" date " + date);
        Debugger.logD(" threadId " + threadId);
        //Contact contacts = remoteMessage.getClass().get("contacts");
        //String contact = remoteMessage.getData().get("contact");
        //String thread = remoteMessage.getData().get("thread");
        String role = "EDUCATOR";
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatConversationActivity.class);
        //intent.putExtra("ROLE", role);
        Bundle bundle = new Bundle();
//        intent.putExtra("CONTACT", contact);
//        intent.putExtra("THREAD", thread);
        intent.putExtra("RECEIVER_ID", user);
        intent.putExtra("RECEIVER_NAME", name);
        intent.putExtra("RECEIVER_PHOTO", photo_name);
        intent.putExtra("THREAD_ID", threadId);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if (j > 0){
            i = j;
        }

        noti.notify(i, builder.build());
    }
}
