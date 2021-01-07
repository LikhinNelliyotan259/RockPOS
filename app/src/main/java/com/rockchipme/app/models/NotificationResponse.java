package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sibin on 12/21/2017.
 */

public class NotificationResponse {

    @SerializedName("response_text")
    public ResponseText responseText;
    @SerializedName("response_code")
    public String responseCode = "";

    public class ResponseText {
        @SerializedName("notifications")
        public List<Notification> notificationsList;

        @SerializedName("success")
        public int success;
        @SerializedName("status")
        public String status;
        @SerializedName("message")
        public String message;
    }



    @SerializedName("notifications")
    public List<Notification> notifications;

//    public static class Languages {
//        @SerializedName("notificationId")
//        public int notificationId;
//        @SerializedName("notification_id")
//        public String notification_id;
//        @SerializedName("language_id")
//        public String language_id;
//        @SerializedName("title")
//        public String title;
//        @SerializedName("small_description")
//        public String small_description;
//        @SerializedName("big_description")
//        public String big_description;
//        @SerializedName("small_image")
//        public String small_image;
//        @SerializedName("big_image")
//        public String big_image;
//        @SerializedName("created_at")
//        public String created_at;
//        @SerializedName("updated_at")
//        public String updated_at;
//    }

//    public static class Notification {
//        @SerializedName("notificationId")
//        public int notificationId;
//        @SerializedName("notification_for")
//        public String notification_for;
//        @SerializedName("notification_type")
//        public String notification_type;
//        @SerializedName("type_value")
//        public String type_value;
//        @SerializedName("status")
//        public String status;
//        @SerializedName("created_at")
//        public String created_at;
//        @SerializedName("updated_at")
//        public String updated_at;
//        @SerializedName("languages")
//        public List<Languages> languages;
//    }

}
