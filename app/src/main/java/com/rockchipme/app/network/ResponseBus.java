package com.rockchipme.app.network;

/**
 * Created by Sibin on 10/11/2017.
 */

public class ResponseBus {
    public static final int ADD_ADDRESS = 0;
    public static final int WISH_LIST = 1;
    public static final int FILTER = 2;
    public static final int FAILED = 3;
    public static final int CART = 4;
    public static final int LOGIN = 5;
    public static final int NOTIFICATION = 6;
    public static final int PAYMENT = 7;
    public static final int PROFILE = 8;
    public static final int FORGOT_PASSWORD = 9;

    public String FROM = "";
    public int UPDATE_TYPE = 0;
    public String slug = "";
    public int cart = 0;
    public int value = 0;
    public String valueString = "0";
    public String ratingCount = "0";
    public String reviewsCount = "0";
    public String otp = "";
    public String email = "";
}
