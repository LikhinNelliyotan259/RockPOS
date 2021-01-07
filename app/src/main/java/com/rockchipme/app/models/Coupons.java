package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alisons on 9/15/2017.
 */
public class Coupons {

    @SerializedName("couponId") public String couponId = "";
    @SerializedName("couponCode") public String couponCode = "";
    @SerializedName("discountType") public String discountType = "";
    @SerializedName("discountPerc") public String discountPerc = "";
    @SerializedName("discountRate") public String discountRate = "";
    @SerializedName("minimumTotal") public String minimumTotal = "";
//    @SerializedName("") public String  = "";


}
