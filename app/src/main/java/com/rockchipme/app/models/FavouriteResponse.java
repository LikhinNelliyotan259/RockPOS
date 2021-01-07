package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alisons on 4/20/2018.
 */

public class FavouriteResponse {
    @SerializedName("response_text")
    public ResponseText responseText;
    @SerializedName("response_code")
    public String responseCode = "";

    public class ResponseText {
        @SerializedName("favourites")
        public List<Products> productsList;

        @SerializedName("success")
        public int success;
        @SerializedName("status")
        public String status;
        @SerializedName("message")
        public String message;
    }
}
