package com.rockchipme.app.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.Group;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.AddressListActivity;
import com.rockchipme.app.activities.CreateAddressActivty;
import com.rockchipme.app.activities.OrderHistoryActivty;
import com.rockchipme.app.adapters.BasketListAdapter;
import com.rockchipme.app.adapters.CouponPopupAdapter;
import com.rockchipme.app.models.Coupons;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.network.ApiCallRequest;
import com.rockchipme.app.network.ApiCallServiceTask;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Alisons on 9/14/2017.
 */
public class BasketHelp/*  implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener*/{

    private TextView tvTotalPrice, tv_taxPrice, tvDeliveryPrice, tvGrandTotal, tvSavedCoupon, tvLabelSaved, tvAddress,
            tv_bottomsheet_itemCount, tv_bottomsheet_price;

    private Button btnChangeOrEditAddress, btnAddAddress, btnPlaceOrder, btnAddVoucher;

    private ImageView ivRemoveCoupon;
    public RecyclerView rv_Cart;
    private String currency = "";

    private AlertDialog alertDialog;

    private SharedPreferences pref_cart, pref_login;
    private SharedPreferences.Editor editor_cart;

    private RadioGroup rgDeliveryType;

//    RelativeLayout rl_couponPopoup;


    private Activity _activity;
    private List<Products> cartList;

    private boolean isSkippedUser;

    private Group groupAddressDetails, groupNoAddress, groupCoupon;

    private String subTotal = "0.00", grand_total = "0.00", delivery_fee = "0.00", tax_price = "0.00";
    private JSONObject jsonObject;

    private BasketListAdapter basketListAdapter;


    public BasketHelp(Activity activity) {
        this._activity = activity;
        cartList = new ArrayList<>();

        Log.e(Constants.APP_TAG, "basket working");

        initializeView(_activity);

        initializeDatas(false);
    }

    public BasketHelp(Activity activity, List<Products> cartList, JSONObject jsonObject) {
        this._activity = activity;
        this.cartList = cartList;
        this.jsonObject = jsonObject;


//        pref_cart = activity.getSharedPreferences(Constants.PREF_CART,activity.MODE_PRIVATE);
//        editor_cart = pref_cart.edit();

//        Log.e(Constants.APP_TAG,"cartlist size: "+cartList.size());

        initializeView(_activity);

//        rl_couponPopoup.setVisibility(View.GONE);


        initializeDatas(false);


    }


    public BasketHelp() {
    }

