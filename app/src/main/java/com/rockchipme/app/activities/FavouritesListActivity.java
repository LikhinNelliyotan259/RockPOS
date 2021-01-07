package com.rockchipme.app.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.rockchipme.app.R;
import com.rockchipme.app.adapters.FavouritesListAdapter;
import com.rockchipme.app.helpers.BasketHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.models.CartResponse;
import com.rockchipme.app.models.FavouriteResponse;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 4/20/2018.
 */

public class FavouritesListActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish{

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_foavourites_list;
    }

    RecyclerView rvFavouriteList;
    FavouritesListAdapter favouritesListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolBar(false, getResources().getString(R.string.favourites), false);
        getFavouriteApi();

        rvFavouriteList = findViewById(R.id.rvFavouriteList);
        rvFavouriteList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvFavouriteList.setLayoutManager(layoutManager);
        favouritesListAdapter = new FavouritesListAdapter(this, currency);
        rvFavouriteList.setAdapter(favouritesListAdapter);

    }


    private final String FROM_GET_FAVOURITE = "FROM_GET_FAVOURITE";
    private void getFavouriteApi() {
        RequestBody body=new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apiKey)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_FAVOURITE, Constants.getFavourites_api, body, false,"Loading..."));
    }

    private final String FROM_REMOVE_FROM_FAVOURITE = "FROM_REMOVE_FROM_FAVOURITE";
    public void removeFavouriteApi(String productId) {
        RequestBody body=new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apiKey)
                .add("pdtId", productId)
                .add("add", "0")
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_REMOVE_FROM_FAVOURITE, Constants.addToFavourites_api, body, false,"Loading..."));
    }

    private void moveToCartResponse(String response) {
        try{
            CartResponse cartResponse = new Gson().fromJson(response, CartResponse.class);
            List<Products> cartList = new ArrayList<>();
            if (cartResponse!=null && cartResponse.responseText!=null && cartResponse.responseText.cartList!=null){
                cartList = cartResponse.responseText.cartList;
                try {
                    editorCart.putString(Constants.grandTotal, cartResponse.responseText.grandTotal);
                    editorCart.putString(Constants.subTotal, cartResponse.responseText.subTotal);
                    editorCart.commit();
                } catch (Exception e){e.printStackTrace();}
            }

            editorCart.putString(Constants.cartCount, cartList.size()+"").commit();
            setCartCount(true);
            checkEmptyScreen();
        } catch (Exception e){e.printStackTrace();}
    }

    private void getFavouriteApiResponse(String response) {
        try {
            FavouriteResponse favouriteResponse = new Gson().fromJson(response, FavouriteResponse.class);
            List<Products> favouriteList = favouriteResponse.responseText.productsList;
            notifyAdapter(favouriteList);
        } catch (Exception e){e.printStackTrace();}
    }

    private void notifyAdapter(List<Products> productsList) {
        if (productsList == null){
            productsList = new ArrayList<>();
        }
        if (favouritesListAdapter!=null){
            favouritesListAdapter.setAdapterData(productsList);
        }
        checkEmptyScreen();
    }

    private void checkEmptyScreen() {
        if (favouritesListAdapter!=null){
            if (favouritesListAdapter.getFavouriteList().size()>0){
                if (rvFavouriteList!=null)
                    rvFavouriteList.setVisibility(View.VISIBLE);
                setEmptyScreen(false);
            } else {
                setEmptyScreen(true);
            }
        } else {
            setEmptyScreen(true);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setCartCount(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse){

    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        switch (responseBus.FROM){
            case FROM_REMOVE_FROM_FAVOURITE:
                checkEmptyScreen();
                break;
            case FROM_GET_FAVOURITE:
                getFavouriteApiResponse(responseBus.response);
                break;
            case BasketHelper.FROM_UPDATE_CART:
                moveToCartResponse(responseBus.response);
                break;
        }
    }
}
