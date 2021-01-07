package com.rockchipme.app.activities;

import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.rockchipme.app.BuildConfig;
import com.rockchipme.app.R;
import com.rockchipme.app.adapters.CategoryMenuAdapter;
import com.rockchipme.app.adapters.RecentItemsAdapter;
import com.rockchipme.app.adapters.RecommendCategoriesAdapter;
import com.rockchipme.app.helpers.AddressHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.helpers.DrawerLayoutMenuHelper;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.helpers.LocationUpdateHelper;
import com.rockchipme.app.helpers.PreferenceUtil;
import com.rockchipme.app.helpers.SignupAndSigninApiCall;
import com.rockchipme.app.helpers.Utils;
import com.rockchipme.app.models.Categories;
import com.rockchipme.app.models.HomeResponse;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.models.RestaurantDetails;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.FormBody;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class HomeScreenActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish {

    private List<HomeResponse.Slider> sliderList = new ArrayList<>();

    private RecommendCategoriesAdapter adapterCategories;
    private RecentItemsAdapter adapterRecent;
    private CardView cvCategories;

    TextView tvNoOfCategories, tvRecomentedCategorymore, tvRecentlyViewedMore;
    LinearLayout lnRecomentedCategories, lnRecentlyViewes;

    private boolean exit;
    private DrawerLayout drawerLayout;

    boolean isFirst = true;
    RestaurantDetails restaurantDetails = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home_screen_wdrawer;
    }

    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            PreferenceUtil preferenceUtil = new PreferenceUtil(this);
            restaurantDetails = preferenceUtil.getOutletStoreDetails();
            if (restaurantDetails.rest_key.trim().length() > 0) {
                Constants.REST_KEY = restaurantDetails.rest_key;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeViews();
        exit = false;
        setupDrawerLayout();
        setUpToolBar(true, "", true);
        setUpRecent();

        String homeResponse = getIntent().getStringExtra("homeData");
        if (homeResponse != null) {
            getHomeDataApiResponse(homeResponse);
        } else {
            getHomeDataApi();
        }

        if (locationUpdateHelperIsNotNull()) {
            locationUpdateHelper.setLocationUpdateListener(this::storeLocation);
        }

        cvCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainCategoriesActivity.class));

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (locationUpdateHelperIsNotNull()) {
            locationUpdateHelper.onActivityResult(requestCode, resultCode, data);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (locationUpdateHelperIsNotNull()) {
//            locationUpdateHelper.stopLocationUpdates();
//        }
//    }

    private void storeLocation(Location location1) {
        location = location1;
        if (location1 != null) {
            getHomeDataApi();
            Log.d(Constants.APP_TAG, "latitude:" + location1.getLatitude());
        }
    }

    //    FABRevealMenu fabMenu;
    Button btnMenu;
    TextView tvRestaurantName, tvRestaurantDistance;

    private void initializeViews() {
        cvCategories = findViewById(R.id.cv_categories);
//        ll_basket_peek = (LinearLayout) findViewById(R.id.ll_basket_peek);
        RecyclerView rvCategories = findViewById(R.id.rv_categories);
        RecyclerView rvRecent = findViewById(R.id.rv_recent);

        tvNoOfCategories = findViewById(R.id.tv_noOfCategories);
        tvRecentlyViewedMore = findViewById(R.id.tv_recentlyViewedMore);
        tvRecomentedCategorymore = findViewById(R.id.tv_recomendedCategoryMore);

        lnRecentlyViewes = findViewById(R.id.lnRecentlyViewedItems);
        lnRecomentedCategories = findViewById(R.id.lnRecomendedCategories);

        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantDistance = findViewById(R.id.tvRestaurantDistance);

        setSelectedRestaurantName(null);

        rvRecent.setHasFixedSize(true);
        rvRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterRecent = new RecentItemsAdapter(this, rvRecent, currency);
        rvRecent.setAdapter(adapterRecent);

//
        rvCategories.setHasFixedSize(true);
        RecommendCategoriesAdapter.CURRENT_POSITION = 0;
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterCategories = new RecommendCategoriesAdapter(this, rvCategories, currency);
        // set the adapter object to the Recyclerview
        rvCategories.setAdapter(adapterCategories);


        btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setCategoryItems(new ArrayList<>());
//                showPopup();
                showDialog();
            }
        });
