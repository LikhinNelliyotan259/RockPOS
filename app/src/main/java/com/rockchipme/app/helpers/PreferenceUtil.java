package com.rockchipme.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.rockchipme.app.models.RestaurantDetails;

public class PreferenceUtil {
    private final Context context;
    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public PreferenceUtil(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("PREF_OUTLET", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setOutletStoreDetails(RestaurantDetails restaurantDetails){
        editor.putString("outletKey", restaurantDetails.rest_key);
        editor.putString("outletLocation", restaurantDetails.location);
        editor.putString("outletName", restaurantDetails.name);


        editor.commit();
    }

    public RestaurantDetails getOutletStoreDetails(){
        RestaurantDetails restaurantDetails = new RestaurantDetails();
        restaurantDetails.rest_key = preferences.getString("outletKey", "");
        restaurantDetails.location = preferences.getString("outletLocation", "");
        restaurantDetails.name = preferences.getString("outletName", "");

        return restaurantDetails;
    }

}
