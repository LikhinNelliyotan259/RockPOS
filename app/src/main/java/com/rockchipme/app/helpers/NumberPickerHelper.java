package com.rockchipme.app.helpers;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rockchipme.app.R;

/**
 * Created by Alisons on 4/23/2018.
 */

public class NumberPickerHelper {
    Activity activity;

    FrameLayout flAdd,flRemove;
    TextView tvCount;

    public NumberPickerHelper(Activity context) {
        this.activity = context;
        initializeView();
    }

    private void initializeView() {
        flAdd = activity.findViewById(R.id.flAdd);
        flRemove = activity.findViewById(R.id.flRemove);
        tvCount = activity.findViewById(R.id.tv_items_cartCount);

        flAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        flRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void setCartCount(String cartCount){
        if (tvCount!=null){
            tvCount.setText(cartCount);
        }
    }

    interface NumberPickerClickEvent{
        public void NumberPickerOnClick(int count);
    }

}