    public void initializeView(Activity activity) {

        pref_cart = activity.getSharedPreferences(Constants.PREF_CART, activity.MODE_PRIVATE);
        pref_login = activity.getSharedPreferences(Constants.PREF_LOGIN, activity.MODE_PRIVATE);
        editor_cart = pref_cart.edit();
        isSkippedUser = pref_login.getBoolean(Constants.isSkipUser, false);
        try {
            tv_bottomsheet_itemCount = (TextView) activity.findViewById(R.id.bottomsheet_text_itemCount);
            tv_bottomsheet_price = (TextView) activity.findViewById(R.id.bottomsheet_text_price);
        } catch (Exception e){e.printStackTrace();}

        tvDeliveryPrice = activity.findViewById(R.id.tvDeliveryFeePrice);
        tvGrandTotal = activity.findViewById(R.id.tvGrandTotal);
        tvAddress = activity.findViewById(R.id.tvAddress);

        tv_taxPrice = (TextView) activity.findViewById(R.id.tv_stateTaxprice);

        tvTotalPrice = activity.findViewById(R.id.tvTotal);
        tvSavedCoupon = activity.findViewById(R.id.tvSavedPrice);

        btnPlaceOrder = activity.findViewById(R.id.btnPlaceOrder);
        btnAddVoucher = activity.findViewById(R.id.btnAddVoucher);
        tvLabelSaved = activity.findViewById(R.id.tvLabelSaved);
        rv_Cart = (RecyclerView) activity.findViewById(R.id.rvCart);
//        rv_Cart.setNestedScrollingEnabled(false);

        btnChangeOrEditAddress = activity.findViewById(R.id.btnChangeOrEditAddress);
        btnAddAddress = activity.findViewById(R.id.btnAddAddress);

        groupAddressDetails = activity.findViewById(R.id.groupAddressDetails);
        groupNoAddress = activity.findViewById(R.id.groupNoAddress);
        groupCoupon = activity.findViewById(R.id.groupCoupon);

        ivRemoveCoupon = activity.findViewById(R.id.ivRemoveCoupon);

        rgDeliveryType = activity.findViewById(R.id.rgDeliveryType);

        rv_Cart.setHasFixedSize(true);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(activity.getApplicationContext());
        // use a linear layout manager
        rv_Cart.setLayoutManager(layoutManager);

//        rv_Cart.setItemAnimator(new DefaultItemAnimator());
//        rv_Cart.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));


//        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
//        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_Cart);


//        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT /*| ItemTouchHelper.RIGHT | ItemTouchHelper.UP*/) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                // Row is swiped from recycler view
//                // remove it from adapter
//            }
//
//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//        };
//
//        // attaching the touch helper to recycler view
//        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(rv_Cart);

        //coupon popupwindow view
//        tv_couponsNotAvailable = (TextView) activity.findViewById(R.id.tv_noCoupon_available);
//
//        rl_couponPopoup = (RelativeLayout) activity.findViewById(R.id.rl_coupon_popup_main);
//        btn_close_popup = (Button) activity.findViewById(R.id.btn_close_popup);


    }


//    @Override
//    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction, int position) {
//        if (viewHolder instanceof BasketListAdapter.BasketViewHolder) {
//            // get the removed item name to display it in snack bar
//            String name = cartList.get(viewHolder.getAdapterPosition()).name;
//
//            // backup of removed item for undo purpose
//            final Products deletedItem = cartList.get(viewHolder.getAdapterPosition());
//            final int deletedIndex = viewHolder.getAdapterPosition();
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    // remove the item from recycler view
//                    basketListAdapter.removeItem(viewHolder.getAdapterPosition());
//                }
//            }, 2000);
//
//
//            // showing snack bar with Undo option
//            Snackbar snackbar = Snackbar
//                    .make(_activity.findViewById(R.id.layout_basket_rl1), name + " removed from basket!", Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    // undo is selected, restore the deleted item
//                    basketListAdapter.restoreItem(deletedItem, deletedIndex);
//                }
//            });
//            snackbar.setActionTextColor(Color.YELLOW);
//            snackbar.show();
//        }
//    }

