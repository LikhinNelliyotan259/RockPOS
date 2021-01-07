package com.rockchipme.app.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.helpers.BasketHelp;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.models.Coupons;

import java.util.List;

/**
 * Created by Alisons on 9/15/2017.
 */


public class CouponPopupAdapter extends RecyclerView.Adapter {

    Context context;
    List<Coupons> couponsList;
    SharedPreferences pref_cart;
    SharedPreferences.Editor editor_cart;
    BasketHelp basketHelp;

    public CouponPopupAdapter(Context context, List<Coupons> cartList, BasketHelp basketHelp) {
        this.basketHelp = basketHelp;
        this.context = context;
        this.couponsList = cartList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;

        pref_cart = context.getSharedPreferences(Constants.PREF_CART,context.MODE_PRIVATE);
        editor_cart = pref_cart.edit();

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_coupon_poup, parent, false);

        vh = new CouponviewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        Log.e(Constants.APP_TAG,"sub total:"+pref_cart.getString(Constants.subTotal,"0.00")+" min: "+couponsList.get(position).minimumTotal);

//        if (Double.parseDouble(couponsList.get(position).minimumTotal.trim())>Double.parseDouble(pref_cart.getString(Constants.subTotal,null))){
//            ((CouponviewHolder)holder).ln1_rowOfCoupon.setBackgroundColor(Color.GRAY);
//            ((CouponviewHolder)holder).ln2_rowOfCoupon.setBackgroundColor(Color.GRAY);
//            ((CouponviewHolder)holder).btn_apply.setEnabled(false);
//        }else {


            ((CouponviewHolder) holder).tv_couponTitle.setText(couponsList.get(position).couponCode);
            if (couponsList.get(position).discountType.equalsIgnoreCase("1")) {
                ((CouponviewHolder) holder).tv_couponOFF.setText("FLAT " + couponsList.get(position).discountRate + " OFF");
            } else {
                ((CouponviewHolder) holder).tv_couponOFF.setText(couponsList.get(position).discountPerc + " % OFF");
            }

//        ((CouponviewHolder)holder).view.setBackgroundColor(Color.GRAY);


            ((CouponviewHolder) holder).btn_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    double subTotal = Double.parseDouble(pref_cart.getString(Constants.subTotal, "0.00"));
                    double discount_amount = 0.0;
                    if (Double.parseDouble(couponsList.get(position).minimumTotal.trim()) <= subTotal) {

                        try{
                            if (couponsList.get(position).discountType.equalsIgnoreCase("1")){
                                discount_amount = Double.parseDouble(couponsList.get(position).discountRate);
                            }else {
                                discount_amount = (subTotal * Double.parseDouble(couponsList.get(position).discountPerc)) / 100;
                            }
                        } catch (Exception e){e.printStackTrace();}

                        if (discount_amount >= subTotal){
                            Toast.makeText(context, "You're not eligible", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        editor_cart.putBoolean(Constants.isCouponAdded, true);
                        editor_cart.putString(Constants.couponCode, couponsList.get(position).couponCode);
                        editor_cart.putString(Constants.couponType, couponsList.get(position).discountType);
                        editor_cart.putString(Constants.coupon_perc, couponsList.get(position).discountPerc);
                        editor_cart.putString(Constants.couponRate, couponsList.get(position).discountRate);
                        editor_cart.putString(Constants.couponId, couponsList.get(position).couponId);
                        editor_cart.putString(Constants.couponMinimum,couponsList.get(position).minimumTotal);
                        editor_cart.commit();

                        basketHelp.initializeDatas(true);
                    }
                    else {
                        Toast.makeText(context, "You're not eligible", Toast.LENGTH_SHORT).show();
                    }
                }
            });



    }

    @Override
    public int getItemCount() {
        return couponsList.size();
    }

    class CouponviewHolder extends RecyclerView.ViewHolder{

        TextView tv_couponTitle,tv_couponOFF;
        Button btn_apply;
        View view;
        LinearLayout ln1_rowOfCoupon,ln2_rowOfCoupon;

        public CouponviewHolder(View v) {
            super(v);
            view = v.getRootView();
            tv_couponTitle = (TextView) v.findViewById(R.id.tv_couponTitle);
            tv_couponOFF = (TextView) v.findViewById(R.id.tv_couponOff);
            btn_apply = (Button) v.findViewById(R.id.btn_couponApply);

            ln1_rowOfCoupon = (LinearLayout) v.findViewById(R.id.ln1_row_couponPopup);
            ln2_rowOfCoupon = (LinearLayout) v.findViewById(R.id.ln2_row_couponPopup);

        }
    }


}
