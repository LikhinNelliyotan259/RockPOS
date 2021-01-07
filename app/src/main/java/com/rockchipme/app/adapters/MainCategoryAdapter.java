package com.rockchipme.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ProductListActivity;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.models.Categories;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2/9/2017.
 */
public class MainCategoryAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;

    private List<Categories> categoriesList = new ArrayList<>();
    Context context;

    public MainCategoryAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        int VIEW_PROG = 0;
        return categoriesList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_single_category_vertical, parent, false);

            vh = new CategoryViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_progressbar_vertical, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryViewHolder) {
            final Categories data = categoriesList.get(position);
            Log.d(Constants.APP_TAG, "cat img:"+data.getImage());
                Picasso.with(context).load(data.getImage())
//                        .placeholder(R.drawable.z_banner)
                        .placeholder(R.drawable.no_image)
                        .into(((CategoryViewHolder) holder).iv);
//            Glide.with(context).load(Constants.categoriesImageBaseUrl+list.get(position).image).placeholder(R.drawable.banner).into(((CategoryViewHolder) holder).iv);
                ((CategoryViewHolder) holder).tvName.setText(data.name.toUpperCase());
                ((CategoryViewHolder) holder).tvItemCount.setText(data.pdtCount);

                ((CategoryViewHolder) holder).view.setOnClickListener(new View.OnClickListener() {
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

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName, tvItemCount;
        final ImageView iv;
        final View view;

        CategoryViewHolder(View v) {
            super(v);
            iv = v.findViewById(R.id.imageView);
            tvName = v.findViewById(R.id.title);
            tvItemCount = v.findViewById(R.id.tv_item_count);
            view = v;

        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        final ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    public void setAdapterList(List<Categories> categoriesList){
        this.categoriesList = categoriesList;
        notifyDataSetChanged();
    }
}