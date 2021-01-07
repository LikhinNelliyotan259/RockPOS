package com.rockchipme.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ItemDetailsActivity;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.interfaces.OnLoadMoreListener;
import com.rockchipme.app.models.Products;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2/9/2017.
 */
public class RecentItemsAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Products> recentlyViewedList = new ArrayList<>();
    private Context context;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    String currency;

    public RecentItemsAdapter(Context context, RecyclerView recyclerView, String currency) {
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
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
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
        return recentlyViewedList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_single_item_horizontal, parent, false);

            vh = new CategoryViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_progressbar_horizontal, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CategoryViewHolder) {

//
//            ((StudentViewHolder) holder).tvName.setText(singleStudent.getName());
//
//            ((StudentViewHolder) holder).tvEmailId.setText(singleStudent.getEmailId());
//
//            ((StudentViewHolder) holder).student= singleStudent;
            try {
                if (recentlyViewedList.get(position).imageList!=null && recentlyViewedList.get(position).imageList.size()>0) {
                    Log.d(Constants.APP_TAG, "recentlyViewedList img " + position + ": "+recentlyViewedList.get(position).imageList.get(0).getImage());
                    Picasso.with(context).load(recentlyViewedList.get(position).imageList.get(0).getImage())
                            .resize(200, 200)
//                            .placeholder(R.drawable.banner)
                            .centerCrop()
                            .into(((CategoryViewHolder) holder).iv);
                } else{

                    Picasso.with(context).load(R.mipmap.ic_launcher)
                            .resize(200, 200)
//                            .placeholder(R.drawable.banner)
                            .centerCrop()
                            .into(((CategoryViewHolder) holder).iv);
                }
            }catch (Exception e){
                ((CategoryViewHolder) holder).iv.setImageResource(R.mipmap.ic_launcher);
                e.printStackTrace();
            }
            ((CategoryViewHolder) holder).tvName.setText(recentlyViewedList.get(position).name);
            ((CategoryViewHolder) holder).tvPrice.setText(currency + " " + recentlyViewedList.get(position).rate);

            ((CategoryViewHolder) holder).iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recentlyViewedList.size()>position) {
                        Intent intent = new Intent(context, ItemDetailsActivity.class);
                        intent.putExtra("pdtId", recentlyViewedList.get(position).pdtId);
                        context.startActivity(intent);
                    }
                }
            });

        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return recentlyViewedList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvPrice;
        //        public TextView tvEmailId;
        public ImageView iv;

        public CategoryViewHolder(View v) {
            super(v);
            iv = (ImageView) v.findViewById(R.id.imageView);
            tvName = (TextView) v.findViewById(R.id.title);
            tvPrice = (TextView) v.findViewById(R.id.tvPrice);
//            tvEmailId = (TextView) v.findViewById(R.id.tvEmailId);
//            v.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(),
//                            "OnClick :" + student.getName() + " \n "+student.getEmailId(),
//                            Toast.LENGTH_SHORT).show();
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

    public void setAdapterData(List<Products> recentlyViewedList, String currency){
        this.currency = currency;
        this.recentlyViewedList = recentlyViewedList;
        notifyDataSetChanged();
    }
}