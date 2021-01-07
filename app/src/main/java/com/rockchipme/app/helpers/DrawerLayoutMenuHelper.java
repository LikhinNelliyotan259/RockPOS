package com.rockchipme.app.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.AboutUsActivity;
import com.rockchipme.app.activities.FavouritesListActivity;
import com.rockchipme.app.activities.LoginActivity;
import com.rockchipme.app.activities.NotificationListActivity;
import com.rockchipme.app.activities.OrderHistoryActivty;
import com.rockchipme.app.activities.OutletsActivity;
import com.rockchipme.app.activities.SettingsActivity;
import com.rockchipme.app.activities.SignUpActivity;

/**
 * Created by Alisons on 10/3/2017.
 */
public class DrawerLayoutMenuHelper {

    private Activity activity;

    private SharedPreferences pref_login;

    //    private SquareLayout slNotifications, sl_orderHistory, slOutlet, sl_favourite, sl_settings, slAboutUs;
    private View viewNotifications, viewOrderHistory, viewOutlet, viewFavourite, viewSettings, viewAboutUs;

    public DrawerLayoutMenuHelper(Activity activity) {
        this.activity = activity;
    }

    public void setDrawerLayoutMenu(){
        initialize();
        clickEvents();
    }


    private void initialize() {


        pref_login = activity.getSharedPreferences(Constants.PREF_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor login_editor = pref_login.edit();

//        pref_cart = activity.getSharedPreferences(Constants.PREF_CART,activity.MODE_PRIVATE);
//        cart_editor = pref_cart.edit();


        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);

        TextView tv_email = activity.findViewById(R.id.tv_user_email);
        TextView tv_userName = activity.findViewById(R.id.tv_userName);

        viewOutlet = activity.findViewById(R.id.viewOutlet);
        viewFavourite = activity.findViewById(R.id.viewFavourite);
        viewSettings = activity.findViewById(R.id.viewSettings);
        viewOrderHistory = activity.findViewById(R.id.viewOrderHistory);
        viewNotifications = activity.findViewById(R.id.viewNotifications);
        viewAboutUs = activity.findViewById(R.id.viewAboutUs);

//        ImageView iv_userPic = (ImageView) activity.findViewById(R.id.iv_profilePic);


        if (pref_login.getBoolean(Constants.isSkipUser,false)){
            tv_userName.setText("REGISTER");
            tv_userName.setVisibility(View.GONE);
            tv_userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, SignUpActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
//                    activity.finish();
                }
            });

            tv_email.setText("LOGIN");
            tv_email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("from","activty");
                    activity.startActivity(intent);
//                    activity.finish();
                }
            });
        }else {

            tv_userName.setText(pref_login.getString(Constants.username,""));
            tv_email.setText(pref_login.getString(Constants.email,""));

            tv_userName.setVisibility(View.VISIBLE);

            tv_userName.setClickable(false);
            tv_email.setClickable(false);
        }
    }


    private void clickEvents() {
        viewNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, NotificationListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });


        viewOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pref_login.getBoolean(Constants.isSkipUser,false)){
                    Intent intent = new Intent(activity, OrderHistoryActivty.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
                else {
                    Toast.makeText(activity, "please login", Toast.LENGTH_SHORT).show();
                }

            }
        });

        viewOutlet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, OutletsActivity.class);
                intent.putExtra("isFromDrawerMenu", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);

            }
        });

        viewFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, FavouritesListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });

        viewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });

        viewAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AboutUsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        });
    }
}
