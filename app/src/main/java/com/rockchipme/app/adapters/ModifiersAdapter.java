package com.rockchipme.app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ItemDetailsActivity;
import com.rockchipme.app.models.Products;

import java.util.ArrayList;
import java.util.List;

public class ModifiersAdapter extends RecyclerView.Adapter<ModifiersAdapter.ViewHolder> {
    Context context;
    private List<List<Products>> modifiers;
    public List<Products> selectedIds = new ArrayList<>();

    public ModifiersAdapter(Context context, List<List<Products>> modifiers) {
        this.context = context;
        if (modifiers == null){
            modifiers = new ArrayList<>();
        }
        this.modifiers = modifiers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_modifiers, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final List<Products> data = modifiers.get(position);
        if (modifiers.get(position) != null && modifiers.get(position).size() > 0){
            holder.setDataAdapterData(modifiers.get(position));

            if(modifiers.get(position).get(0).title != null && modifiers.get(position).get(0).title.trim().length()>0){
                holder.tvTitle.setText(modifiers.get(position).get(0).title);
            } else {
                holder.tvTitle.setText("Modifiers " + (position + 1));
            }
        } else {
            modifiers.remove(position);
            notifyDataSetChanged();
        }

        try{
            ((ItemDetailsActivity) context).listExpandOrCollapse(holder.group, holder.view.findViewById(R.id.imageView9), holder.rv);
        } catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int getItemCount() {
        return modifiers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ModifiersDataAdapter dataAdapter;
        RecyclerView rv;
        Group group;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            rv = itemView.findViewById(R.id.rv);
            group = itemView.findViewById(R.id.group);
            dataAdapter = new ModifiersDataAdapter();
            rv.setAdapter(dataAdapter);
        }

        public void setDataAdapterData(List<Products> modifiers) {
            if (modifiers == null){
                modifiers = new ArrayList<>();
            }
            dataAdapter.setAdapterData(modifiers);
        }
    }

    public void setAdapterData(List<List<Products>> modifiers) {
        if (modifiers == null){
            modifiers = new ArrayList<>();
        }
        this.modifiers = modifiers;
        notifyDataSetChanged();
    }


    class ModifiersDataAdapter extends RecyclerView.Adapter<ModifiersDataAdapter.ViewHolder> {
        List<Products> dataList = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_modifiers_data, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Products data = dataList.get(position);
            holder.cbModifiers.setText(data.name);
            holder.tvPrice.setText(data.rate);

            if (selectedIds.contains(data)){
                holder.cbModifiers.setChecked(true);
            } else {
                holder.cbModifiers.setChecked(false);
            }

            holder.cbModifiers.setOnClickListener(v -> {
                if (selectedIds.contains(data)){
                    selectedIds.remove(data);
                } else {
                    selectedIds.add(data);
                }
                try{
                    ((ItemDetailsActivity)context).setTotalPrice();
                } catch (Exception e){e.printStackTrace();}
                notifyDataSetChanged();
            });

            if (position == dataList.size()-1){
                holder.viewUnderline.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbModifiers;
            TextView tvPrice;
            View viewUnderline;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                cbModifiers = itemView.findViewById(R.id.cbModifiers);
                viewUnderline = itemView.findViewById(R.id.viewUnderline);
            }
        }

        public void setAdapterData(List<Products> modifiers) {
            dataList = modifiers;
            notifyDataSetChanged();
        }
    }
}
