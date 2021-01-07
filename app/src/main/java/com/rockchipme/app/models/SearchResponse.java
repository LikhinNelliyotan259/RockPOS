package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alisons on 4/11/2018.
 */

public class SearchResponse {
    @SerializedName("response_text") public ResponseText responseText;


    public class ResponseText {

        @SerializedName("searchReults")public List<Products> searchReultsList;

        @SerializedName("status") public String status;
    }
}
