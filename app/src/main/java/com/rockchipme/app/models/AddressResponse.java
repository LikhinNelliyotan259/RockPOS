package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alisons on 9/22/2017.
 */
public class AddressResponse {

//    @SerializedName("") public String  = "";
    @SerializedName("response_text") public ResponseText responseText;

    public class ResponseText {
        @SerializedName("address")public List<Address> addressList;
        @SerializedName("status") public String status;
        @SerializedName("success") public int success;
    }

    public class Address{
        @SerializedName("addressId") public String addressId = "";
        @SerializedName("addressType") public String addressType = "";
        @SerializedName("fullName") public String fullName = "";
        @SerializedName("mobileNumber") public String mobileNumber = "";
        @SerializedName("pinCode") public String pinCode = "";
        @SerializedName("houseNo") public String houseNo = "";
        @SerializedName("street") public String street = "";
        @SerializedName("landmark") public String landmark = "";
        @SerializedName("town") public String town = "";
        @SerializedName("state") public String state = "";
        @SerializedName("latitude") public String latitude = "";
        @SerializedName("longitude") public String longitude = "";
    }


}
