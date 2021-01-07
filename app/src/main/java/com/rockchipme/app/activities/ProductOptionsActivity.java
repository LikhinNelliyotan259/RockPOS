package com.rockchipme.app.activities;

import android.os.Bundle;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.rockchipme.app.R;
import com.rockchipme.app.adapters.ForcedQuestionAdapter;
import com.rockchipme.app.adapters.ModifiersAdapter;
import com.rockchipme.app.adapters.UnitsAdapter;
import com.rockchipme.app.helpers.BasketHelper;
import com.rockchipme.app.helpers.EventBusResponse;
import com.rockchipme.app.models.Products;
import com.rockchipme.app.network.ApiCallResponse;
import com.rockchipme.app.network.ApiCallServiceTask;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class ProductOptionsActivity extends BaseActivity implements ApiCallServiceTask.onApiFinish {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_product_options;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    ForcedQuestionAdapter forcedQuestionAdapter;
    UnitsAdapter unitsAdapter;
    ModifiersAdapter modifiersAdapter;
    Button btnAdd;

    double totalPrice = 0;
    int cartCount = 0;

    private void initViews() {
        Products products = (Products) getIntent().getSerializableExtra("product");
        cartCount = getIntent().getIntExtra("cartCount", 1);
        if (products != null) {
            try {
                totalPrice = Double.parseDouble(products.rate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RecyclerView rvForceQuestion = findViewById(R.id.rvForceQuestion);
            RecyclerView rvUnit = findViewById(R.id.rvUnit);
            RecyclerView rvModifiers = findViewById(R.id.rvModifiers);

            Group groupModifiers = findViewById(R.id.groupModifiers);
            Group groupUnit = findViewById(R.id.groupUnit);
            Group groupForceQuestion = findViewById(R.id.groupForceQuestion);

            btnAdd = findViewById(R.id.btnAddToCart);

            if (products.force == null || products.force.size() < 1) {
                groupForceQuestion.setVisibility(View.GONE);
//            } else {
//                Log.d(Constants.APP_TAG, "force size:"+products.force.size());
            }

            if (products.units == null || products.units.size() < 1) {
                groupUnit.setVisibility(View.GONE);
//            } else {
//                Log.d(Constants.APP_TAG, "units size:"+products.units.size());
            }

            if (products.modifiers == null || products.modifiers.size() < 1) {
                groupModifiers.setVisibility(View.GONE);
//            } else {
//                Log.d(Constants.APP_TAG, "modifiers size:"+products.modifiers.size());
            }

            forcedQuestionAdapter = new ForcedQuestionAdapter(this, products.force);
            rvForceQuestion.setAdapter(forcedQuestionAdapter);

            unitsAdapter = new UnitsAdapter(this, products.units);
            rvUnit.setAdapter(unitsAdapter);

            modifiersAdapter = new ModifiersAdapter(this, products.modifiers);
            rvModifiers.setAdapter(modifiersAdapter);

            setTotalPrice();

            btnAdd.setOnClickListener(v -> addToCart(products));
        }
    }

    public void setTotalPrice() {
        if (unitsAdapter.selectedUnitId != null) {
            totalPrice = Double.parseDouble(unitsAdapter.selectedUnitId.price);
        }

        if (forcedQuestionAdapter.selectedIds != null){
            totalPrice += Double.parseDouble(forcedQuestionAdapter.selectedIds.rate);
        }

        for (Products selectedModifiers : modifiersAdapter.selectedIds) {
            totalPrice += Double.parseDouble(selectedModifiers.rate);
        }

        totalPrice *= cartCount;

        btnAdd.setText("Add "+totalPrice);


//        if (forcedQuestionAdapter.getItemCount() > 0) {
//            if (forcedQuestionAdapter.selectedIds.size() != forcedQuestionAdapter.getItemCount()) {
//                return;
//            } else {
//                options.selectedForceQuestions = forcedQuestionAdapter.selectedIds;
//            }
//        }
    }

    private void addToCart(Products products) {
        if (forcedQuestionAdapter.getItemCount() > 0) {
            if (forcedQuestionAdapter.selectedIds == null) {
                return;
            } else {
                products.selectedForce = new ArrayList<>();
                products.selectedForce.add(forcedQuestionAdapter.selectedIds);
            }
        }

        if (unitsAdapter.getItemCount() > 0) {
            if (unitsAdapter.selectedUnitId == null) {
                return;
            } else {
                products.unit_id = unitsAdapter.selectedUnitId.unitId;
            }
        }

        products.selectedModifiers = modifiersAdapter.selectedIds;

        new BasketHelper(this).updateCart(cartCount, products, "Options");
    }

    @Override
    public void onApiFinished(ApiCallResponse responseBus) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusResponse eventBusResponse) {

    }
}
