package com.rockchipme.app.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rockchipme.app.R;
import com.rockchipme.app.models.CouponResponse;
import com.rockchipme.app.models.Coupons;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.models.Unit;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallServiceTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;

/**
 * Created by Android on 2/16/2017.
 */
public class BasketHelper {

    private Context context;
    public BottomSheetBehavior bottomSheetBehavior;
    private TextView tvToolBar = null;
    private String toolBarText = "";
    private FrameLayout flSearchAction, flCartAction = null;

    private JSONObject priceObjects = new JSONObject();


    public BasketHelper(Context _activity) {
        this.context = _activity;
    }


    public BasketHelper(Context _activity, TextView tvToolBar, String toolBarText, FrameLayout flSearchAction, FrameLayout flCartAction) {
        this.context = _activity;
        this.tvToolBar = tvToolBar;
        this.toolBarText = toolBarText;
        this.flSearchAction = flSearchAction;
        this.flCartAction = flCartAction;
    }

    public BottomSheetBehavior setUPBasket() {
//        okHttpCommon = new ApiCallServiceTask(_activity);
        final View bottom = ((Activity) context).findViewById(R.id.design_bottom_sheet);

//        initialize(bottom);
//
//        final LinearLayoutManager layoutManager = new LinearLayoutManager(_activity, LinearLayoutManager.VERTICAL, false);
//        // use a linear layout manager
//        rv_Cart.setLayoutManager(layoutManager);
//
//        getBasketItems();

        LinearLayout ll_basket_peek = ((Activity) context).findViewById(R.id.ll_basket_peek);
        setFitsSystemWindows(bottom, false);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        setFitsSystemWindows(bottom, false);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        setFitsSystemWindows(bottom, false);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        setFitsSystemWindows(bottom, true);
                        bottom.setOnClickListener(v -> {
                            //nothing
                        });

                        setToolbarActionVisibility(View.INVISIBLE, "Basket");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
//                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                        }
                        setFitsSystemWindows(bottom, false);
                        setToolbarActionVisibility(View.VISIBLE, toolBarText);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            //bottomSheetBehavior.setPeekHeight((int)getResources().getDimension(R.dimen.peek_height));
                        }
                        setFitsSystemWindows(bottom, false);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
                if (slideOffset == 0) {
                    setFitsSystemWindows(bottom, false);
                }
            }
        });

        ll_basket_peek.setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
//                else {
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
        });

        return bottomSheetBehavior;
    }

    private void setToolbarActionVisibility(int visibility, String title) {

        if (flSearchAction != null) {
            flSearchAction.setVisibility(visibility);
        }

        if (flCartAction != null) {
            flCartAction.setVisibility(visibility);
        }

        if (tvToolBar != null) {
            tvToolBar.setText(title);
        }
    }

    private void setFitsSystemWindows(View bottomSheet, boolean isFit) {
        bottomSheet.setFitsSystemWindows(isFit);
        bottomSheet.requestFitSystemWindows();
        bottomSheet.setPadding(0, 0, 0, 0);
    }

    public final static String FROM_UPDATE_CART = "FROM_UPDATE_CART";

    public void updateCart(int currentValue, Products product, String from) {
        if (product == null) {
            return;
        }
        Log.e(Constants.APP_TAG, "cart count: " + currentValue);
        ApiCallServiceTask okHttpCommon = new ApiCallServiceTask(context);
        SharedPreferences pref = context.getSharedPreferences(Constants.PREF_LOGIN, Context.MODE_PRIVATE);

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("rest_key", Constants.REST_KEY);
//                .add("pdtId",list.get(position1).getPdtId())
        builder.add("pdtId", product.pdtId);
        builder.add("quantity", String.valueOf(currentValue));
        builder.add("api_key", pref.getString(Constants.api_key, "") + "");
        if (from != null && from.equalsIgnoreCase("FavouritesListAdapter")) {
            builder.add("from", "favourites");
        }

        if (from != null && from.equalsIgnoreCase("BasketListAdapter") && product.cartId != null) {
            builder.add("cart_id", product.cartId);
        }

        String selectedUnit = null;
        if (product.units != null) {
            for (Unit units : product.units) {
//                if (units.primaryUnit.trim().equals("1") && product.cartCount.equals("0")) {
//                    selectedUnit = units.unitId;
//                }
                if (units.primaryUnit.trim().equals("1") && product.cartCount.equals("0")) {
                    selectedUnit = units.unitId;
                    break;
                }
            }
        }

        if (product.selectedForce != null && product.selectedForce.size() > 0) {
            builder.add("force", product.selectedForce.get(0).pdtId);
        }

        if (product.unit_id != null && product.unit_id.trim().length() > 0) {
            selectedUnit = product.unit_id;
        }

        if (product.selectedModifiers != null && product.selectedModifiers.size() > 0) {
            String comma = "";
            StringBuilder modifiers = new StringBuilder();
            for (Products i : product.selectedModifiers) {
                modifiers.append(comma).append(i.pdtId);
                comma = ",";
            }
            builder.add("modifiers", modifiers.toString());
        }

        if (selectedUnit != null && selectedUnit.trim().length() > 0) {
            builder.add("unit", selectedUnit);
        }

        if (product.remarks == null) {
            product.remarks = "";
        }
        builder.add("remarks", product.remarks.trim());

        okHttpCommon.requestApi(new ApiCallRequest(FROM_UPDATE_CART, Constants.addToCart_api, builder.build(), true, ApiCallRequest.TRANSPARENT));
    }

    public void getCoupon(String response, BasketHelp basketHelp) {
        try {
            CouponResponse couponResponse = new Gson().fromJson(response, CouponResponse.class);
            List<Coupons> couponsList = couponResponse.responseText.coupons;
            if (couponsList != null && couponsList.size() > 0) {
                basketHelp.showCouponPopup(couponsList);
                return;
            }
            Toast.makeText(context, couponResponse.responseText.status, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setPriceObjects(JSONObject jsonObject) {
        try {
            priceObjects = new JSONObject();
            priceObjects.put("subTotal", jsonObject.optString("subTotal"));
            priceObjects.put("deliveryCharge", jsonObject.optString("deliveryCharge"));
            priceObjects.put("grandTotal", jsonObject.optString("grandTotal"));
//            priceObjects.put("tax",jsonObject.optString("tax"));
//            priceObjects.put("subTotal",jsonObject.optString(""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    public JSONObject setPriceObjects(String subTotal, String deliveryCharge, String grandTotal) {
//        try {
//            priceObjects = new JSONObject();
//            priceObjects.put("subTotal", subTotal);
//            priceObjects.put("deliveryCharge", deliveryCharge);
//            priceObjects.put("grandTotal", grandTotal);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return priceObjects;
//
//    }

    public JSONObject getPriceObjects() {
        return priceObjects;
    }

}
