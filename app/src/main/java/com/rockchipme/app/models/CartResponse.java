package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alisons on 4/20/2018.
 */

public class CartResponse {
    @SerializedName("response_text")
    public ResponseText responseText;

    public class ResponseText {
        @SerializedName("cart") public List<Products> cartList;
        @SerializedName("subTotal") public String subTotal;
        @SerializedName("deliveryCharge") public String deliveryCharge;
        @SerializedName("grandTotal") public String grandTotal;

        @SerializedName("message") public String message;
        @SerializedName("success") public int success;

    }
}
