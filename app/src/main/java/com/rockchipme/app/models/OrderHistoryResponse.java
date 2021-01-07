package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alisons on 4/18/2018.
 */

public class OrderHistoryResponse {
    @SerializedName("response_text") public ResponseText responseText;
    @SerializedName("response_code") public String responseCode = "";
    public class ResponseText {
        @SerializedName("success")public int success;
        @SerializedName("message")public String message;
        @SerializedName("orders")public List<Orders> ordersList;
    }


}
