package com.rockchipme.app.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.custom.QuantityPicker;
import com.rockchipme.app.helpers.BasketHelper;
import com.rockchipme.app.models.Products;

import java.util.List;

/**
 * Created by Alisons on 9/14/2017.
 */
public class BasketListAdapter extends RecyclerView.Adapter<BasketListAdapter.BasketViewHolder> {

    private List<Products> list;
    Context context;
    private String currency;
    private BasketHelper basketHelper;

    public BasketListAdapter(Context context, List<Products> list, String currency) {
        this.list = list;
        this.currency = currency;
        this.context = context;
        basketHelper = new BasketHelper(context);
    }

//    @Override
//    public int getItemViewType(int position) {
//        return list.get(position) != null ? VIEW_ITEM : VIEW_PROG;
//    }

    @Override
    public BasketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_basket_item, parent, false);

        return new BasketViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BasketViewHolder holder, int i) {
        final Products data = list.get(i);
        holder.tvName.setText(data.name.toUpperCase());
        holder.tvPrice.setText(currency + " " + data.total);

        holder.tvSelectedUnit.setText(data.unitName);

        holder.quantityPicker.setValue(Integer.parseInt(data.quantity));
        holder.quantityPicker.setValueChangedListener((value, action) -> {
                    basketHelper.updateCart(value, data, "BasketListAdapter");
                }
        );

        holder.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basketHelper.updateCart(0, data, "BasketListAdapter");
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basketHelper.updateCart(0, data, "BasketListAdapter");
            }
        });
        
        String selectedAddOns = selectedAddOns(data);
        if (selectedAddOns.trim().length() > 0){
            holder.tvSelectedAddons.setText(selectedAddOns);
            holder.groupAddOns.setVisibility(View.VISIBLE);
        } else {
            holder.groupAddOns.setVisibility(View.GONE);
        }
        if (data.remarks != null && data.remarks.trim().length() > 0){
            holder.tvRemarks.setText(data.remarks);
            holder.groupRemarks.setVisibility(View.VISIBLE);
        } else {
            holder.groupRemarks.setVisibility(View.GONE);
        }

    }
    
    
    private String selectedAddOns(Products products){
        String comma = "";
        StringBuilder addOns = new StringBuilder();
        for (Products product : products.selectedModifiers){
            addOns.append(comma).append(product.name);
            comma = ",";
        }
        for (Products product : products.selectedForce){
            addOns.append(comma).append(product.name);
            comma = ",";
        }
        return addOns.toString();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    class BasketViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvSelectedUnit, tvSelectedAddons, tvRemarks;
        QuantityPicker quantityPicker;
        LinearLayout llDelete;
        ImageView ivDelete;
        View view;
        Group groupAddOns, groupRemarks;
        
        BasketViewHolder(View v) {
            super(v);
            view = v;
            tvName = v.findViewById(R.id.title);
            tvPrice = v.findViewById(R.id.tvPrice);

            quantityPicker = v.findViewById(R.id.quantityPicker);
            llDelete = v.findViewById(R.id.llDelete);
            ivDelete = v.findViewById(R.id.ivDelete);
            
            tvSelectedUnit = v.findViewById(R.id.tvUnit);
            tvSelectedAddons = v.findViewById(R.id.tvAddons);
            tvRemarks = v.findViewById(R.id.tvRemarks);
            groupAddOns = v.findViewById(R.id.groupAddOns);
            groupRemarks = v.findViewById(R.id.groupRemarks);
        }
    }

    public void removeItem(int position) {
        list.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public void restoreItem(Products item, int position) {
        list.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
        notifyItemRangeChanged(position, list.size());
    }

}
