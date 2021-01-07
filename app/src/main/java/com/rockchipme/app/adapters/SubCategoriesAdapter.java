package com.rockchipme.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ProductListActivity;
import com.rockchipme.app.helpers.Utils;
import com.rockchipme.app.models.Categories;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2/9/2017.
 */
public class SubCategoriesAdapter extends RecyclerView.Adapter <SubCategoriesAdapter.CategoryViewHolder> {

    private List<Categories> subCategories = new ArrayList<>();
    private final Context context;

    public SubCategoriesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_subcategories, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {
        final Categories data = subCategories.get(position);

        if (position == 0) {
            Utils.setMargins(holder.view, 10, 0, 0, 0);
        } else if (position == getItemCount()-1){
            Utils.setMargins(holder.view, 0, 0, 10, 0);
        } else {
            Utils.setMargins(holder.view, 5, 0, 5, 0);
        }

        holder.tvName.setText(data.name);
        Picasso.with(context).load(data.getImage())
                .placeholder(R.drawable.no_image)
                .into(holder.iv);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductListActivity.class);
                intent.putExtra("catId", data.catId);
                intent.putExtra("itemsCount", data.pdtCount);
                intent.putExtra("catName", data.name);
                intent.putExtra("catImg", data.getImage());
                context.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return subCategories.size();
    }

    //
    class CategoryViewHolder extends RecyclerView.ViewHolder {

        final TextView tvName;
        final ImageView iv;
        final View view;

        CategoryViewHolder(View v) {
            super(v);
            view = v;
            iv = v.findViewById(R.id.civ);
            tvName = v.findViewById(R.id.tvName);
        }
    }

    public void setAdapterData(List<Categories> subCategories) {
        this.subCategories = subCategories;
        notifyDataSetChanged();
    }

}