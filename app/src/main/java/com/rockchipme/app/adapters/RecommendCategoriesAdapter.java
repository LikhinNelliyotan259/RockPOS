package com.rockchipme.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ItemDetailsActivity;
import com.rockchipme.app.interfaces.OnLoadMoreListener;
import com.rockchipme.app.models.Products;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2/9/2017.
 */
public class RecommendCategoriesAdapter extends RecyclerView.Adapter {

    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();

    public static int CURRENT_POSITION = 0;


    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Products> productsList = new ArrayList<>();
    private Context context;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean lockedAnimations = false;
    private int lastAnimatedItem = -1;

    String currency;

    public RecommendCategoriesAdapter(Context context, RecyclerView recyclerView, String currency) {
        this.context = context;
        this.currency = currency;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return productsList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_single_category_horizontal, parent, false);
            vh = new CategoryViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_progressbar_horizontal, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {
        if (holder instanceof CategoryViewHolder) {
            CURRENT_POSITION = position;
            ((CategoryViewHolder) holder).tvName.setText(productsList.get(position).name);
            ((CategoryViewHolder) holder).tvPrice.setText(currency + " " + productsList.get(position).rate);

            try {
//                bindPhoto(((CategoryViewHolder) holder), position, productsList.get(position).imageList.get(0).getImage());
               if (productsList.get(position).imageList != null && productsList.get(position).imageList.size() > 0) {
                   Picasso.with(context)
                           .load(productsList.get(position).imageList.get(0).getImage())
                           .resize(200, 200)
                           .centerCrop()
                           .placeholder(R.drawable.no_image)
                           .into(((CategoryViewHolder) holder).iv);
               } else {
                   ((CategoryViewHolder)holder).iv.setImageResource(R.mipmap.ic_launcher);
               }
            } catch (Exception e) {

                e.printStackTrace();
            }

            ((CategoryViewHolder)holder).view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productsList !=null && productsList.size()>position) {
                        Intent intent = new Intent(context, ItemDetailsActivity.class);
                        intent.putExtra("pdtId", productsList.get(position).pdtId);
                        context.startActivity(intent);
                    }
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    private void bindPhoto(final CategoryViewHolder holder, int position, String image) throws Exception {
        try {
            Picasso.with(holder.iv.getContext())
                    .load(image)
                    .resize(200, 200)
                    .centerCrop()
//                .placeholder(R.drawable.burgur)
                    .into(holder.iv, new Callback() {
                        @Override
                        public void onSuccess() {
                            animatePhoto(holder);
                        }

                        @Override
                        public void onError() {

                        }
                    });
            if (lastAnimatedItem < position) lastAnimatedItem = position;
        } catch (Exception e){e.printStackTrace();}
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

    private void animatePhoto(final CategoryViewHolder holder) {
        if (!lockedAnimations) {
            if (lastAnimatedItem == holder.getPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = PHOTO_ANIMATION_DELAY + holder.getPosition() * 30;

            holder.iv.setScaleY(0);
            holder.iv.setScaleX(0);

            holder.iv.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    //
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvPrice;
//        public TextView tvEmailId;
        public ImageView iv;
        View view;

        public CategoryViewHolder(View v) {
            super(v);
            view = v;
            iv = (ImageView) v.findViewById(R.id.imageView);
            tvName = (TextView) v.findViewById(R.id.title);
            tvPrice = (TextView) v.findViewById(R.id.tvPrice);
//
//            tvEmailId = (TextView) v.findViewById(R.id.tvEmailId);
//
//            v.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(),
//                            "OnClick :" + student.getName() + " \n "+student.getEmailId(),
//                            Toast.LENGTH_SHORT).show();
//
//                }
//            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    public void setAdapterData(List<Products> recomendedCategoryList, String currency){
        this.currency = currency;
        this.productsList = recomendedCategoryList;
        notifyDataSetChanged();
    }

}