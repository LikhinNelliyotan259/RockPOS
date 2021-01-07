package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OutletsResponse {
    @SerializedName("response_text") public ResponseText responseText;
    @SerializedName("response_code") public String responseCode = "";

    public class ResponseText {
        @SerializedName("success") public int success;
        @SerializedName("status") public String status;
        @SerializedName("message") public String message;
        @SerializedName("data") public List<RestaurantDetails> outlets;
    }
}
