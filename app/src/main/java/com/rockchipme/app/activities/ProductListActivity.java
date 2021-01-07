package com.rockchipme.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rockchipme.app.R;
import com.rockchipme.app.adapters.ItemsListAdapter;
import com.rockchipme.app.adapters.SubCategoriesAdapter;
import com.rockchipme.app.helpers.BasketHelp;
import com.rockchipme.app.helpers.BasketHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.helpers.Utils;
import com.rockchipme.app.models.CartResponse;
import com.rockchipme.app.models.Categories;
import com.rockchipme.app.models.ProductListResponse;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ProductListActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_product_list;
    }

    private int start = 0;
    private BasketHelp basketHelp;
    private String catId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpToolBar(false, "Product List", true);
        initViews();
        Intent intent = getIntent();
        if (intent != null && intent.getStringExtra("catId") != null) {
            catId = intent.getStringExtra("catId");
        }
        getCategories(start);
    }

    private ImageView ivCategory;
    private TextView tvItemCount, tvCategoryName;
    private SubCategoriesAdapter subCategoriesAdapter;
    private ItemsListAdapter productListAdapter;
    private Group groupSubCategory;

    private void initViews() {
        setBottomSheetBehavior();
        ivCategory = findViewById(R.id.ivCategory);
        tvItemCount = findViewById(R.id.tvItemCount);
        tvCategoryName = findViewById(R.id.tvCategoryName);

        groupSubCategory = findViewById(R.id.groupSubCategory);

        RecyclerView rvSubCategories = findViewById(R.id.rvSubCategories);
        subCategoriesAdapter = new SubCategoriesAdapter(this);
        rvSubCategories.setAdapter(subCategoriesAdapter);

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        productListAdapter = new ItemsListAdapter(this, basketHelper, currency);
        rvProducts.setAdapter(productListAdapter);

//        rvProducts.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) rvItems.getLayoutManager()) {
//            @Override
//            public void onLoadMore(int limitStart) {
////                if(productsResponse == null){
////                    Log.e(Constants.APP_TAG,"productsResponse is null");
////                    return;
////                }
////                if(productsResponse.products.next_page_url == null){
////                    Log.e(Constants.APP_TAG,"roductsResponse.products.next_page_url is null");
////                    return;
////                }
////                start = limitStart;
////                productListIem.add(null);
////                notifyCategoryAdapter(productListIem);
////                Log.e(Constants.APP_TAG,"api calling");
////                getCategory(start, false);
//            }
//        });

    }

    private final String FROM_GET_PRODUCTS = "FROM_GET_PRODUCTS";

    //define methods for request get_categories
    private void getCategories(int start) {
        RequestBody body = new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("limit", "1000")
                .add("start", String.valueOf(start).trim())
                .add("api_key", apiKey)
                .add("catId", catId)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_PRODUCTS, Constants.getCategories_api, body, true, ApiCallRequest.WHITE));
    }


    List<Products> productListIem;

    private void getProductsResponse(String response) {
        ProductListResponse productListResponse = new Gson().fromJson(response, ProductListResponse.class);
        if (productListResponse == null || productListResponse.responseText == null) {
            return;
        }
        if (productListResponse.responseText.success.equals("0") && productListResponse.responseText.message != null) {
            Toast.makeText(this, productListResponse.responseText.message, Toast.LENGTH_SHORT).show();
        }

        if (productListResponse.responseText.category != null) {
            tvItemCount.setText(productListResponse.responseText.category.pdtCount);
            tvCategoryName.setText(productListResponse.responseText.category.name);
            chanfgetToolbarText(productListResponse.responseText.category.name);
            try {
//                Glide.with(this).load(productListResponse.responseText.category.getImage()).placeholder(R.drawable.burgur).centerCrop().into(iv_items_categoryImage);
                Picasso.with(this).load(productListResponse.responseText.category.getImage())
//                        .placeholder(R.drawable.pizza)
                        .into(ivCategory);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (productListIem == null) {
            productListIem = new ArrayList<>();
        }

        if (productListIem.size() > 0 && productListIem.get(productListIem.size() - 1) == null) {
            productListIem.remove(productListIem.size() - 1);
        }

        if (productListResponse.responseText.productsList != null) {
            productListIem.addAll(productListResponse.responseText.productsList);
        }

        List<Categories> categories = productListResponse.responseText.categories;

        notifySubCategoryAdapter(categories);
//        subCategoryFlowLayout.removeAllViews();
//        if (categories != null && categories.size() > 0) {
//            for (Categories category : categories) {
//                TextView textView = buildLabel(category);
//                subCategoryFlowLayout.addView(textView);
//            }
//        } else {
//            subCategoryFlowLayout.removeAllViews();
//        }

        notifyProductListAdapter(productListIem);

        if (productListResponse.responseText.cartList == null) {
            Log.e(Constants.APP_TAG, "cart is empty");
            basketHelp = new BasketHelp(this);
            if (basketHelp.rv_Cart != null)
                basketHelp.rv_Cart.setVisibility(View.INVISIBLE);
            return;
        }

        List<Products> cartList = productListResponse.responseText.cartList;
        try {
            basketHelp = new BasketHelp(this, cartList, new JSONObject(response).optJSONObject("response_text"));
        } catch (JSONException e) {
            basketHelp = new BasketHelp(this, cartList, basketHelper.getPriceObjects());
        }
        checkProductAvailableInCart(cartList);
        setCartCount(false);
    }


    private TextView buildLabel(final Categories categories) {
        if (categories == null) {
            return new TextView(this);
        }
        TextView textView = new TextView(this);
        textView.setText(categories.name);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        int padding = Utils.getPxFromDp(this, 3f);
        textView.setPadding(padding, padding, padding, padding);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
//        textView.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/"+"SFProDisplay-Light.ttf"));
//        textView.setPadding((int)dpToPx(12), (int)dpToPx(2), (int)dpToPx(12), (int)dpToPx(2));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(Utils.getPxFromDp(this, 8f),
                Utils.getPxFromDp(this, 5f),
                5,
                0);
        textView.setLayoutParams(params);

//        textView.setBackgroundResource(genaratedBg);
        textView.setBackgroundColor(Color.GRAY);

        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductListActivity.class);
                intent.putExtra("catId", categories.catId);
                intent.putExtra("itemsCount", categories.pdtCount);
                intent.putExtra("catName", categories.name);
                intent.putExtra("catImg", categories.getImage());
                startActivity(intent);
            }
        });

        return textView;
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

        if (productListIem == null) {
            productListIem = new ArrayList<>();
        }

        checkProductAvailableInCart(cartList);

