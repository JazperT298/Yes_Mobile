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
import com.theyestech.yes_mobile.activities.MessageActivity;

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

        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();

        Bundle b=new Bundle();
        b.putString("title",title);
        b.putString("message",message);
        Intent intent = new Intent(this,Notification.class);
        intent.putExtra("notificationData", "This is the message from the notification.");
        intent.putExtras(b);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
        notificationBuilder.setSmallIcon(android.R.drawable.ic_lock_idle_alarm);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());

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
        String role = "EDUCATOR";

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        //intent.putExtra("ROLE", role);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        bundle.putString("ROLE", role);
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
        String role = "EDUCATOR";
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        assert user != null;
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        //intent.putExtra("ROLE", role);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        bundle.putString("ROLE", role);
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
