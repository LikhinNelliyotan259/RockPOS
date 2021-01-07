package com.rockchipme.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.rockchipme.app.models.HomeResponse;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alisons on 4/18/2018.
 */

public class Utils  {
    Context context;
    SharedPreferences preferences;

    public Utils(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(Constants.PREF_RESPONSE, Context.MODE_PRIVATE);
    }

    public void setHomeResponse(String response){
        if (preferences!=null) {
            preferences.edit().putString(Constants.homeResponse, response).commit();
        }
    }
    public HomeResponse getHomeResponse(String response){
        if (preferences==null) {
            return null;
        }
        return new Gson().fromJson(preferences.getString(Constants.homeResponse,""), HomeResponse.class);
    }

    public static void setMargins (View view, int left, int top, int right, int bottom) {
        try {
            left = getPxFromDp(view.getContext(), left);
            top = getPxFromDp(view.getContext(), top);
            right = getPxFromDp(view.getContext(), right);
            bottom = getPxFromDp(view.getContext(), bottom);
            if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//                p.setMargins(left, top, right, bottom);
//                view.requestLayout();
                p.leftMargin = left;
                p.topMargin = top;
                p.rightMargin = right;
                p.bottomMargin = bottom;
            }
        } catch (Exception e){e.printStackTrace();}
    }


    public static int getPxFromDp(Context context, float dp){
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }

    public static String convertDateFormat(String orderDate){
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date myDate = null;
            try {
                myDate = dateFormat.parse(orderDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.ENGLISH);
            return timeFormat.format(myDate);
        }catch (Exception e){
            e.printStackTrace();
        }
//        }
        return  orderDate;
    }
}