    public void initializeDatas(boolean isCouonAdding) {
        if (pref_cart!=null){
            currency = pref_cart.getString(Constants.currency,"");
        }

        CardView cvAddress = _activity.findViewById(R.id.cvAddress);

        if (isSkippedUser) {
            if (cvAddress != null){
                cvAddress.setVisibility(View.GONE);
            }
            groupNoAddress.setVisibility(View.GONE);
            groupAddressDetails.setVisibility(View.GONE);
        } else {
            if (cvAddress != null){
                cvAddress.setVisibility(View.VISIBLE);
            }

            if (pref_login.getBoolean(Constants.isAddressAdded, false)) {
                groupAddressDetails.setVisibility(View.VISIBLE);
                groupNoAddress.setVisibility(View.GONE);
            } else {
                groupAddressDetails.setVisibility(View.GONE);
                groupNoAddress.setVisibility(View.VISIBLE);
            }
        }

        if (cartList==null || cartList.size()<1){
            _activity.findViewById(R.id.emptyBasketScreen).setVisibility(View.VISIBLE);
        } else {
            _activity.findViewById(R.id.emptyBasketScreen).setVisibility(View.GONE);
        }

//        if (isCouonAdding){
//            subTotal = pref_cart.getString(Constants.subTotal, "0.00");
//        } else{
            try {
                if (jsonObject!=null) {
                    if (jsonObject.has("subTotal")) {
                        subTotal = jsonObject.optString("subTotal");
                    }
                    if (jsonObject.has("deliveryCharge")) {
                        delivery_fee = jsonObject.optString("deliveryCharge");
                    }
                    if (jsonObject.has("grandTotal")) {
                        grand_total = jsonObject.optString("grandTotal");
                    }
                }

            } catch (Exception e) {
                Log.e(Constants.APP_TAG, "BasketHelp excep1 : " + e.toString());
            }

            editor_cart.putString(Constants.subTotal, subTotal);
            editor_cart.putString(Constants.cartCount, cartList.size() + "");
            editor_cart.commit();
//        }

        double discount_amount = 0;


        if (pref_cart.getBoolean(Constants.isCouponAdded, false)) {


            if (Double.parseDouble(pref_cart.getString(Constants.couponMinimum, "0.00")) < Double.parseDouble(pref_cart.getString(Constants.subTotal, "0.00"))) {

                Log.e(Constants.APP_TAG,"couponMin:"+pref_cart.getString(Constants.couponMinimum, "0.00")+
                        " - subtotal:"+pref_cart.getString(Constants.subTotal, "0.00"));


                tvLabelSaved.setText(tvLabelSaved.getText().toString() + "(" + pref_cart.getString(Constants.couponCode, "") + ")");
                tvLabelSaved.setText("Saved (" + pref_cart.getString(Constants.couponCode, "") + ")");
                groupCoupon.setVisibility(View.VISIBLE);
                try {
                    btnAddVoucher.setText("Change voucher");
                } catch (Exception e){e.printStackTrace();}

                ivRemoveCoupon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteCouponDetalis();
                    }
                });

