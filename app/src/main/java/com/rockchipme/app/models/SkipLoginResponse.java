package com.rockchipme.app.models;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Alisons on 4/25/2018.
 */

public class SkipLoginResponse {
    @SerializedName("response_text") public ResponseText responseText;
    @SerializedName("response_code") public String responseCode = "";

    public class ResponseText {
        @SerializedName("success") public int success;
        @SerializedName("status") public String status;
        @SerializedName("message") public Message message;
        @SerializedName("api_key") public String api_key;
        @SerializedName("profile_pic") public String profile_pic;

    }
}
