package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisons on 8/3/2017.
 */
public class UserDetails {
    @SerializedName("success") public String success = "";
    @SerializedName("api_key") public String apiKey = "";
    @SerializedName("username") public String username = "";
    @SerializedName("email") public String email = "";
    @SerializedName("accountType") public String accountType = "";
//    @SerializedName("address") public String address = "";
    @SerializedName("phone") public String phone = "";
    @SerializedName("photo") public String photo = "";
    @SerializedName("status") public String status = "";
    @SerializedName("address") public List<AddressResponse.Address> address = new ArrayList<AddressResponse.Address>();

}
