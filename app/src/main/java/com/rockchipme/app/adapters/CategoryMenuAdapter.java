package com.rockchipme.app.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ProductListActivity;
import com.rockchipme.app.models.Categories;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2/9/2017.
 */
public class CategoryMenuAdapter extends RecyclerView.Adapter <CategoryMenuAdapter.CategoryViewHolder> {

    private final List<Categories> categoriesList;
    private final Dialog dialog;
    private final Context context;

    public CategoryMenuAdapter(Context context, List<Categories> categoriesList, Dialog dialog) {
        this.context = context;
        if (categoriesList == null){
            categoriesList = new ArrayList<>();
        }
        this.categoriesList = categoriesList;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_category_menu, parent, false);
       return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        final Categories data = categoriesList.get(position);
        holder.tvName.setText(data.name.toUpperCase());
        holder.tvItemCount.setText(data.pdtCount);
        holder.view.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(context, ProductListActivity.class);
                intent.putExtra("catId", data.catId);
                intent.putExtra("itemsCount", data.pdtCount);
                intent.putExtra("catName", data.name);
                intent.putExtra("catImg", data.getImage());
                context.startActivity(intent);
                if (dialog != null) {
                    dialog.dismiss();
                }
            } catch (Exception e){e.getMessage();}
        });

    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvItemCount;
        final View view;

        CategoryViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.title);
            tvItemCount = v.findViewById(R.id.tv_item_count);
            view = v;

        }
    }
}