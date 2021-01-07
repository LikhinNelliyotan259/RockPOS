package com.rockchipme.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.OrderDetailsActivity;
import com.rockchipme.app.helpers.Utils;
import com.rockchipme.app.models.Orders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Alisons on 9/27/2017.
 */
public class OrdersAdapter extends RecyclerView.Adapter implements Serializable {

    List<Orders> ordersList = new ArrayList<>();
    Context context;
    String currency = "";


    public OrdersAdapter(Context context, String currecncy) {
        this.context = context;
        this.currency = currecncy;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_history,parent,false);

        vh = new OrdersViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ((OrdersViewHolder) holder).tv_orderId.setText("Order Id: "+ordersList.get(position).orderId);
        ((OrdersViewHolder) holder).tv_orderPlacedDate.setText(Utils.convertDateFormat(ordersList.get(position).orderedDate)/*.substring(0,ordersList.get(position).deliveryDate.indexOf(" "))*/);
        ((OrdersViewHolder) holder).tv_price.setText(currency + " " + ordersList.get(position).grandTotal);

        ((OrdersViewHolder) holder).tvOrderStatus.setText("Order Status: ");
        switch (ordersList.get(position).orderStatus){
            case "0":
                ((OrdersViewHolder) holder).tv_orderDeliveredDate.setText("Pending");
                break;
            case "1":
                ((OrdersViewHolder) holder).tv_orderDeliveredDate.setText("Confirmed");
                break;
            case "2":
                ((OrdersViewHolder) holder).tv_orderDeliveredDate.setText("In delivery process");
                break;
            case "3":
                ((OrdersViewHolder) holder).tvOrderStatus.setText("Delivery Date: ");
                ((OrdersViewHolder) holder).tv_orderDeliveredDate.setText(ordersList.get(position).deliveryDate/*.substring(0,ordersList.get(position).orderedDate.indexOf(" "))*/);
                break;
            case "4":
                ((OrdersViewHolder) holder).tv_orderDeliveredDate.setText("Cancelled");
                break;
        }


        ((OrdersViewHolder)holder).view.setOnClickListener(new View.OnClickListener() {

//            List<Orders> ordersList2 = new ArrayList<Orders>();
//            ordersList2 = ordersList;
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderId",ordersList.get(position).orderId);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    private class OrdersViewHolder extends RecyclerView.ViewHolder{

        TextView tv_orderId, tv_price, tv_orderPlacedDate, tvOrderStatus, tv_orderDeliveredDate;
        View view;
        public OrdersViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_orderId = (TextView) itemView.findViewById(R.id.tv_order_id);
            tv_price = (TextView) itemView.findViewById(R.id.tv_order_history_price);
            tv_orderPlacedDate = (TextView) itemView.findViewById(R.id.tv_order_placed_date);
            tv_orderDeliveredDate = (TextView) itemView.findViewById(R.id.tv_order_dispatched_date);

            tvOrderStatus = (TextView) itemView.findViewById(R.id.tv_label_order_dispatched);
        }
    }

    public void setAdapterData(List<Orders> ordersList){
        this.ordersList = ordersList;
        notifyDataSetChanged();
    }

    public List<Orders> getOrdersList(){
        if (ordersList==null){
            ordersList = new ArrayList<>();
        }
        return ordersList;
    }
}
