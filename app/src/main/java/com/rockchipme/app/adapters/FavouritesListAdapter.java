package com.rockchipme.app.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.FavouritesListActivity;
import com.rockchipme.app.activities.ItemDetailsActivity;
import com.rockchipme.app.helpers.BasketHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.models.Products;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sibin on 10/20/2017.
 */

public class FavouritesListAdapter extends RecyclerView.Adapter<FavouritesListAdapter.ProductViewHolder> {
    private List<Products> favouritesList = new ArrayList<>();
    private FavouritesListActivity context;
    private String currency;

    public FavouritesListAdapter(FavouritesListActivity context, String currency) {
        this.context = context;
        this.currency = currency;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_favourite_list_new, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products data = favouritesList.get(position);
        holder.tvTitle.setText(data.name);
        holder.tvPrice.setText(String.format("%s %s", currency, data.rate));
//        holder.tvPrice.setText(currency + " "+data.rate);

        if (data.imageList != null && data.imageList.size() > 0) {
            Log.d(Constants.APP_TAG, "fav img:"+data.imageList.get(0).getImage());
            Picasso.with(context)
                    .load(data.imageList.get(0).getImage())
                    .placeholder(R.drawable.no_image)
                    .into(holder.ivProduct);
        } else {
            Log.d(Constants.APP_TAG, "fav img: empty");
            holder.ivProduct.setImageResource(R.mipmap.ic_launcher);
        }

//        ((ProductViewHolder)holder).
        holder.tvRemove.setOnClickListener(v -> {
            if (favouritesList.size() > position) {
                context.removeFavouriteApi(data.pdtId);
                favouritesList.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.tvMoveToCart.setOnClickListener(v -> {
            if (favouritesList.size() > position) {
                new BasketHelper(context).updateCart(1, data, "FavouritesListAdapter");
                favouritesList.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.view.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("pdtId", data.pdtId);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice;
        Button tvMoveToCart, tvRemove;
        ImageView ivProduct;
        View view;

        ProductViewHolder(View view) {
            super(view);
            this.view = view;
            tvTitle = view.findViewById(R.id.tvItemName);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvMoveToCart = view.findViewById(R.id.btnMoveToCart);
            tvRemove = view.findViewById(R.id.btnRemove);
            ivProduct = view.findViewById(R.id.civ);
        }
    }

    public List<Products> getFavouriteList() {
        if (favouritesList == null) {
            favouritesList = new ArrayList<>();
        }
        return favouritesList;
    }

    public void setAdapterData(List<Products> productsList) {
        this.favouritesList = productsList;
        notifyDataSetChanged();
    }
}