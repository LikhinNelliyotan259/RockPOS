package com.rockchipme.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.adapters.MainCategoryAdapter;
import com.rockchipme.app.custom.EndlessRecyclerOnScrollListener;
import com.rockchipme.app.helpers.BasketHelp;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.models.CartResponse;
import com.rockchipme.app.models.Categories;
import com.rockchipme.app.models.CategoriesResponse;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MainCategoriesActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish {

    BasketHelp basketHelp;
    List<Categories> categoriesList = new ArrayList<Categories>();
    List<Products> listCart = new ArrayList<>();
    RecyclerView rvCategories;
    MainCategoryAdapter adapterCategories;

    //    HashMap hashMap;
    boolean isBack, isFromNotification = false;
    ;

    int start = 0;
    boolean loading;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main_categories;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (listCart == null)
            listCart = new ArrayList<>();
        isBack = false;
        isFromNotification = getIntent().getBooleanExtra("isFromNotification", false);
        setUpToolBar(false, "CATEGORIES", true);
        initialize();

        setBottomSheetBehavior();

        getCategory(start);
    }

    void initialize() {
        rvCategories = (RecyclerView) findViewById(R.id.rv_categories);
        setAdapter();
    }

    private void setAdapter() {
        rvCategories.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCategories.setLayoutManager(layoutManager);
        adapterCategories = new MainCategoryAdapter(this);
        rvCategories.setAdapter(adapterCategories);

        rvCategories.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int limitStart) {
//                if(productsResponse == null){
//                    Log.e(Constants.APP_TAG,"productsResponse is null");
//                    return;
//                }
//                if(productsResponse.products.next_page_url == null){
//                    Log.e(Constants.APP_TAG,"roductsResponse.products.next_page_url is null");
//                    return;
//                }
//                start = limitStart;
//                categoriesList.add(null);
//                notifyCategoryAdapter(categoriesList);
//                Log.e(Constants.APP_TAG,"api calling");
//                getCategory(start, false);
            }
        });

    }

    //define methods for request get_categories
    private final String FROM_GET_CATEGORY = "FROM_GET_CATEGORY";

    private void getCategory(int start) {
        RequestBody body = new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("limit", "1000")
                .add("start", String.valueOf(start).trim())
                .add("api_key", apiKey)
                .add("catId", "")
                .build();
        loading = true;
//        apiCallServiceTask.requestApi(ApiResponseBus.RC_CATEGORY, showProgress,body, Constants.SERVER_URL+ Constants.getCategories_api,"GET CATEGORIES...");
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_CATEGORY, Constants.getCategories_api, body, true, ApiCallRequest.WHITE));
    }//end requset

    public void getCategoryResponse(String response) {

        final CategoriesResponse categoriesResponse = new Gson().fromJson(response, CategoriesResponse.class);

        if (categoriesList == null) {
            categoriesList = new ArrayList<>();
        }

        if (categoriesList.size() > 0 && categoriesList.get(categoriesList.size() - 1) == null) {
            categoriesList.remove(categoriesList.size() - 1);
        }

        if (categoriesResponse.responseText.categoriesList != null) {
            categoriesList.addAll(categoriesResponse.responseText.categoriesList);
        }

        if (categoriesResponse.responseText.status != null) {
            Toast.makeText(MainCategoriesActivity.this, categoriesResponse.responseText.status, Toast.LENGTH_SHORT).show();
        }

        notifyCategoryAdapter(categoriesList);

        if (categoriesResponse.responseText.cartList == null) {
            Log.e(Constants.APP_TAG, "cart is empty");
            basketHelp = new BasketHelp(this);
            if (basketHelp.rv_Cart != null)
                basketHelp.rv_Cart.setVisibility(View.INVISIBLE);
            return;
        }

        listCart = categoriesResponse.responseText.cartList;
        basketHelp = new BasketHelp(this, listCart, basketHelper.getPriceObjects());
        setCartCount(false);
    }


    private void updateCartDetails(JSONObject response, String responseBus) {
        CartResponse cartResponse = new Gson().fromJson(responseBus, CartResponse.class);
        if (cartResponse == null || cartResponse.responseText == null) {
            return;
        }
        List<Products> cartList = cartResponse.responseText.cartList;
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        editorCart.putString(Constants.grandTotal, cartResponse.responseText.grandTotal);
        editorCart.putString(Constants.subTotal, cartResponse.responseText.subTotal);
        editorCart.putString(Constants.cartCount, cartList.size() + "");
        editorCart.commit();

        if (cartList.size() > 0) {
            basketHelp = new BasketHelp(this, cartList, response.optJSONObject("response_text"));
            basketHelp.rv_Cart.setVisibility(View.VISIBLE);
        } else {
            basketHelp = new BasketHelp(this);
            basketHelp.rv_Cart.setVisibility(View.INVISIBLE);
        }
        setCartCount(false);
    }


    @Override
    protected void onResume() {
        super.onResume();

        setBottomSheetText();

        if (isBack) {
//            if (!listCart.isEmpty()) {
            Log.e(Constants.APP_TAG, "refreshing");

            getCart(false);
        }
        isBack = true;

    }

    private void notifyCategoryAdapter(List<Categories> listCategories) {
        if (listCategories == null) {
            listCategories = new ArrayList<>();
        }
        if (adapterCategories != null) {
            adapterCategories.setAdapterList(listCategories);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse) {

    }

    @Override
    public void onBackPressed() {

        if (isFromNotification) {
            setResult(Activity.RESULT_CANCELED);
            Intent mIntent = new Intent(getApplicationContext(), HomeScreenActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
            ActivityCompat.finishAffinity(this);
            finish();
        }
        super.onBackPressed();
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            String response = responseBus.response;
            JSONObject object = new JSONObject(response);
            basketHelper.setPriceObjects(object.optJSONObject("response_text"));

            switch (responseBus.FROM){
                case FROM_GET_CATEGORY:
                    getCategoryResponse(response);
                    loading = false;
                    break;
                case FROM_GET_CART:
                    updateCartDetails(object, response);
                    break;
                case BasketHelp.FROM_PLACE_ORDER:
                    basketHelp.placeOrderResponse(MainCategoriesActivity.this, object);
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