//        if (cartList.size() > 0) {
//            for (int m = 0; m < productListIem.size(); m++) {
//                for (int i = 0; i < cartList.size(); i++) {
//                    if (cartList.get(i).pdtId.equals(productListIem.get(m).pdtId)) {
//                        productListIem.set(m, copyCartItemToProduct(productListIem.get(m), cartList.get(i)));
//                        break;
//                    } else {
//                        productListIem.set(m, copyCartItemToProduct(productListIem.get(m), null));
//                    }
//                }
//            }
//        } else {
//            for (int i = 0; i < productListIem.size(); i++) {
//                productListIem.set(i, copyCartItemToProduct(productListIem.get(i), null));
//            }
//        }
        setCartCount(false);
        notifyProductListAdapter(productListIem);
    }


    private void checkProductAvailableInCart(List<Products> cartList) {
        if (productListIem == null) {
            return;
        }
        if (cartList != null && cartList.size() > 0) {

            for (int m = 0; m < productListIem.size(); m++) {
                for (int i = 0; i < cartList.size(); i++) {
                    if (cartList.get(i).pdtId.equals(productListIem.get(m).pdtId)) {
                        productListIem.set(m, copyCartItemToProduct(productListIem.get(m), cartList.get(i)));
                        break;
                    } else {
                        productListIem.set(m, copyCartItemToProduct(productListIem.get(m), null));
                    }
                }
            }
        } else {
            for (int i = 0; i < productListIem.size(); i++) {
                productListIem.set(i, copyCartItemToProduct(productListIem.get(i), null));
            }
        }
    }

    private Products copyCartItemToProduct(Products productListIem, Products cart) {
        if (cart == null) {
            productListIem.cartCount = "0";
            productListIem.selectedForce = new ArrayList<>();
            productListIem.selectedModifiers = new ArrayList<>();
            productListIem.unit_id = "";
        } else {
            productListIem.cartCount = cart.quantity;
            productListIem.selectedForce = cart.selectedForce;
            productListIem.selectedModifiers = cart.selectedModifiers;
            productListIem.unit_id = cart.unit_id;
        }
        return productListIem;
    }

    private void notifyProductListAdapter(List<Products> productsList) {
        if (productsList == null) {
            productsList = new ArrayList<>();
        }

        if (productListAdapter != null) {
            productListAdapter.setAdapterData(productsList);
        }
    }


    private void notifySubCategoryAdapter(List<Categories> categoriesList) {
        if (categoriesList == null) {
            categoriesList = new ArrayList<>();
        }
        if (categoriesList.size() > 0) {
            groupSubCategory.setVisibility(View.VISIBLE);
        } else {
            groupSubCategory.setVisibility(View.GONE);
        }
        subCategoriesAdapter.setAdapterData(categoriesList);
    }


    boolean isBack = false;

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse) {
        switch (eventBusResponse.type) {
            case EventBusResponse.UPDATE_CART:
                updateItemscartConut(eventBusResponse);
                break;
        }
    }


    private void updateItemscartConut(EventBusResponse eventBusResponse) {
        if (productListIem != null) {
            for (int i = 0; i < productListIem.size(); i++) {
                if (productListIem.get(i).pdtId.equalsIgnoreCase(eventBusResponse.productId)) {
                    productListIem.get(i).cartCount = String.valueOf(eventBusResponse.quantity);
                    notifyProductListAdapter(productListIem);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        try {
            if (getIntent().getBooleanExtra("isFromNotification", false)) {
                setResult(Activity.RESULT_CANCELED);
                Intent mIntent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mIntent);
                ActivityCompat.finishAffinity(this);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            String response = responseBus.response;
            JSONObject object = new JSONObject(responseBus.response);
            basketHelper.setPriceObjects(object.getJSONObject("response_text"));
            String resp_code = object.optString("response_code");

            switch (responseBus.FROM) {
                case FROM_GET_PRODUCTS:
                    getProductsResponse(response);
                    break;
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
