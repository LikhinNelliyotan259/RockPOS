package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class Notification implements Serializable {
    @SerializedName("sentDate") public String sentDate;
    @SerializedName("image") public String image;
    @SerializedName("value") public String value;
    @SerializedName("title") public String title;
    @SerializedName("message") public String message;
    @SerializedName("type") public String type;
    @SerializedName("notificationId") public String notificationId;
    @SerializedName("data") public List<Categories> notificationdate;
}
