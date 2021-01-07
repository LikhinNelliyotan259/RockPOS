package com.rockchipme.app.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.OrderDetailsActivity;
import com.rockchipme.app.custom.QuantityPicker;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.models.Orders;
import com.rockchipme.app.models.Products;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Alisons on 9/27/2017.
 */
public class OrderedItemsAdapter extends RecyclerView.Adapter<OrderedItemsAdapter.OrderedItemsViewHolder> {

    private OrderDetailsActivity context;
    private List<Products> orderedItemList = new ArrayList<>();
    private String currency = "";
    private Orders orders;
    private double total = 0;
    public boolean isUpdateQuantityLoading = false;

    public OrderedItemsAdapter(OrderDetailsActivity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public OrderedItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_product_list, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ordered_items, parent, false);
        return new OrderedItemsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedItemsViewHolder holder, int position) {
        final Products data = orderedItemList.get(position);
        holder.tvName.setText(data.name);
        holder.tvUnit.setText(data.unitName);
        holder.tvPrice.setText(currency + " " + data.total);
        holder.quantityPicker.setValue(Integer.parseInt(data.quantity));

        if (orders == null || orders.orderStatus == null || !orders.orderStatus.equalsIgnoreCase("0")) {
            holder.quantityPicker.disableClickEvent();
        }

        holder.quantityPicker.setValueChangedListener(new QuantityPicker.ValueChangedListener() {
            @Override
            public void valueChanged(int value, QuantityPicker.ActionEnum action) {
                if (!isUpdateQuantityLoading) {
                    try {
                        int difference = value - Integer.parseInt(data.quantity);
                        Log.e(Constants.APP_TAG, "difference:" + difference);
                        total += (difference * Double.parseDouble(data.rate));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    checkOrderStaus(data, value + "");
                }
            }
        });

        try {
            if (data.imageList != null && data.imageList.size() > 0) {
                Log.e(Constants.APP_TAG, "img:" + data.imageList.get(0).getImage());
                Picasso.with(context).load(data.imageList.get(0).getImage())
//                        .placeholder(R.drawable.food1)
                        .into(holder.civ);
            } else {
//                Picasso.with(context)
//                        .load(R.drawable.food1)
//                        .into(((OrderedItemsViewHolder) holder).civ);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.notifyModifiersAdapter(data);
    }

    private void checkOrderStaus(final Products product, final String quantity) {
        if (orderedItemList == null)
            return;

        if (orderedItemList.size() <= 1 && quantity.equals("0")) {
            showWarningDialog("Cancel your order", product, quantity, true);
        } else {
            try {
                if (orders.couponsList != null && orders.couponsList.size() > 0 && orders.couponsList.get(0).minimumTotal != null) {
                    Log.e(Constants.APP_TAG, "Coupon added" + " total:" + total + " coupon minTotal:" + orders.couponsList.get(0).minimumTotal);
                    if (total < Double.parseDouble(orders.couponsList.get(0).minimumTotal)) {
                        showWarningDialog("Remove your coupons", product, quantity, false);
                        return;
                    }
                }
                isUpdateQuantityLoading = true;
                context.editOrdersApi(product, quantity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderedItemList.size();
    }

    class OrderedItemsViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvUnit;//, tvItemCount;
        ImageView civ;
        //        FrameLayout flAdd, flRemove;
        QuantityPicker quantityPicker;
        ModifiersAdapter modifiersAdapter;

        OrderedItemsViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.title);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            civ = itemView.findViewById(R.id.civ);
            quantityPicker = itemView.findViewById(R.id.quantityPicker);


            RecyclerView rvBasketModifiers = itemView.findViewById(R.id.rvAddOns);
            modifiersAdapter = new ModifiersAdapter();
            rvBasketModifiers.setAdapter(modifiersAdapter);
        }

        void notifyModifiersAdapter(Products products) {
            if (products.selectedModifiers == null) {
                products.selectedModifiers = new ArrayList<>();
            }
            if (products.selectedForce == null) {
                products.selectedForce = new ArrayList<>();
            }
            modifiersAdapter.setAdapterData(products);
        }
    }


    public void showWarningDialog(String title, final Products product, final String quantity, final boolean isCancelOrder) {
        if (context == null) {
            return;
        }
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText(title)
                .setConfirmText("Continue")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        isUpdateQuantityLoading = true;
                        if (isCancelOrder) {
                            context.cancelOrderApi();
                        } else {
                            context.editOrdersApi(product, quantity);
                        }
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        setAdapterData(orders, currency);
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public void setAdapterData(Orders orders, String currency) {
        this.currency = currency;
        if (orders == null || orders.products == null) {
            this.orderedItemList = new ArrayList<>();
        } else {
            this.orders = orders;
            this.orderedItemList = orders.products;
            try {
                this.total = Double.parseDouble(orders.total);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    public void enableQuantityUpdate() {
        isUpdateQuantityLoading = false;
    }


    private class ModifiersAdapter extends RecyclerView.Adapter<ModifiersAdapter.ViewHolder> {
        private List<Products> addOns = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_addons_orders, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            Products data = addOns.get(i);
            holder.tvName.setText(data.name);
            holder.tvPrice.setText(currency + " " + data.total);

            if (i == getItemCount()-1){
                holder.underLine.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return addOns.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvName, tvPrice;
            View view, underLine;

            public ViewHolder(View v) {
                super(v);
                view = v;
                tvName = v.findViewById(R.id.tvName);
                underLine = v.findViewById(R.id.view);
                tvPrice = v.findViewById(R.id.tvPrice);
            }
        }

        public void setAdapterData(Products product) {
            this.addOns = product.selectedModifiers;
            this.addOns.addAll(product.selectedForce);
            notifyDataSetChanged();
        }
    }
}