                try {
                    if (pref_cart.getString(Constants.couponType, "").trim().equalsIgnoreCase("0")) {
//            subTotal =String.valueOf(Integer.parseInt(subTotal)+Integer.parseInt(pref_cart.getString("couponType",null)));
                        discount_amount = (Double.parseDouble(subTotal) * Double.parseDouble(pref_cart.getString(Constants.coupon_perc, "1"))) / 100;
                        grand_total = String.valueOf(Double.parseDouble(grand_total) - discount_amount);

                    } else {
                        discount_amount = Double.parseDouble(pref_cart.getString(Constants.couponRate, ""));
                        grand_total = String.valueOf(Double.parseDouble(grand_total) - discount_amount);

                    }

                    if(Double.parseDouble(grand_total) < 0){
                        grand_total = "0.00";
                    }
                    editor_cart.putString(Constants.grandTotal, grand_total);
                    editor_cart.commit();

                } catch (Exception e) {
                    Log.e(Constants.APP_TAG, "BasketHelp excep2 : " + e.toString());
                }

            } else {
                deleteCouponDetalis();
            }

        } else {
            groupCoupon.setVisibility(View.GONE);
//            tvGrandTotal.setText(grandTotal);
//            tvGrandTotal.setText(pref_cart.getString(Constants.grandTotal, "0.00"));
            editor_cart.putString(Constants.grandTotal, grand_total);
            editor_cart.commit();

            tvGrandTotal.setText(pref_cart.getString(Constants.grandTotal, "0.00"));
        }
        setBottomsheet();

        tvAddress.setText(new AddressHelper(_activity).getAddress());

        // create an Object for Adapter
        basketListAdapter = new BasketListAdapter(_activity, cartList, currency);
        // set the adapter object to the Recyclerview
        rv_Cart.setAdapter(basketListAdapter);


        try {
            alertDialog.dismiss();
        } catch (Exception e) {
        }

        buttonClick();

        if (pref_cart.getString(Constants.subTotal, "0.00").trim().startsWith("0") || Double.parseDouble(grand_total) < 0) {
            grand_total = "0.00";
            if (delivery_fee==null || delivery_fee.equalsIgnoreCase(""))
                delivery_fee = "0.00";
        }

        tvGrandTotal.setText(currency + " " + pref_cart.getString(Constants.grandTotal, "0.00"));
        tvSavedCoupon.setText( "- " + currency+" " + discount_amount + "");
        tvTotalPrice.setText(currency + " " + subTotal + "");
        tvDeliveryPrice.setText(currency + " " + delivery_fee);
    }

    private void deleteCouponDetalis() {
        editor_cart.putBoolean(Constants.isCouponAdded, false);
        editor_cart.remove(Constants.couponCode);
        editor_cart.remove(Constants.couponType);
        editor_cart.remove(Constants.coupon_perc);
        editor_cart.remove(Constants.couponRate);
        editor_cart.remove(Constants.couponId);
        editor_cart.remove(Constants.couponMinimum);
        editor_cart.commit();


        groupCoupon.setVisibility(View.GONE);
        btnAddVoucher.setText("Add voucher");

        initializeDatas(false);

    }

    public void setBottomsheet() {
        if (tv_bottomsheet_itemCount!=null)
            tv_bottomsheet_itemCount.setText(pref_cart.getString(Constants.cartCount, "0"));
        if (tv_bottomsheet_price!=null)
            tv_bottomsheet_price.setText(currency + " " +pref_cart.getString(Constants.grandTotal, "0.00"));
    }

    private void buttonClick() {
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pref_login.getBoolean(Constants.isSkipUser, false)) {
                    Toast.makeText(_activity, "please login", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cartList==null ||cartList.size()<1 ) {
                    Toast.makeText(_activity, "No items are available", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pref_login.getString(Constants.addressId, "").equals("")){
                    Intent intent = new Intent(_activity, CreateAddressActivty.class);
                    intent.putExtra("use", "create");
                    intent.putExtra("from", "basket");
                    _activity.startActivity(intent);
                    Toast.makeText(_activity, "Please add new address", Toast.LENGTH_SHORT).show();
                    return;
                }

                String deliveryType = "";
                switch (rgDeliveryType.getCheckedRadioButtonId()){
                    case R.id.rbDeliveryTypeHome:
                        deliveryType = "0";
                        break;
                    case R.id.rbDeliveryTypePickup:
                        deliveryType = "1";
                        break;
                    default:
                        Toast.makeText(_activity, "Select a delivery mode", Toast.LENGTH_SHORT).show();
                        return;
                }

                placeOrder(deliveryType);
//                Intent intent = new Intent(_activity, OrderHistoryActivty.class);
//                _activity.startActivity(intent);

            }
        });

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_activity, CreateAddressActivty.class);
                intent.putExtra("use", "create");
                intent.putExtra("from", "basket");
                _activity.startActivity(intent);
            }
        });

        btnChangeOrEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(_activity, AddressListActivity.class);
                intent.putExtra("use", "edit");
                intent.putExtra("from", "basket");
                intent.putExtra("address_id", pref_login.getString(Constants.addressId, ""));
                _activity.startActivity(intent);
            }
        });

        btnAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCoupon();

            }
        });
    }

    public static final String FROM_PLACE_ORDER = "FROM_PLACE_ORDER";
    private void placeOrder(String deliveryType) {
        String couponId = "";

        if (pref_cart.getBoolean(Constants.isCouponAdded, false)) {
            couponId = pref_cart.getString(Constants.couponId, "");
        }

        RequestBody body = new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", pref_login.getString(Constants.api_key, ""))
                .add("couponId", couponId)
                .add("addressId", pref_login.getString(Constants.addressId, ""))
                .add("deliveryType",deliveryType)
                .build();
//        new ApiCallServiceTask(_activity).requestApi(ApiResponseBus.RC_PLACE_ORDER, true, body, Constants.SERVER_URL + Constants.confirmOrder_api, "CHECKOUT...");
        new ApiCallServiceTask(_activity).requestApi(new ApiCallRequest(FROM_PLACE_ORDER, Constants.confirmOrder_api, body, true, ApiCallRequest.TRANSPARENT));
        editor_cart.putBoolean(Constants.isCouponAdded, false);
        editor_cart.putString(Constants.couponId, "");
        editor_cart.commit();
    }

    public final static String FROM_GET_VOUCHER = "FROM_GET_VOUCHER";
    private void addCoupon() {
        String apikey = pref_login.getString(Constants.api_key, "");
        RequestBody body = new FormBody.Builder()
                .add("rest_key", Constants.REST_KEY)
                .add("api_key", apikey)
                .build();
//        new ApiCallServiceTask(_activity).requestApi(ApiResponseBus.RC_ADD_VOUCHER, true, body, Constants.SERVER_URL + Constants.getCoupons_api, "CHECKING COUPONS...");
        new ApiCallServiceTask(_activity).requestApi(new ApiCallRequest(FROM_GET_VOUCHER, Constants.getCoupons_api, body, true, ApiCallRequest.TRANSPARENT));
    }

    public void showCouponPopup(List<Coupons> couponsList) {

        if (couponsList!=null && couponsList.size()>0) {
            Log.e(Constants.APP_TAG, "coupon is not empty");
            showDialog(couponsList);
        }


////        rl_couponPopoup.setVisibility(View.VISIBLE);
//
//        if(couponsList.size()==0){
////            tv_couponsNotAvailable.setVisibility(View.VISIBLE);
////            tv_couponsNotAvailable.setText("No Coupon Available");
////            rv_Coupons.setVisibility(View.GONE);
//        }else {
////            tv_couponsNotAvailable.setVisibility(View.GONE);
////            rv_Coupons.setVisibility(View.VISIBLE);
////
////            rv_Coupons.setHasFixedSize(true);
////            final LinearLayoutManager layoutManager
////                    = new LinearLayoutManager(_activity, LinearLayoutManager.VERTICAL, false);
////            // use a linear layout manager
////            rv_Coupons.setLayoutManager(layoutManager);
//
//
//        }

//        btn_close_popup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rl_couponPopoup.setVisibility(View.GONE);
//            }
//        });

    }

    private void showDialog(List<Coupons> couponsList) {
        if (couponsList==null){
            return;
        }

        alertDialog = new AlertDialog.Builder(_activity).create();
        LayoutInflater inflater = _activity.getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.layout_coupon_popup, null);
        alertDialog.setView(convertView, 0, 0, 0, 0);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.setView(convertView);
