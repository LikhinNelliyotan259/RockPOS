package com.rockchipme.app.helpers;


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by Android on 02/21/2017.
 */
public class Constants {
    public static final String APP_TAG = "HOMEDEL_TAG";
    public static String HASH_KEY = "";
//    public static String REST_KEY = "B5BDF7D8D58EF";
    public static String REST_KEY = "123R";
//    public final static String REST_KEY_MAIN = "Q0hTVE5VVDg5ODk4";
//    public final static String REST_KEY_MAIN = "123CCC";
    public final static String REST_KEY_MAIN = "123RRR";
    public static final String OS = "ANDROID";
    public static boolean isShowUpdatePopUp = true;

    public static final int PLACE_PICKER_REQUEST = 34;

    public static final int phoneNo_length = 10;

    public static final String place_api = "AIzaSyDJP8HKqDJhHl4WnKvBeKD4c1qF7aI9FzQ";

    public static final int RC_SIGN_IN = 55;

    public static final String accounType_Emil = "email";
    public static final String accounType_Gplus = "gplus";
    public static final String accounType_FB = "fb";

    //server
//    private static final String baseUrl = "http://alisonsinfomatics.com/rest";
    private static final String baseUrl = "https://rockchipme.com/clients/homedelivery/index.php/en/api";
    public static final String SERVER_URL = baseUrl+"/index.php/en/api/";
    public static final String categoriesImageBaseUrl = baseUrl+"/uploads/category/";
    public static final String productsImageBaseUrl = baseUrl+"/uploads/products/";
    public static final String imageBaseUrl = baseUrl+"/uploads/images/";
    public static final String sliderImageUrl = baseUrl+"/uploads/slider/";

    public static final String user_signin_api = "user_signin";
    public static final String signup_api="signup";
    public static final String forgotPassword_api="forgotPassword";
    public static final String resetForgotPassword_api="resetForgotPassword";
    public static final String changePassword_api="changePassword";
    public static final String getCategories_api="getCategories";
    public static final String getProducts_api="getProducts";
    public static final String getCart_api="getCart";
    public static final String addToCart_api="addToCart";
    public static final String updateCart_api="updateCart";
    public static final String deleteFromCart_api="deleteFromCart";
    public static final String addAddress_api="addAddress";
    public static final String getAddress_api="getAddress";
    public static final String deleteAddress_api="deleteAddress";
    public static final String updateAddress_api="updateAddress";
    public static final String getCoupons_api="getCoupons";
    public static final String confirmOrder_api="confirmOrder";
    public static final String getOrders_api="getOrders";
    public static final String skipRegistration_api="skipRegistration";
    public static final String getProductDetails_api="getProductDetails";
    public static final String getHomeData_api="home";
    public static final String search_api="search";
    public static final String getOrderDetails_api="getOrderDetails";
    public static final String addToFavourites_api="addToFavourites";
    public static final String getFavourites_api="getFavourites";
    public static final String getNotifications_api="getNotifications";
    public static final String aboutUs_api="aboutUs";
    public static final String termsAndConditions_api="termsAndConditions";
    public static final String editOrder_api="editOrder";
    public static final String cancelOrder_api="cancelOrder";
    public static final String getAppVersionInfo_api="getAppVersionInfo";
    public static final String getOutlets_api="getOutlets";
//    public static final String _api="";


    //activity Results

    public static final int SIGNUP_ACTIVITY_RESULT_CODE = 101;


    //SharedPreference---LOGIN---

    public static final String PREF_LOGIN ="LOGIN_DETAILS";
    //keys
    public static final String isSkipUser ="isSkipUser";
    public static final String isLogin ="isLogin";
    public static final String api_key ="api_key";
    public static final String username ="username";
    public static final String email ="email";
    public static final String accounType ="accounType";
    public static final String status ="status";
    public static final String phone ="phone";
    public static final String photo ="photo";
    public static final String device_id ="device_id";
    public static final String logo ="logo";
    //address
    public static final String isAddressAdded ="isAddressAdded";
    public static final String addressId ="addressId";
    public static final String addressType ="addressType";
    public static final String fullName ="fullName";
    public static final String mobileNumber ="mobileNumber";
    public static final String pinCode ="pinCode";
    public static final String houseNo ="houseNo";
    public static final String street ="street";
    public static final String landmark ="landmark";
    public static final String town ="town";
    public static final String state ="state";
    public static final String longitude ="longitude";
    public static final String latitude ="latitude";

    //SharedPreference---CART---
    public static final String PREF_CART ="CART_DETAILS";
    //keys
    public static final String subTotal ="SUB_TOTAL";
    public static final String grandTotal ="GRAND_TOTAL";
    public static final String cartCount ="CART_COUNT";
    public static final String isCouponAdded ="isCouponAdded";
    public static final String couponCode ="couponCode";
    public static final String couponType ="couponType";
    public static final String coupon_perc ="coupon_perc";
    public static final String couponRate ="couponRate";
    public static final String couponId ="couponId";
    public static final String couponMinimum = "couponMinimum";
    public static final String currency = "currency";


    //SharedPreference---RESPONSE---
    public static final String PREF_RESPONSE ="RESPONSE";
    //keys
    public static final String homeResponse ="homeResponse";


    //SharedPreference---Activties---
//    public static final String PREF_REFRESH_ACTIVITIES ="REFRESH_ACTIVITIES";
//    //keys
//    public static final String selectAddressActivity ="AddressListActivity";
//    public static final String  ="";
//    public static final String  ="";
//    public static final String  ="";
//    public static final String  ="";
//    public static final String  ="";
//    public static final String  ="";


    //Request code for startActivityForResult
    public static final int RC_ADDRESS_SAVED = 101;
    public static final int RC_PRODUCT_LIST = 102;
    public static final int RC_CATEGORY_lIST = 103;
//    public static final int  = 104;
//    public static final int  = 105;
//    public static final int  = 106;
//    public static final int  = 107;
//    public static final int  = 108;
//    public static final int  = 109;
//    public static final int  = 110;
//    public static final int  = 111;
//    public static final int  = 112;
//    public static final int  = 113;


    private static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
    private static final DecimalFormat format = new DecimalFormat("##.##", symbols);
    public static String changeDecimalFormat(double priceD){
        return format.format(priceD);
    }

}
