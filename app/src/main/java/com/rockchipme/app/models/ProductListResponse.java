package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alisons on 4/20/2018.
 */

public class ProductListResponse {
    @SerializedName("response_text")
    public ResponseText responseText;
    @SerializedName("response_code")
    public String responseCode = "";

    public class ResponseText {
        @SerializedName("categories")
        public List<Categories> categories;
        @SerializedName("products")
        public List<Products> productsList;
        @SerializedName("cart")
        public List<Products> cartList;
        @SerializedName("subTotal")
        public String subTotal;
        @SerializedName("deliveryCharge")
        public String deliveryCharge;
        @SerializedName("grandTotal")
        public String grandTotal;

        @SerializedName("category")
        public Categories category;

        @SerializedName("message") public String message = "0";
        @SerializedName("success") public String success;

    }
}
