package com.rockchipme.app.custom;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.rockchipme.app.helpers.Constants;

import java.util.List;

/**
 * Created by Sibin on 10/27/2017.
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private LinearLayoutManager mLinearLayoutManager;
    private List<Object> list;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager/*, List<Object> list*/) {
        this.mLinearLayoutManager = linearLayoutManager;
//        this.list = list;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        Log.d(Constants.APP_TAG,"totalItemCount:"+totalItemCount+" previousTotal"+previousTotal+
                " visibleItemCount:"+visibleItemCount+" firstVisibleItem"+firstVisibleItem);

        if (loading) {
            if (totalItemCount > previousTotal+1) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) < (firstVisibleItem + visibleThreshold)) {
            // End has been reached
            // Do something
            onLoadMore(totalItemCount+1);
            loading = true;
            Log.d(Constants.APP_TAG,"recycleView scrolling");
        }


//        if (!loading && )

    }

    public abstract void onLoadMore(int current_page);
}