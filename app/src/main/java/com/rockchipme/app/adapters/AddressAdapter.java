package com.rockchipme.app.adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.AddressListActivity;
import com.rockchipme.app.activities.CreateAddressActivty;
import com.rockchipme.app.helpers.ActivityRequestCode;
import com.rockchipme.app.helpers.AddressHelper;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.models.AddressResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisons on 9/25/2017.
 */

public class AddressAdapter extends RecyclerView.Adapter {

    private AddressListActivity context;
    private List<AddressResponse.Address> addressList = new ArrayList<>();
    private String addressId;
    private int deleted_position;

    private SharedPreferences prefLogin;
    private Boolean isFirst = true;
    private boolean isCreatedNewAddress;

    private int selectedPosition = 0;//-1

    public AddressAdapter(AddressListActivity context, boolean isCreatedNewAddress, SharedPreferences prefLogin) {
        this.context = context;
        this.isCreatedNewAddress = isCreatedNewAddress;
        this.prefLogin = prefLogin;
//        selectAddressActivity = new AddressListActivity();

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_select_address,parent,false);
        vh = new AddressViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (isCreatedNewAddress){
            selectedPosition = addressList.size()-1;
        }else {
            if (isFirst) {
                if (prefLogin.getBoolean(Constants.isAddressAdded, false)) {
                    if (!addressList.isEmpty()) {
                        for (int i = 0; i < addressList.size(); i++) {
                            if (addressList.get(i).addressId.equalsIgnoreCase(prefLogin.getString(Constants.addressId, ""))) {
                                selectedPosition = i;
                            }
                        }
                    }
                }
                isFirst = false;
            }
        }


        ((AddressViewHolder)holder).rb_name.setTag(position);
//        ((AddressViewHolder)holder).rb_name.setText(addressList.get(position).fullName);
        ((AddressViewHolder)holder).tvAddressName.setText(addressList.get(position).fullName);
        ((AddressViewHolder)holder).tvAddress.setText(new AddressHelper(context).getAddress(addressList.get(position), false));

        ((AddressViewHolder)holder).rb_name.setChecked(position == selectedPosition);

        ((AddressViewHolder)holder).rb_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCheckChanged(v);
                new AddressHelper(context).storeAddress(addressList,addressList.get(position).addressId);
                ((Activity)context).finish();
            }
        });

        ((AddressViewHolder)holder).row.setTag(position);
        ((AddressViewHolder)holder).row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCheckChanged(v);
                new AddressHelper(context).storeAddress(addressList,addressList.get(position).addressId);
                ((Activity)context).finish();
            }
        });

        ((AddressViewHolder)holder).tvAddressEdit.setTag(position);
        ((AddressViewHolder)holder).tvAddressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemCheckChanged(v);
                Intent intent = new Intent(context, CreateAddressActivty.class);
                intent .putExtra("use","edit");
                intent.putExtra("from","selectAddress");
                intent.putExtra("address_id",addressList.get(position).addressId);
                ((Activity)context).startActivityForResult(intent, ActivityRequestCode.RC_ADDRESS_SAVED);
//                context.startActivity(intent);
            }
        });

        ((AddressViewHolder)holder).tvAddressDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(0);
                itemCheckChanged(v);
                deleted_position = position;
                setAddressId(addressList.get(position).addressId);
                context.deleteAddressApi(addressList.get(position).addressId);

            }
        });

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }



    private void itemCheckChanged(View v) {
        selectedPosition = (Integer) v.getTag();
//        notifyDataSetChanged();
    }


    private void setAddressId(String addressId){
        this.addressId = addressId;
    }

    public String getAddressId(){
        return addressId;
    }


    public int getSelectedPosition() {
        return selectedPosition;
    }

    public int getDeleted_position() {
        return deleted_position;
    }

    private class AddressViewHolder extends RecyclerView.ViewHolder{

        RadioButton rb_name;
        TextView tvAddress, tvAddressEdit, tvAddressDelete, tvAddressName;
        View row;
        private AddressViewHolder(View itemView) {
            super(itemView);
            row = itemView;
            rb_name = (RadioButton) itemView.findViewById(R.id.rb_address_name);
            tvAddress = (TextView) itemView.findViewById(R.id.tv_full_address);
            tvAddressName = (TextView) itemView.findViewById(R.id.tv_address_name);
            tvAddressEdit = (TextView) itemView.findViewById(R.id.tv_edit_address);
            tvAddressDelete = (TextView) itemView.findViewById(R.id.tv_delete_address);
        }
    }

    public List<AddressResponse.Address> getAddressList() {
        if (addressList==null){
            addressList = new ArrayList<>();
        }
        return addressList;
    }

    public void setAddressList(List<AddressResponse.Address> addressList){
        this.addressList = addressList;
        notifyDataSetChanged();
    }

}
