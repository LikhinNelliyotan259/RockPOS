package com.rockchipme.app.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.BasketHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.SignupAndSigninApiCall;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallServiceTask;

import org.greenrobot.eventbus.EventBus;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Jafseel on 4/4/2018.
 */

public abstract class BaseActivity extends AppCompatActivity {
    SharedPreferences prefLogin, prefCart;
    String currency = "";
    String apiKey = "", toolbarText = "";
    SharedPreferences.Editor editorLogin, editorCart;
    Constants constants;
    TextView tvToolbar, tvCartCount;
    FrameLayout flSearchAction, flCartAction, flBackAction;

    ApiCallServiceTask apiCallServiceTask;
    BottomSheetBehavior bottomSheetBehavior;
    BasketHelper basketHelper;
//    boolean isSkippedUser,isLogin, isNetworkAvailable;

    private TextView tvBottomsheetItemCount, tvBottomsheetPrice;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        constants = new Constants();
        prefCart = getSharedPreferences(constants.PREF_CART, MODE_PRIVATE);
        editorCart = prefCart.edit();
        prefLogin = getSharedPreferences(constants.PREF_LOGIN, MODE_PRIVATE);
        editorLogin = prefLogin.edit();
        apiCallServiceTask = new ApiCallServiceTask(this);


        tvBottomsheetItemCount = (TextView) findViewById(R.id.bottomsheet_text_itemCount);
        tvBottomsheetPrice = (TextView) findViewById(R.id.bottomsheet_text_price);


        apiKey = prefLogin.getString(Constants.api_key, "");
        currency = prefCart.getString(Constants.currency, "");

        if (apiKey == null || apiKey.trim().length() < 1) {
            new SignupAndSigninApiCall().logout(this, prefLogin, editorCart);
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        try {
            if (findViewById(R.id.btnShopNow) != null) {
                findViewById(R.id.btnShopNow).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        } else {
                            openHome();
                        }
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void openHome() {
        Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
        finish();
    }

    void setUpToolBar(final boolean isHomeScreen, String toolbarText, boolean showSearcMenu) {
        this.toolbarText = toolbarText;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        flBackAction = toolbar.findViewById(R.id.flBackAction);
        flSearchAction = toolbar.findViewById(R.id.flSearchAction);
        flCartAction = toolbar.findViewById(R.id.flCartAction);
        tvToolbar = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tvCartCount = toolbar.findViewById(R.id.cart_badge);

        try {
            if (isHomeScreen) {
                ((ImageView) findViewById(R.id.ivBackAction)).setImageResource(R.mipmap.ic_menu_icon);
            } else {
                ((ImageView) findViewById(R.id.ivBackAction)).setImageResource(R.drawable.back_btn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        flBackAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHomeScreen) {
                    ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
                } else {
                    onBackPressed();
                }
            }
        });

        flSearchAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            }
        });

        if (flCartAction != null) {
            flCartAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (basketHelper != null && basketHelper.bottomSheetBehavior != null) {
                        basketHelper.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    } else {
                        startActivity(new Intent(getApplicationContext(), BasketActivity.class));
                    }
                }
            });
        }
        if (tvToolbar != null) {
            tvToolbar.setText(toolbarText);
        }

        if (!showSearcMenu) {
            if (flSearchAction != null) {
                flSearchAction.setVisibility(View.INVISIBLE);
            }
        }
    }

    protected void hideSearchMenu() {
        if (flSearchAction != null) {
            flSearchAction.setVisibility(View.GONE);
        }
    }

    protected void hideBackButton() {
        if (flBackAction != null) {
            flBackAction.setVisibility(View.GONE);
        }
    }

    protected void chanfgetToolbarText(String toolbarText) {
        this.toolbarText = toolbarText;
        if (tvToolbar != null) {
            tvToolbar.setText(toolbarText);
        }
        setBottomSheetBehavior();
    }


    protected final String FROM_GET_CART = "FROM_GET_CART";

    protected void getCart(boolean isFromBasketActivty) {
        RequestBody body = new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apiKey)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_CART, Constants.getCart_api, body, true, ApiCallRequest.TRANSPARENT));
    }

    protected void setBottomSheetBehavior() {
        if (basketHelper ==  null) {
            basketHelper = new BasketHelper(this, tvToolbar, toolbarText, flSearchAction, flCartAction);
        }
        bottomSheetBehavior = basketHelper.setUPBasket();
    }

    protected void setBottomSheetText() {
        if (tvBottomsheetItemCount != null)
            tvBottomsheetItemCount.setText(prefCart.getString(Constants.cartCount, "0"));
        if (tvBottomsheetPrice != null)
            tvBottomsheetPrice.setText(currency + " " + prefCart.getString(Constants.grandTotal, "0.00"));
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        apiKey = prefLogin.getString(Constants.api_key, "");
        currency = prefCart.getString(Constants.currency, "");
        if (apiKey == null || apiKey.trim().length() < 1) {
            new SignupAndSigninApiCall().logout(this, prefLogin, editorCart);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected void setEmptyScreen(boolean isShow) {
        int visibility = isShow ? View.VISIBLE : View.GONE;
        LinearLayout lnEmptyScreen = findViewById(R.id.layoutEmptyScreen);
        Log.e(Constants.APP_TAG, "setEmptyScreen():" + isShow + " - " + String.valueOf(lnEmptyScreen != null));
        if (lnEmptyScreen != null)
            lnEmptyScreen.setVisibility(visibility);

    }

    protected void setCartCount(boolean isFromFavorites) {
        try {
            if (flCartAction != null) {
                if ((bottomSheetBehavior == null || bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)) {
                    flCartAction.setVisibility(View.VISIBLE);
                } else {
                    flCartAction.setVisibility(View.GONE);
                }
            }
            if (flSearchAction != null && isFromFavorites) {
                flSearchAction.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (tvCartCount != null && prefCart != null) {
            tvCartCount.setText(prefCart.getString(Constants.cartCount, "0"));
            if (prefCart.getString(Constants.cartCount, "0").equals("0")) {
                tvCartCount.setVisibility(View.INVISIBLE);
            } else {
                tvCartCount.setVisibility(View.VISIBLE);
            }
        }
    }

    protected abstract int getLayoutResourceId();
}

