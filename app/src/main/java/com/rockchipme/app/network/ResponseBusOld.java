package com.rockchipme.app.network;


/**
 * Created by Sibin on 10/11/2017.
 */

public class ResponseBusOld {
    public static final int CART_COUNT = 0;
    public static final int WISH_LIST = 1;
    public static final int RATING = 2;
    public static final int FAILED = 3;
    public static final int CART_REMOVED = 4;
    public static final int CART_AND_WISH_LIST = 5;
    public static final int NOTIFICATION = 6;
    public static final int PAYMENT = 7;
    public static final int PROFILE = 8;
    public static final int FORGOT_PASSWORD = 9;
    public static final int COUPON_SELECTED = 10;
    public static final int ADDRESS_SHIPPING = 11;
    public static final int ADDRESS_BILLING = 12;
    public static final int ORDER_CANCELLATION = 13;
    public static final int ADDRESS_ADDED = 14;

    public String FROM = "";
    public int UPDATE_TYPE = 0;
    public String slug = "";
    public int cart = 0;
    public int wishlist = 0;
    public String value = "0";
    public String ratingCount = "0";
    public String reviewsCount = "0";
    public String otp = "";
    public String email = "";
}
