package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alisons on 4/10/2018.
 */

public class ProductDetailsResponse {
    @SerializedName("response_text") public ResponseText responseText;
    @SerializedName("response_code") public String responseCode = "";

    public class ResponseText {
        @SerializedName("product")public Products products;
        @SerializedName("recentlyViewd")public List<Products> recentlyViewedList;

        @SerializedName("cart")public List<Products> cartList;
        @SerializedName("subTotal") public String subTotal;
        @SerializedName("deliveryCharge") public String deliveryCharge;
        @SerializedName("grandTotal") public String grandTotal;

        @SerializedName("status") public String status;
    }
}
