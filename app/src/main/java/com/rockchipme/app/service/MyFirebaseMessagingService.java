package com.rockchipme.app.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.SplashScreenActivity;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.models.Notification;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Alisons on 4/12/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(Constants.APP_TAG, "On messgae received");
        Log.d(Constants.APP_TAG, "From: " + remoteMessage.getFrom());
        // In smartshop format of data
        //{smallIcon="", data={"largeIcon":"","smallIcon":"","group":""}, icon=***, text=***, sound=default, title=***, vibrate=default, largeIcon=****}
        processNotification(remoteMessage);

    }
    // [END receive_message]


//    @Override
//    public void handleIntent(Intent intent) {
//        processNotification(intent.getExtras());
////        startService(new Intent(getApplicationContext(), MyService.class));
//        try{
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
//                startService(new Intent(getApplicationContext(), MyService.class));
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }

    public void processNotification(Bundle bundle) {
        Log.e(Constants.APP_TAG, "processNotification:Bundle");
        try {
            if (bundle == null) {
                Log.e(Constants.APP_TAG, "processNotification:bundle:null");
                return;
            }
            Log.d(Constants.APP_TAG, "Notification data: " + bundle.getString("data"));
//            Log.d(Constants.APP_TAG, "Notification notification: " + bundle.getString("notification"));
            redirectMethod(bundle.getString("data"));
        } catch (Exception e) {
            Log.e(Constants.APP_TAG, "processNotification:" + e.toString());
            e.printStackTrace();
        }
    }

    public void processNotification(RemoteMessage remoteMessage) {
        Log.e(Constants.APP_TAG, "processNotification:RemoteMessage");
        try {
            if (remoteMessage.getData() == null) {
                Log.e(Constants.APP_TAG, "processNotification:remoteMessage:null");
                return;
            }
            Log.d(Constants.APP_TAG, "Notification data: " + remoteMessage.getData().get("data"));
//            Log.d(Constants.APP_TAG, "Notification notification: " + remoteMessage.getData().get("notification"));
            redirectMethod(remoteMessage.getData().get("data"));
        } catch (Exception e) {
            Log.e(Constants.APP_TAG, "processNotification:" + e.toString());
            e.printStackTrace();
        }
    }

    public void redirectMethod(String notificationData) {
        try {
            SharedPreferences sp = getSharedPreferences(Constants.PREF_LOGIN, 0);
            if (sp.getString(Constants.api_key, "").trim().length() < 1) {
                Log.d(Constants.APP_TAG,"apiKey null");
                return;
            }
//            String selectedLanguage = sp.getString(PrefKeys.LANG, Constants.DEFAULT_LANG);
//            String guestId = sp.getString(PrefKeys.GUEST_ID, "");

            Notification notifications = new Gson().fromJson(notificationData, Notification.class);

//            JSONObject jsonObject = new JSONObject(notificationData);
            String id = "", typeValue = "", title = "", smallDescription = "", bigDescription = "", smallImage = "", bigImage = "";
//            notificationId = jsonObject.getString("notificationId");
//            typeValue = jsonObject.getString("type_value");
//            if (selectedLanguage.equals("en")) {
//                title = jsonObject.getString("title_en");
//                smallDescription = jsonObject.getString("small_description_en");
//                bigImage = jsonObject.getString("big_image_en");
//            } else if (selectedLanguage.equals("ar")) {
//                title = jsonObject.getString("title_ar");
//                smallDescription = jsonObject.getString("small_description_ar");
//                bigImage = jsonObject.getString("big_image_ar");
//            }
//                1-Link,2-Product,3-Offer,4-Brand,5-Category,6-Details,7-Order

            if (notifications == null)
                return;



            id = notifications.notificationId;
            typeValue = notifications.value;
            smallDescription = notifications.message;
            bigImage = notifications.image;
            title = notifications.title;



            Intent intent = null;

            intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
            intent.putExtra("notification", notifications);

//            switch (notifications.type) {
//                case "1":
////                    intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
////                    intent.putExtra("link", typeValue);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    intent = new Intent(getApplicationContext(), OrderHistoryActivty.class);
//                    intent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
//                    intent.putExtra("orderId", typeValue);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    break;
//                case "2":
//                    intent = new Intent(getApplicationContext(), ItemsListActivity.class);
////                    "data":[{"catId":"5","image":"img_5","pdtCount":"36","name":"JUICE"}]
//                    intent.putExtra("catId", typeValue);
//                    try {
//                        if (notifications.notificationdate != null && notifications.notificationdate.size() > 0) {
//                            intent.putExtra("itemsCount", notifications.notificationdate.get(0).pdtCount);
//                            intent.putExtra("catName", notifications.notificationdate.get(0).name);
//                            intent.putExtra("catImg", notifications.notificationdate.get(0).getImage());
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        }
//                    } catch (Exception e){}
//                    break;
//                case "3":
//                    intent = new Intent(getApplicationContext(), ItemDetailsActivity.class);
//                    intent.putExtra("pdtId", typeValue);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    break;
////                case 4:
////                    intent = new Intent(getApplicationContext(), ProductListingActivity.class);
////                    intent.putExtra("slug", typeValue);
////                    intent.putExtra("label", title);
////                    intent.putExtra("by", "brand");
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    break;
////                case 5:
////                    intent = new Intent(getApplicationContext(), ProductListingActivity.class);
////                    intent.putExtra("slug", typeValue);
////                    intent.putExtra("label", title);
////                    intent.putExtra("by", "category");
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    break;
////                case 6:
////                    intent = new Intent(getApplicationContext(), NotificationActivity.class);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    break;
////                case 7:
////                    if (guestId.equals("")) {
////                        intent = new Intent(getApplicationContext(), OrderActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    } else {
////                        intent = new Intent(getApplicationContext(), LoginActivity.class);
////                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    }
////                    break;
////                default:
////                    intent = new Intent(getApplicationContext(), HomeActivity.class);
////                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
////                    break;
//            }
            sendNotification(intent, id, bigImage, title, smallDescription);
//            ResponseBus responseBus = new ResponseBus();
//            responseBus.FROM = "MyFirebaseMessagingService";
//            responseBus.UPDATE_TYPE = ResponseBus.NOTIFICATION;
//            EventBus.getDefault().post(responseBus);
        } catch (Exception e) {
            Log.e(Constants.APP_TAG, "redirectMethod:" + e.toString());
            e.printStackTrace();
        }
    }

    private void sendNotification(Intent intent, String id, String imageUrl, String title, String description) {
        if (intent == null) {
            return;
        }
        intent.putExtra("isFromNotification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Bitmap image = null;
        URL url = null;
        try {
            //url = new URL(messageBody.get("largeIcon"));
            url = new URL(/*Constants.NOTIFICATION_BIG_URL + */imageUrl);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            Log.e("Exception", e.toString());
        } catch (IOException e) {
            Log.e("Exception", e.toString());
        }

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.app_name);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setSmallIcon(R.drawable.app_icon);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(description);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setContentIntent(pendingIntent);

        if (image!=null) {
            notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image).setSummaryText(description));
        }



        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notficationID = 0;
        if (id.length() > 4) {
            notficationID = Integer.parseInt(id.substring(id.length() - 4));
        } else {
            notficationID = Integer.parseInt(id);
        }

        final String NOTIFICATION_CHANNEL_ID = String.valueOf(notficationID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        if (notificationManager!=null)
            notificationManager.notify(notficationID /* ID of notification */, notificationBuilder.build());

    }
}
