package com.rockchipme.app.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.BasketHelp;
import com.rockchipme.app.helpers.BasketHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.models.CartResponse;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisons on 10/3/2017.
 */
public class BasketActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish{
    BasketHelp basketHelp;
    BasketHelper basketHelper;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_basket;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpToolBar(false, "BASKET", false);
        basketHelper = new BasketHelper(this);
    }

    private void updateCartDetails(JSONObject response, String responseBus){
        CartResponse cartResponse = new Gson().fromJson(responseBus, CartResponse.class);

        if (cartResponse==null || cartResponse.responseText==null){
            return;
        }

        List<Products> cartList = cartResponse.responseText.cartList;
        if (cartList==null){
            cartList = new ArrayList<>();
        }
        editorCart.putString(Constants.grandTotal, cartResponse.responseText.grandTotal);
        editorCart.putString(Constants.subTotal, cartResponse.responseText.subTotal);
        editorCart.putString(Constants.cartCount, cartList.size()+"");
        editorCart.commit();

        if ( cartList.size()>0){
            basketHelp = new BasketHelp(BasketActivity.this, cartList, response.optJSONObject("response_text"));
            basketHelp.rv_Cart.setVisibility(View.VISIBLE);
        } else {
            basketHelp = new BasketHelp(this);
            basketHelp.rv_Cart.setVisibility(View.INVISIBLE);
        }

//        basketHelp.updateCartPopup();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCart(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse){

    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            String response = responseBus.response;
            JSONObject object = new JSONObject(response);
            String resp_code = object.optString("response_code");

            switch (responseBus.FROM) {
                case FROM_GET_CART:
                case BasketHelper.FROM_UPDATE_CART:
                    updateCartDetails(object, response);
                    break;
                case BasketHelp.FROM_PLACE_ORDER:
                    basketHelp.placeOrderResponse(this, object);
                    break;
                case BasketHelp.FROM_GET_VOUCHER:
                    basketHelper.getCoupon(response, basketHelp);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
