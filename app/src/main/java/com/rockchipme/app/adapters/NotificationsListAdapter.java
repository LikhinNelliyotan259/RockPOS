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
import com.rockchipme.app.activities.ItemDetailsActivity;
import com.rockchipme.app.activities.OrderDetailsActivity;
import com.rockchipme.app.activities.ProductListActivity;
import com.rockchipme.app.models.Notification;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sibin on 10/20/2017.
 */

public class NotificationsListAdapter extends RecyclerView.Adapter <NotificationsListAdapter.NotificationsViewHolder> {


    private List<Notification> notificationsList = new ArrayList<>();
    private Context context;

    public NotificationsListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_notification_list, parent, false);
        return new NotificationsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, final int position) {
        final Notification data = notificationsList.get(position);
        holder.tvTitle.setText(data.title);
        holder.tvTime.setText(data.sentDate);
        holder.tvMessage.setText(data.message);

        if (data.image!=null && data.image.trim().length()>0){
            Picasso.with(context)
                    .load(data.image)
//                    .placeholder(R.drawable.banner)
                    .into(holder.ivNotification);
        } else {
//            ((NotificationsViewHolder)holder).ivNotification.setImageResource(R.drawable.banner);
            Picasso.with(context)
                    .load(R.drawable.app_icon)
                    .into(holder.ivNotification);
        }

        holder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirect(data);
            }
        });

    }



    private void redirect(Notification notification){
        Intent intent = null;
        if (notification==null){
            return;
        }
        switch (notification.type) {
            case "1":
                intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("orderId", notification.value);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case "2":
                intent = new Intent(context, ProductListActivity.class);
//                    "data":[{"catId":"5","image":"img_5","pdtCount":"36","name":"JUICE"}]
                intent.putExtra("catId", notification.value);
                if (notification.notificationdate!=null && notification.notificationdate.size()>0) {
                    intent.putExtra("itemsCount", notification.notificationdate.get(0).pdtCount);
                    intent.putExtra("catName", notification.notificationdate.get(0).name);
                    intent.putExtra("catImg", notification.notificationdate.get(0).getImage());
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                break;
            case "3":
                intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("pdtId", notification.value);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
        }
        if(intent != null){
            context.startActivity(intent);
        }


    }




    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    class NotificationsViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        ImageView ivNotification;
        View rowView;

        NotificationsViewHolder(View view) {
            super(view);
            rowView = view;
            tvTitle = view.findViewById(R.id.tvNotificationTitle);
            tvMessage = view.findViewById(R.id.tvNotificationMessage);
            tvTime = view.findViewById(R.id.tvNotificationTime);
            ivNotification = view.findViewById(R.id.civ);
        }
    }

    public void setAdapterData(List<Notification> notificationsList){
        this.notificationsList = notificationsList;
        notifyDataSetChanged();
    }
}