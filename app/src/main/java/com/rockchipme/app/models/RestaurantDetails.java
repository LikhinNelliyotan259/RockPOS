package com.rockchipme.app.models;

import com.rockchipme.app.helpers.Constants;
import com.google.gson.annotations.SerializedName;

public class RestaurantDetails {
    @SerializedName("rest_key") public String rest_key = "";
    @SerializedName("name") public String name = "";
    @SerializedName("logo") private String logo = "";
    @SerializedName("location") public String location = "";
    @SerializedName("latitude") public String latitude = "";
    @SerializedName("longitude") public String longitude = "";
    @SerializedName("maxMs") public String maxMs = "";
    @SerializedName("minimumOrder") public String minimumOrder = "";
    @SerializedName("phoneNumber") public String phoneNumber = "";
    @SerializedName("email") public String email = "";
    @SerializedName("currency") public String currency = "";
    @SerializedName("description") public String description = "";
    @SerializedName("workingHours") public String workingHours = "";
    @SerializedName("rest_type") public String restType;
    @SerializedName("distance") public String distance;

    public String getLogo(){
            return Constants.imageBaseUrl + logo;
        }

}
