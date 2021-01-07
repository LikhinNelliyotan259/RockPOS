package com.rockchipme.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisons on 9/27/2017.
 */
public class Orders {
    @SerializedName("orderId") public String orderId = "";
    @SerializedName("deliveryCharge") public String deliveryCharge = "";
    @SerializedName("couponCode") public String couponCode = "";
    @SerializedName("total") public String total = "";
    @SerializedName("couponDiscountAmount") public String couponDiscountAmount = "";
    @SerializedName("grandTotal") public String grandTotal = "";
    @SerializedName("deliveyAddressId") public String deliveyAddressId = "";
    @SerializedName("orderedDate") public String orderedDate = "";
    @SerializedName("deliveryDate") public String deliveryDate = "";
    @SerializedName("orderStatus") public String orderStatus = "";
    @SerializedName("products") public List<Products> products= new ArrayList<Products>();
    @SerializedName("deliveyAddress") public AddressResponse.Address deliveyAddress;
    @SerializedName("minimumTotal") public String minimumTotal = "";

    @SerializedName("coupon")public List<Coupons> couponsList;

//        "coupon":[{"couponCode":"NEW20","minimumTotal":"0.00"}]}}

}
