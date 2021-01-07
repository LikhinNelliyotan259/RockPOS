package com.rockchipme.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.ViewModel;
import com.rockchipme.app.adapters.ForcedQuestionAdapter;
import com.rockchipme.app.adapters.ModifiersAdapter;
import com.rockchipme.app.adapters.RecentItemsAdapter;
import com.rockchipme.app.adapters.UnitsAdapter;
import com.rockchipme.app.custom.QuantityPicker;
import com.rockchipme.app.custom.SpannableCustom;
import com.rockchipme.app.helpers.BasketHelp;
import com.rockchipme.app.helpers.BasketHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.helpers.Utils;
import com.rockchipme.app.models.CartResponse;
import com.rockchipme.app.models.ProductDetailsResponse;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.models.Unit;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ItemDetailsActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish {

    //    List<Item> listItems = new ArrayList<>();
    RecyclerView rvRecent;
    RecentItemsAdapter adapterRecent;

    boolean isFromNotification = false;

    TextView tvItemdetails, tvItemName, tvItemCurrentPrice;//, tvItemDetailsCartCount;
    ImageView ivMain, /*ivItemDetailsAddCart, ivItemDetailsRemoveCart,*/
            ivFavourite;
    BasketHelp basketHelp;
    String productId;

    Group groupUnit, groupForce, groupAddOns;

    QuantityPicker quantityPicker;
    EditText etRemarks;

    boolean isBack = false;

//    String responseForCart = null;
//    List<Cart> cartList = new ArrayList<>();
//    List<Products> recentlyViewedList = new ArrayList<>();

    ArrayList<String> imagesArray = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_item_details_new;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
//        initActivityTransitions();

//        Log.e(Constants.APP_TAG,"price object = "+new BasketHelper(this).getPriceObjects().toString());

//        ViewCompat.setTransitionName(findViewById(R.id.ivMain), EXTRA_IMAGE);
//        supportPostponeEnterTransition();

        initialize();
        setUpToolBar(false, "DETAILS", true);
        setUpRecent();

        isFromNotification = getIntent().getBooleanExtra("isFromNotification", false);

        setBottomSheetBehavior();

        ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageViewer();
            }
        });

        productId = getIntent().getStringExtra("pdtId") == null ? "" : getIntent().getStringExtra("pdtId");

        getProductDetailsApi();
    }

    private void updateCart(Products product) {
        int cartCount = 0;
        if (quantityPicker != null) {
            cartCount = quantityPicker.getValue();
        }
//        int cartCount = Integer.parseInt(tvItemDetailsCartCount.getText().toString().trim());

//        if (((product.modifiers != null && product.modifiers.size() > 0) ||
//                (product.force != null && product.force.size() > 0) ||
//                (product.units != null && product.units.size() > 1)) &&
//                (product.cartCount == null || product.cartCount.trim().equals("0"))) {
//            Intent intent = new Intent(getApplicationContext(), ProductOptionsActivity.class);
//            intent.putExtra("product", product);
//            intent.putExtra("cartCount", cartCount);
//            startActivity(intent);
//        } else {

        product.remarks = etRemarks.getText().toString().trim().replaceAll(" +", " ");

        if (forcedQuestionAdapter.getItemCount() > 0) {
            if (forcedQuestionAdapter.selectedIds == null) {
                try {
                    float y = rvForceQuestion.getY() + rvForceQuestion.getChildAt(0).getY();
                    ((NestedScrollView) findViewById(R.id.scroll)).smoothScrollTo(0, (int) y - Utils.getPxFromDp(this, 120));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Please select a forced option", Toast.LENGTH_SHORT).show();
                return;
            } else {
                product.selectedForce = new ArrayList<>();
                product.selectedForce.add(forcedQuestionAdapter.selectedIds);
            }
        }

        if (unitsAdapter.getItemCount() > 0) {
            if (unitsAdapter.selectedUnitId == null) {
                return;
            } else {
                product.unit_id = unitsAdapter.selectedUnitId.unitId;
            }
        }

        product.selectedModifiers = modifiersAdapter.selectedIds;


        basketHelper.updateCart(cartCount, product, "ItemDetailsActivity");
//        }
    }

    private void openImageViewer() {
        if (imagesArray != null && imagesArray.size() > 0) {
            Bundle b = new Bundle();
            b.putStringArrayList("images", imagesArray);
            b.putString("position", 0 + "");
            Intent intent = new Intent(getApplicationContext(), ImageViewerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    private final String FROM_GET_PRODUCT_DETAILS = "FROM_GET_PRODUCT_DETAILS";

    private void getProductDetailsApi() {
        RequestBody body = new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apiKey)
                .add("pdtId", productId)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_PRODUCT_DETAILS, Constants.getProductDetails_api, body, true, ApiCallRequest.WHITE));
    }


    private final String FROM_ADD_TO_FAVOURITE = "FROM_ADD_TO_FAVOURITE";

    private void addToFavouriteApi(String isAdd) {
        RequestBody body = new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apiKey)
                .add("pdtId", productId)
                .add("add", isAdd)
                .build();
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_ADD_TO_FAVOURITE,
                Constants.addToFavourites_api, body, false, "Loading..."));
    }

    ForcedQuestionAdapter forcedQuestionAdapter;
    UnitsAdapter unitsAdapter;
    ModifiersAdapter modifiersAdapter;
    RecyclerView rvForceQuestion, rvUnit, rvModifiers;
    Button btnAddToCart;

    private void initialize() {

        ivMain = (ImageView) findViewById(R.id.ivMain);
        ivFavourite = (ImageView) findViewById(R.id.ivFavourite);

        quantityPicker = findViewById(R.id.quantityPicker);
        quantityPicker.setValue(getIntent().getIntExtra("qty", 1));
        quantityPicker.setMin(1);


        quantityPicker.setValueChangedListener((value, action) -> {
            setTotalPrice();
        });

//        ivItemDetailsAddCart = (ImageView) findViewById(R.id.iv_item_details_addCart);
//        ivItemDetailsRemoveCart = (ImageView) findViewById(R.id.iv_item_details_removeCart);
//        tvItemDetailsCartCount = (TextView) findViewById(R.id.tv_item_details_cartCount);

        tvItemdetails = (TextView) findViewById(R.id.tvItemDetails);
        tvItemName = (TextView) findViewById(R.id.tProductName);
        tvItemCurrentPrice = (TextView) findViewById(R.id.tvProductPrice);

        etRemarks = findViewById(R.id.etRemarks);

        btnAddToCart = findViewById(R.id.btnAddToCart);

        rvRecent = (RecyclerView) findViewById(R.id.rv_recent);

        rvForceQuestion = findViewById(R.id.rvForceQuestion);
        rvUnit = findViewById(R.id.rvUnit);
        rvModifiers = findViewById(R.id.rvModifiers);

        groupUnit = findViewById(R.id.groupUnit);
        groupForce = findViewById(R.id.groupForce);
        groupAddOns = findViewById(R.id.groupAddOns);

        listExpandOrCollapse(groupUnit, findViewById(R.id.imageView10), rvUnit);
        listExpandOrCollapse(groupForce, findViewById(R.id.imageView11), rvForceQuestion);
        listExpandOrCollapse(groupAddOns, findViewById(R.id.imageView12), rvModifiers);

        forcedQuestionAdapter = new ForcedQuestionAdapter(this, null);
        rvForceQuestion.setAdapter(forcedQuestionAdapter);

        unitsAdapter = new UnitsAdapter(this, null);
        rvUnit.setAdapter(unitsAdapter);

        modifiersAdapter = new ModifiersAdapter(this, null);
        rvModifiers.setAdapter(modifiersAdapter);


        rvRecent.setHasFixedSize(true);
        rvRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterRecent = new RecentItemsAdapter(this, rvRecent, currency);
        rvRecent.setAdapter(adapterRecent);
    }

    public void collapseUnitList() {
        slideToTop(rvUnit, findViewById(R.id.imageView10));
    }

    public void collapseForceList() {
        slideToTop(rvForceQuestion, findViewById(R.id.imageView11));
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
//            Explode transition = new Explode();
//            ChangeBounds transition = new ChangeBounds();
//            ChangeImageTransform transition = new ChangeImageTransform();
//            ChangeImageTransform transition = new ChangeImageTransform();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }


    public void setUpReadMore() {
        makeTextViewResizable(tvItemdetails, 2, "More", true);
    }

    Products productDetail;

    private void getProductDetailsApiResponse(String response) {
        try {
            ProductDetailsResponse productDetailsResponse = new Gson().fromJson(response, ProductDetailsResponse.class);
            if (productDetailsResponse == null || productDetailsResponse.responseText == null) {
                return;
            }
            productDetail = productDetailsResponse.responseText.products;

            if (productDetail == null) {
                showErrorMessage(productDetailsResponse.responseText.status);
                return;
            }
            List<Products> recentlyViewedList = productDetailsResponse.responseText.recentlyViewedList;
            List<Products> cartList = productDetailsResponse.responseText.cartList;
            if (productDetail.imageList != null && productDetail.imageList.size() > 0) {
                Picasso.with(this).load(productDetail.imageList.get(0).getImage())
//                        .placeholder(R.drawable.banner)
                        .into(ivMain);
                imagesArray = new ArrayList<>();
                for (int i = 0; i < productDetail.imageList.size(); i++) {
                    imagesArray.add(productDetail.imageList.get(i).getImage());
                }
            }

            tvItemName.setText(productDetail.name);
            chanfgetToolbarText(productDetail.name);

            try {
                totalPrice = Double.parseDouble(productDetail.rate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tvItemCurrentPrice.setText(currency + " " + productDetail.rate);
            quantityPicker.setValue(Integer.parseInt(productDetail.cartCount));
//            tvItemDetailsCartCount.setText(productDetail.cartCount);

            if (productDetail.favourite.equalsIgnoreCase("1")) {
                ivFavourite.setImageResource(R.drawable.fav_selected);
            } else {
                ivFavourite.setImageResource(R.drawable.fav_unselect);
            }

            ivFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productDetail.favourite.equalsIgnoreCase("0")) {
                        ivFavourite.setImageResource(R.drawable.fav_selected);
                        productDetail.favourite = "1";
                        addToFavouriteApi("1");
                    } else {
                        ivFavourite.setImageResource(R.drawable.fav_unselect);
                        productDetail.favourite = "0";
                        addToFavouriteApi("0");
                    }
                }
            });

            setModifiersAdapterData(productDetail.modifiers);
            setUnitAdapterData(productDetail.units);
            setForceAdapterData(productDetail.force);

            setTotalPrice();

            btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateCart(productDetail);
                }
            });

            tvItemdetails.setText(productDetail.desc);