//        fabMenu = findViewById(R.id.fabMenu);

//        try {
//            fabMenu.bindAnchorView(btnMenu);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    private LocationUpdateHelper locationUpdateHelper;

    private boolean locationUpdateHelperIsNotNull() {
        if (locationUpdateHelper == null) {
            locationUpdateHelper = new LocationUpdateHelper(this);
        }
        return true;
    }

    List<Categories> categories;

    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.popup_window);

        RecyclerView rv = dialog.findViewById(R.id.rv);
        rv.setAdapter(new CategoryMenuAdapter(this, categories, dialog));

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams wmlp = window.getAttributes();
            window.setLayout(Utils.getPxFromDp(this, 230), ViewGroup.LayoutParams.WRAP_CONTENT);
            wmlp.gravity = Gravity.BOTTOM | Gravity.END;
            wmlp.x = Utils.getPxFromDp(this, 0);
            wmlp.y = Utils.getPxFromDp(this, 0);
        }

        dialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (locationUpdateHelperIsNotNull()) {
            locationUpdateHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private final String FROM_GET_HOME_DATA = "FROM_GET_HOME_DATA";

    private void getHomeDataApi() {
        String token = FirebaseInstanceId.getInstance().getToken();
        token = token == null ? "" : token;

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("rest_key", Constants.REST_KEY);
        builder.add("rest_key_main", Constants.REST_KEY_MAIN);
        builder.add("api_key", apiKey);
        builder.add("deviceToken", token);
        builder.add("appVersion", BuildConfig.VERSION_CODE + "");
        builder.add("deviceOS", "ANDROID");
        builder.add("deviceModel", getDeviceName());
        if (location != null) {
            builder.add("latitude", location.getLatitude() + "");
            builder.add("longitude", location.getLongitude() + "");
        }
        String progressBg = ApiCallRequest.WHITE;
        if (!isFirst) {
            progressBg = ApiCallRequest.TRANSPARENT;
        }
//        apiCallServiceTask.requestApi(ApiResponseBus.RC_GET_HOME_DATA_API, false, body, Constants.SERVER_URL+ Constants.getHomeData_api,"Loading...");
        apiCallServiceTask.requestApi(new ApiCallRequest(FROM_GET_HOME_DATA, Constants.getHomeData_api, builder.build(), true, progressBg));
    }

    private String getDeviceName() {
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return model;
            }
            return manufacturer + " " + model;
        } catch (Exception e) {
        }
        return "";
    }

    private void setupDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Snackbar.make(content, menuItem.getTitle() + " pressed", Snackbar.LENGTH_LONG).show();
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    //int[] sampleImages;// = {R.drawable.best_buy_bag, R.drawable.best_buy_bnr, R.drawable.best_buy_dress, R.drawable.best_buy_drinks, R.drawable.best_buy_fruits};
    public void setUpSlider() {
        CarouselView carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setImageListener(imageListener);
        carouselView.setPageCount(sliderList.size());
//        imagesArray = new ArrayList<>();
//        for (int i=0; i<sliderList.size(); i++){
//         imagesArray.add(sliderList.get(i).image);
//        }
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(final int position, ImageView imageView) {
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (sliderList != null && sliderList.size() > 0) {
                Picasso.with(getApplicationContext()).load(sliderList.get(position).getImage()).into(imageView);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sliderList != null && sliderList.size() > position) {
                        switch (sliderList.get(position).type) {
                            case "1":
                                Intent intent = new Intent(getApplicationContext(), ProductListActivity.class);
                                intent.putExtra("catId", sliderList.get(position).value);
//                                intent.putExtra("itemsCount",categoriesList.get(position).pdtCount);
//                                intent.putExtra("catName",categoriesList.get(position).name);
//                                intent.putExtra("catImg",categoriesList.get(position).getImage());
                                startActivity(intent);
                                break;

                            case "2":
                                Intent detailsIntent = new Intent(getApplicationContext(), ItemDetailsActivity.class);
                                detailsIntent.putExtra("pdtId", sliderList.get(position).value);
                                startActivity(detailsIntent);
                                break;
                            case "3":
                                String url = sliderList.get(position).value;
                                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                                    url = "http://" + url;
                                }
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                browserIntent.setData(Uri.parse(url));
                                startActivity(browserIntent);
                                break;
                        }
                    }
                }
            });
        }
    };

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
//                        for (int i = start + 1; i <= end; i++) {
//                            listItems.add(new Item("Burger", "", 0.00));
//                            adapterRecent.notifyItemInserted(listItems.size());
//                        }
//                        adapterRecent.setLoaded();
//                        //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
//                    }
//                }, 2000);
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new DrawerLayoutMenuHelper(this).setDrawerLayoutMenu();
        setCartCont();
        if (!isFirst) {
            getHomeDataApi();
        }
        isFirst = false;
    }

    @Override
    public void onBackPressed() {
//        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        } else {
//            super.onBackPressed();
//        }
//
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
//
//        if(fabMenu.isShowing()) {
//            fabMenu.closeMenu();
//            return;
//        }

        if (exit) {
            finish();
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press Back again to Exit.", Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 5000);
        }

    }


    private void getHomeDataApiResponse(String response) {
        try {
            HomeResponse homeResponse = new Gson().fromJson(response, HomeResponse.class);
            if (homeResponse != null && homeResponse.responseText != null) {
                try {
                    if (homeResponse.responseText.message != null && homeResponse.responseText.message.equalsIgnoreCase("Invalid ApiKey or Restkey")) {
                        new SignupAndSigninApiCall().logout(this, prefLogin, editorCart);
                        return;
                    }

                    if (homeResponse.responseText.status != null) {
                        Toast.makeText(this, homeResponse.responseText.status, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (homeResponse.responseText.version != null && homeResponse.responseText.version.size() > 0) {
                    if (homeResponse.responseText.version.get(0).forceUpdate.trim().equals("1")) {
                        appForceUpdate();
                    } else {
                        try {
                            int i = Integer.parseInt(homeResponse.responseText.version.get(0).version);
                            if (BuildConfig.VERSION_CODE < i) {
                                appUpdate();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    if (homeResponse.responseText.restaurantDetailsList != null && homeResponse.responseText.restaurantDetailsList.size() > 0) {
                        editorCart.putString(Constants.currency, homeResponse.responseText.restaurantDetailsList.get(0).currency).commit();
                        currency = homeResponse.responseText.restaurantDetailsList.get(0).currency;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sliderList = homeResponse.responseText.sliderList;
                List<Products> recomendedProductList = homeResponse.responseText.productsList;
                List<Products> recentlyViewedList = homeResponse.responseText.recentlyViewedList;
                categories = homeResponse.responseText.categories;

                if (adapterCategories != null) {
                    if (recomendedProductList == null) {
                        lnRecomentedCategories.setVisibility(View.GONE);
                        recomendedProductList = new ArrayList<>();
                    }
                    adapterCategories.setAdapterData(recomendedProductList, currency);
                }

                if (adapterRecent != null) {
                    if (recentlyViewedList == null) {
                        lnRecentlyViewes.setVisibility(View.GONE);
                        recentlyViewedList = new ArrayList<>();
                    }
                    adapterRecent.setAdapterData(recentlyViewedList, currency);
                }

                tvNoOfCategories.setText(homeResponse.responseText.categoriesCount);
                setUpSlider();

                editorCart.putString(Constants.cartCount, homeResponse.responseText.cartCount + "").commit();
                setCartCont();

                setSelectedRestaurantName(homeResponse.responseText.outlets);

                if (homeResponse.responseText.outlets != null && homeResponse.responseText.outlets.size() > 0) {
                    if (!homeResponse.responseText.outlets.get(0).rest_key.equals(Constants.REST_KEY)) {
                        showDialogForChangeOutlet();
                    }
                }

                if (homeResponse.responseText.addressList != null && homeResponse.responseText.addressList.size() > 0) {
                    new AddressHelper(this).storeAddress(homeResponse.responseText.addressList.get(0));
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAlreadyShowDialogForChangeOutlet = false;

    private void showDialogForChangeOutlet() {
        if (isAlreadyShowDialogForChangeOutlet) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You want to change outlet.");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    dialog.cancel();
                    Intent intent = new Intent(getApplicationContext(), OutletsActivity.class);
                    intent.putExtra("isFromDrawerMenu", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });

        builder.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder.create();
        alert11.show();
        isAlreadyShowDialogForChangeOutlet = true;
    }

    private void setCartCont() {
        if (tvCartCount != null && prefCart != null) {
            tvCartCount.setText(prefCart.getString(Constants.cartCount, "0"));
            if (prefCart.getString(Constants.cartCount, "0").equals("0")) {
                tvCartCount.setVisibility(View.INVISIBLE);
            } else {
                tvCartCount.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setSelectedRestaurantName(List<RestaurantDetails> outlets) {
        if (restaurantDetails == null) {
            return;
        }
        if (outlets != null && outlets.size() > 0) {
            for (RestaurantDetails outlet : outlets) {
                if (outlet.rest_key.equalsIgnoreCase(restaurantDetails.rest_key)) {
                    restaurantDetails = outlet;
                    break;
                }
            }
        }

        int textSize1 = getResources().getDimensionPixelSize(R.dimen._15sp);
        int textSize2 = getResources().getDimensionPixelSize(R.dimen._13sp);

        SpannableString name = new SpannableString(restaurantDetails.name);
        name.setSpan(new AbsoluteSizeSpan(textSize1), 0, restaurantDetails.name.length(), SPAN_INCLUSIVE_INCLUSIVE);

        String locationStr = ", " + restaurantDetails.location;
        SpannableString location = new SpannableString(locationStr);
        location.setSpan(new AbsoluteSizeSpan(textSize2), 1, locationStr.length(), SPAN_INCLUSIVE_INCLUSIVE);

        tvRestaurantName.setText(TextUtils.concat(name, location));

        if (restaurantDetails.distance != null && restaurantDetails.distance.trim().length() > 0) {
            String distanceStr = restaurantDetails.distance + " km";
            tvRestaurantDistance.setText(distanceStr);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse) {

    }


    private void appUpdate() {
        if (!Constants.isShowUpdatePopUp) {
            return;
        }
        Constants.isShowUpdatePopUp = false;
        if (!isFinishing()) {
            SweetAlertDialog dialog = new SweetAlertDialog(HomeScreenActivity.this, SweetAlertDialog.WARNING_TYPE);
            dialog.setTitleText("Update Available");

            String appName = getResources().getString(R.string.app_name);
            dialog.setContentText("A new version of " + appName + " is available");

            dialog.setConfirmText("Update");
            dialog.setCancelText("Close");
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    openPlayStore();
                }
            });
            dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void appForceUpdate() {
        if (!isFinishing()) {
            SweetAlertDialog dialog = new SweetAlertDialog(HomeScreenActivity.this, SweetAlertDialog.WARNING_TYPE);
            dialog.setTitleText("Update Available");

            String appName = getResources().getString(R.string.app_name);
            dialog.setContentText("A new version of " + appName + " is available");

            dialog.setConfirmText("Update");
            dialog.setCancelText("Exit");
            dialog.setCancelable(false);
            dialog.setConfirmClickListener(sweetAlertDialog -> {
                sweetAlertDialog.dismiss();
                openPlayStore();
            });
            dialog.setCancelClickListener(sweetAlertDialog -> {
                sweetAlertDialog.dismiss();
                getIntent().addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try {
                    ActivityCompat.finishAffinity(HomeScreenActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            });
            dialog.show();
        }
    }

    private void openPlayStore() {
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        Log.d(Constants.APP_TAG, "PACKAGE_NAME:" + PACKAGE_NAME);
        String link = "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME + "&hl=en";
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(browserIntent);
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        switch (responseBus.FROM) {
            case FROM_GET_HOME_DATA:
                getHomeDataApiResponse(responseBus.response);
                break;

        }
    }
}
