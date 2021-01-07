package com.rockchipme.app.adapters;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.OutletsActivity;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.models.RestaurantDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alisons on 10/22/2015.
 */
public class OutletsAdapter extends RecyclerView.Adapter <OutletsAdapter.ViewHolder> {

    private OutletsActivity context;
    private List<RestaurantDetails> outlets = new ArrayList<>();
    public OutletsAdapter(OutletsActivity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.row_outlets_list, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final RestaurantDetails restaurantDetails = outlets.get(position);
        holder.tvOutletName.setText(restaurantDetails.name);
        holder.tvAddress.setText(restaurantDetails.location);
        holder.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=" +
                        restaurantDetails.latitude+","+restaurantDetails.longitude+" ("+ restaurantDetails.name +")");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                try {
                    context.startActivity(intent);
                }
                catch(ActivityNotFoundException ex) {
                    try
                    {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(unrestrictedIntent);
                    }
                    catch(ActivityNotFoundException innerEx)
                    {
                        Toast.makeText(context, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        if (restaurantDetails.restType != null && restaurantDetails.restType.trim().length() > 0){
            holder.tvType.setVisibility(View.VISIBLE);
            holder.tvType.setText(restaurantDetails.restType);
        } else {
            holder.tvType.setVisibility(View.GONE);
        }

        if (restaurantDetails.distance != null && restaurantDetails.distance.trim().length() > 0){
            holder.tvDistance.setVisibility(View.VISIBLE);
            holder.tvDistance.setText(restaurantDetails.distance + "km");
        } else {
            holder.tvDistance.setVisibility(View.GONE);
        }


        if (Constants.REST_KEY!=null && Constants.REST_KEY.equals(restaurantDetails.rest_key)){
            holder.cvContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.accent_bright));
        } else {
            holder.cvContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.storeOutletDetails(restaurantDetails);
            }
        });

        try {
            Picasso.with(context).load(restaurantDetails.getLogo()).placeholder(R.drawable.app_icon).into(holder.iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return outlets.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final TextView tvOutletName, tvAddress, tvType, tvDistance;
        final ConstraintLayout container;
//        final  FlowLayout flowLayoutRestaurantType;
        final Button btnLocation;
        final CircleImageView iv;
        final CardView cvContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            cvContainer = itemView.findViewById(R.id.cvContainer);
            container = itemView.findViewById(R.id.container);
            tvOutletName = itemView.findViewById(R.id.tvOutletName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvType = itemView.findViewById(R.id.tvType);
//            flowLayoutRestaurantType = itemView.findViewById(R.id.flowLayoutRestaurantType);
//            flowLayoutRestaurantType.removeAllViews();
            btnLocation = itemView.findViewById(R.id.btnLocation);
            iv = itemView.findViewById(R.id.civ);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }



    public void  setAdapterDatas(List<RestaurantDetails> outlets){
        this.outlets = outlets;
        notifyDataSetChanged();
    }

}