//            tvItemdetails.post(new Runnable() {
//                @Override
//                public void run() {
//                    int length = tvItemdetails.getLineCount();
//                    Log.e(Constants.APP_TAG,"textview length "+length);
//                    if (length > 2) {
//                        setUpReadMore();
//                    }
//                }
//            });

            int length = tvItemdetails.getLineCount();
            Log.e(Constants.APP_TAG, "textview length " + length);
            if (length > 2) {
                setUpReadMore();
            }

            ProductDetailsResponse.ResponseText responseText = productDetailsResponse.responseText;
//            JSONObject priceObjects = basketHelper.setPriceObjects(responseText.subTotal, responseText.deliveryCharge, responseText.grandTotal);

            if (cartList == null) {
                cartList = new ArrayList<>();
            }


            for (Products cart : cartList) {
                if (productId.equals(cart.pdtId)) {
                    copyCartItemToProduct(cart);
                }
            }

            basketHelp = new BasketHelp(ItemDetailsActivity.this, cartList, new JSONObject(response).optJSONObject("response_text"));

            setCartCount(false);


            if (adapterRecent != null) {
                if (recentlyViewedList == null) {
                    recentlyViewedList = new ArrayList<>();
                }
                adapterRecent.setAdapterData(recentlyViewedList, currency);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showErrorMessage(String message) {
        if (message == null || message.trim().length() < 1){
            message = "Something went wrong";
        }

        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(message)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    onBackPressed();
                })
                .show();
    }


    public void setUnitAdapterData(List<Unit> units) {
        if (units == null || units.size() < 1) {
            units = new ArrayList<>();
            groupUnit.setVisibility(View.GONE);
            Log.d(Constants.APP_TAG, "groupUnit.setVisibility(View.GONE)");
        } else {
            groupUnit.setVisibility(View.VISIBLE);
            groupUnit.requestLayout();

            String itemPrice = "";
            if (units.size() == 1) {
                units.get(0).primaryUnit = "1";
                itemPrice = units.get(0).price;
            } else {
                for (Unit unit : units) {
                    if (unit.primaryUnit.equals("1")) {
                        itemPrice = unit.price;
                        break;
                    }
                }
                if (itemPrice.length() < 1) {
                    units.get(0).primaryUnit = "1";
                    itemPrice = units.get(0).price;
                }
            }
            try {
                totalPrice = Double.parseDouble(itemPrice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvItemCurrentPrice.setText(currency + " " + itemPrice);
        }
        unitsAdapter.setAdapterData(units);
    }

    public void setModifiersAdapterData(List<List<Products>> modifiers) {
        if (modifiers == null || modifiers.size() < 1) {
            modifiers = new ArrayList<>();
            groupAddOns.setVisibility(View.GONE);
            Log.d(Constants.APP_TAG, "groupAddOns.setVisibility(View.GONE)");
        } else {
            groupAddOns.setVisibility(View.VISIBLE);
        }
        modifiersAdapter.setAdapterData(modifiers);
    }

    public void setForceAdapterData(List<List<Products>> force) {
        if (force == null || force.size() < 1) {
            force = new ArrayList<>();
            groupForce.setVisibility(View.GONE);
            Log.d(Constants.APP_TAG, "groupForce.setVisibility(View.GONE)");
        } else {
            groupForce.setVisibility(View.VISIBLE);
        }
        forcedQuestionAdapter.setAdapterData(force);
    }

    double totalPrice = 0;

    public void setTotalPrice() {
        try {
            int cartCount = quantityPicker.getValue();
            if (unitsAdapter.selectedUnitId != null) {
                totalPrice = Double.parseDouble(unitsAdapter.selectedUnitId.price);
                tvItemCurrentPrice.setText(currency + " " + unitsAdapter.selectedUnitId.price);
            }

            if (forcedQuestionAdapter.selectedIds != null) {
                totalPrice += Double.parseDouble(forcedQuestionAdapter.selectedIds.rate);
            }

            for (Products selectedModifiers : modifiersAdapter.selectedIds) {
                totalPrice += Double.parseDouble(selectedModifiers.rate);
            }
            totalPrice *= cartCount;


            btnAddToCart.setText("Add " + currency + " " + Constants.changeDecimalFormat(totalPrice));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUpRecent() {

//        adapterRecent.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                Handler handler = new Handler();
//                //Toast.makeText(HomeScreenActivity.this, "LoadMore", Toast.LENGTH_SHORT).show();
//                listItems.add(null);
//                adapterRecent.notifyItemInserted(listItems.size() - 1);
//                //Toast.makeText(mContext , "onLoadMore", Toast.LENGTH_SHORT).show();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //   remove progress item
//                        listItems.remove(listItems.size() - 1);
//                        adapterRecent.notifyItemRemoved(listItems.size());
//                        //add items one by one
//                        int start = listItems.size();
//                        int end = start + 3;
//
//                        for (int i = start + 1; i <= end; i++) {
//                            listItems.add(new Item("Burger", "", 0.00));
//                            adapterRecent.notifyItemInserted(listItems.size());
//                        }
//                        adapterRecent.setLoaded();
//                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
//                    }
//                }, 2000);
//
//            }
//        });

    }

    // To animate view slide out from top to bottom
    private void slideToBottom(View view, ImageView iv) {
//        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
//        animate.setDuration(500);
//        animate.setFillAfter(true);
//        view.startAnimation(animate);
        iv.animate().rotation(0).start();
        view.setVisibility(View.VISIBLE);
    }

    // To animate view slide out from bottom to top
    private void slideToTop(View view, ImageView iv) {
//        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
//        animate.setDuration(500);
//        animate.setFillAfter(true);
//        view.startAnimation(animate);

        iv.animate().rotation(180).start();
        view.setVisibility(View.GONE);
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

        if (cartList != null && cartList.size() > 0) {
            basketHelp = new BasketHelp(ItemDetailsActivity.this, cartList, response.optJSONObject("response_text"));
            basketHelp.rv_Cart.setVisibility(View.VISIBLE);
        } else {
            basketHelp = new BasketHelp(this);
            basketHelp.rv_Cart.setVisibility(View.INVISIBLE);
        }


//        String responseForCart = null;
//        Gson gson = new Gson();

//        try {
//            EventBusResponse eventBusResponse = new EventBusResponse();
//            eventBusResponse.type = EventBusResponse.UPDATE_CART;
//            eventBusResponse.productId = responseBus.productId;
//            eventBusResponse.quantity = responseBus.currentValue;
//            EventBus.getDefault().post(eventBusResponse);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if (cartList.size() > 0) {

            for (int i = 0; i < cartList.size(); i++) {
                if (cartList.get(i).pdtId.equals(productId)) {
                    int qty = 0;
                    try {
                        qty = Integer.parseInt(cartList.get(i).quantity);
                    } catch (Exception e) {
                    }
                    quantityPicker.setValue(qty);
                    copyCartItemToProduct(cartList.get(i));
                    break;
                }
            }
        } else {
            quantityPicker.setValue(0);
            copyCartItemToProduct(null);
        }

        if (productDetail != null) {
            productDetail.cartCount = quantityPicker.getValue() + "";
        }

        if (cartList.size() > 0) {
            basketHelp.rv_Cart.setVisibility(View.VISIBLE);
        } else {
            basketHelp.rv_Cart.setVisibility(View.INVISIBLE);
        }
        setCartCount(false);
    }

    public void listExpandOrCollapse(Group group, ImageView iv, RecyclerView rv) {
        int refIds[] = group.getReferencedIds();
        View view = iv.getRootView();
        for (int id : refIds) {
            view.findViewById(id).setOnClickListener(view1 -> {
                try {
                    if (rv.getVisibility() == View.VISIBLE) {
                        slideToTop(rv, iv);
                    } else {
                        slideToBottom(rv, iv);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }


    // if cart item == product
    private void copyCartItemToProduct(Products cart) {
        if (productDetail == null) {
            return;
        }
        if (cart != null) {
            productDetail.cartCount = cart.quantity;
            productDetail.selectedForce = cart.selectedForce;
            productDetail.selectedModifiers = cart.selectedModifiers;
            productDetail.unit_id = cart.unit_id;
            return;
        }
        productDetail.cartCount = "0";
        productDetail.selectedForce = new ArrayList<>();
        productDetail.selectedModifiers = new ArrayList<>();
        productDetail.unit_id = "";
    }


    private static final String EXTRA_IMAGE = "com.sibin.homedelivery.extraImage";
    private static final String EXTRA_TITLE = "com.sibin.homedelivery.extraTitle";

    public static void navigate(AppCompatActivity activity, View transitionImage, ViewModel viewModel) {
        Intent intent = new Intent(activity, ItemDetailsActivity.class);
        intent.putExtra(EXTRA_IMAGE, viewModel.getImage());
        intent.putExtra(EXTRA_TITLE, viewModel.getText());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_IMAGE);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
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

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        if (vto.isAlive()) {
            Log.e(Constants.APP_TAG, "ViewTreeObserver vto isAlive condiotion");
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    ViewTreeObserver obs = tv.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    if (maxLine == 0) {
                        int lineEndIndex = tv.getLayout().getLineEnd(0);
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);
                    } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                        int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                        String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);
                    } else {
                        int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                        tv.setText(text);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                        tv.setText(
                                addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                        viewMore), TextView.BufferType.SPANNABLE);
                    }
                }
            });
        } else {
            Log.e(Constants.APP_TAG, "ViewTreeObserver vto isAlive else condiotion");
        }

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            ssb.setSpan(new SpannableCustom(false) {
                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, "More", true);
                    }

                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    @Override
    public void onBackPressed() {

        if (isFromNotification) {
            setResult(Activity.RESULT_CANCELED);
            Intent mIntent = new Intent(getApplicationContext(), HomeScreenActivity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mIntent);
            finish();
            ActivityCompat.finishAffinity(ItemDetailsActivity.this);
        }
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse) {

    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        try {
            String response = responseBus.response;
            JSONObject object = new JSONObject(response);

            switch (responseBus.FROM) {
                case FROM_GET_PRODUCT_DETAILS:
                    getProductDetailsApiResponse(response);
                    break;
                case FROM_GET_CART:
                case BasketHelper.FROM_UPDATE_CART:
                    updateCartDetails(object, response);
                    break;
                case BasketHelp.FROM_PLACE_ORDER:
                    basketHelp.placeOrderResponse(ItemDetailsActivity.this, object);
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
