package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alisons on 4/19/2018.
 */

public class CategoriesResponse {
    @SerializedName("response_text") public ResponseText responseText;
    @SerializedName("response_code") public String responseCode = "";

    public class ResponseText {
        @SerializedName("categories")public List<Categories> categoriesList;

        @SerializedName("cart")public List<Products> cartList;
        @SerializedName("subTotal") public String subTotal;
        @SerializedName("deliveryCharge") public String deliveryCharge;
        @SerializedName("grandTotal") public String grandTotal;

        @SerializedName("status") public String status;
    }

}
