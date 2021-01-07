package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alisons on 4/19/2018.
 */

public class CouponResponse {
    @SerializedName("response_text") public ResponseText responseText;
    @SerializedName("response_code") public String responseCode = "";

    public class ResponseText {
        @SerializedName("coupons") public List<Coupons> coupons;
        @SerializedName("status") public String status;
    }

}
