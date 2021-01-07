package com.rockchipme.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ItemDetailsActivity;
import com.rockchipme.app.custom.QuantityPicker;
import com.rockchipme.app.helpers.ActivityRequestCode;
import com.rockchipme.app.helpers.BasketHelper;
import com.rockchipme.app.models.Products;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2/9/2017.
 */
public class ItemsListAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Products> list = new ArrayList<>();

    private BasketHelper basketHelper;
    private Context context;
    private String currency;

    public ItemsListAdapter(Context context, BasketHelper basketHelper, String currency) {
        this.context = context;
        this.currency = currency;
        this.basketHelper = basketHelper;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
//                    R.layout.row_item_list, parent, false);
                    R.layout.row_product_list, parent, false);

            vh = new ProductsViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_progressbar_vertical, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductsViewHolder) {
            final Products data = list.get(position);

            try {
                String price = "";
                if (data.units != null && data.units.size() > 0) {
                    if (data.units.size() == 1) {
                        price = data.units.get(0).price;
                    } else {
                        for (int i = 0; i < data.units.size(); i++) {
                            if (data.units.get(i).primaryUnit.equals("1")) {
                                price = data.units.get(i).price;
                                break;
                            }
                        }
                        if (price.trim().length() < 1) {
                            price = data.units.get(0).price;
                        }
                    }
                } else {
                    price = data.rate;
                }
                ((ProductsViewHolder) holder).tv_price.setText(currency + " " + price);

            } catch (Exception e) {
                ((ProductsViewHolder) holder).tv_price.setText(currency + " " + data.rate);
                e.printStackTrace();
            }

//            if (((data.modifiers != null && data.modifiers.size() > 0) ||
//                    (data.force != null && data.force.size() > 0) ||
//                    (data.units != null && data.units.size() > 1))) {
//                ((ProductsViewHolder) holder).quantityPicker.isWantToWaitChangeValue = false;
//            } else {
//                ((ProductsViewHolder) holder).quantityPicker.isWantToWaitChangeValue = true;
//            }

            ((ProductsViewHolder) holder).tvName.setText(data.name);
//            ((ProductsViewHolder) holder).tvItemCount.setText(data.cartCount);
            ((ProductsViewHolder) holder).quantityPicker.setValue(Integer.parseInt(data.cartCount));

            if (data.imageList != null && data.imageList.size() > 0) {
                Picasso.with(context).load(data.imageList.get(0).getImage())
//                        .placeholder(R.drawable.food1)
                        .into(((ProductsViewHolder) holder).civ);
//                Log.e(Constants.APP_TAG, "produc image url: " + data.imageList.get(0).getImage());
            } else {
//                ((ProductsViewHolder) holder).civ.setImageResource(R.drawable.banner);
//                Picasso.with(context)
//                        .load(R.drawable.food1)
//                        .into(((ProductsViewHolder) holder).civ);
            }


            ((ProductsViewHolder) holder).quantityPicker.setValueChangedListener((value, action) -> {
//                if (((data.modifiers != null && data.modifiers.size() > 0) ||
//                        (data.force != null && data.force.size() > 0) ||
//                        (data.units != null && data.units.size() > 1))
////                        && (data.cartCount == null || data.cartCount.trim().equals("0"))
//                ) {
                    Intent intent = new Intent(context, ItemDetailsActivity.class);
                    intent.putExtra("product", data);
                    intent.putExtra("pdtId", data.pdtId);
                    intent.putExtra("cartCount", value);
                    intent.putExtra("qty", value);
                    context.startActivity(intent);
//                } else {
//                    basketHelper.updateCart(value, data, "ItemsListAdapter");
//                }
            });

            ((ProductsViewHolder) holder).view.setOnClickListener(v -> {
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("pdtId", data.pdtId);
                ((Activity) context).startActivityForResult(intent, ActivityRequestCode.RC_PRODUCT_DETAILS_CART_COUNT_CHANGED);
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private void bindPhoto(final ProductsViewHolder holder, int position, String image) {
        Picasso.with(holder.civ.getContext())
                .load(image)
                .resize(200, 200)
                .placeholder(R.drawable.no_image)
                .centerCrop()
                .into(holder.civ);
    }

    //
    public class ProductsViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tv_price;
        final View view;
        final ImageView civ;
        final QuantityPicker quantityPicker;

        ProductsViewHolder(View v) {
            super(v);
            civ = v.findViewById(R.id.civ);
            view = v;
            tvName = v.findViewById(R.id.title);
            tv_price = v.findViewById(R.id.tvPrice);
            quantityPicker = v.findViewById(R.id.quantityPicker);
            quantityPicker.isWantToWaitChangeValue = false;
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    public void setAdapterData(List<Products> productsList) {
        this.list = productsList;
        notifyDataSetChanged();
    }
}