//        alertDialog.setCancelable(true);
//        alertDialog.setTitle("Choose Coupons");
        RecyclerView rv_Coupons = (RecyclerView) convertView.findViewById(R.id.rv_coupons);

        rv_Coupons.setHasFixedSize(true);
        final LinearLayoutManager layoutManager
                = new LinearLayoutManager(_activity, LinearLayoutManager.VERTICAL, false);
        // use a linear layout manager
        rv_Coupons.setLayoutManager(layoutManager);

        rv_Coupons.setAdapter(new CouponPopupAdapter(_activity, couponsList, BasketHelp.this));
        alertDialog.show();

//        AlertDialog OptionDialog = new AlertDialog.Builder(_activity).create();

        Button btn_close = (Button) convertView.findViewById(R.id.btn_close_popup);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }


    public void placeOrderResponse(Activity activity, JSONObject response) {
        try {
            JSONObject jsonObject = response.optJSONObject("response_text");
            String message = "";
            message = jsonObject.optString("message");
//        if (resp_code.equalsIgnoreCase("200")) {
            if (jsonObject.optString("success").equalsIgnoreCase("1")) {
                Intent intent = new Intent(activity, OrderHistoryActivty.class);
                intent.putExtra("from", "placeOrder");
                activity.startActivity(intent);
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                return;
            } else {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                if (message.equalsIgnoreCase("You've already used this coupon code")){
                    deleteCouponDetalis();
                }
            }

        } catch (Exception e){e.printStackTrace();}
    }

    public void updateCartPopup() {
        final View dialogView = LayoutInflater.from(_activity).inflate(R.layout.dialog_update_cart, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(_activity).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setView(dialogView, 0, 0, 0, 0);
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.findViewById(R.id.btnDialogCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //your business logic
                alertDialog.dismiss();
            }
        });
        dialogView.findViewById(R.id.btnDialogUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

}
