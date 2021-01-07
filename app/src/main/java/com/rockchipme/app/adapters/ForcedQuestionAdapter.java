package com.rockchipme.app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ItemDetailsActivity;
import com.rockchipme.app.helpers.Constants;
import com.rockchipme.app.models.Products;

import java.util.ArrayList;
import java.util.List;

public class ForcedQuestionAdapter extends RecyclerView.Adapter<ForcedQuestionAdapter.ViewHolder> {

    public ForcedQuestionAdapter(Context context, List<List<Products>> forcedQuestions) {
        this.context = context;
        if (forcedQuestions == null) {
            forcedQuestions = new ArrayList<>();
        }
        this.forcedQuestions = forcedQuestions;
    }


    Context context;
    private List<List<Products>> forcedQuestions;
    public Products selectedIds;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_modifiers, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        final List<Products> data = modifiers.get(position);
        if (forcedQuestions.get(position) != null && forcedQuestions.get(position).size() > 0) {
            holder.setDataAdapterData(forcedQuestions.get(position));
            if(forcedQuestions.get(position).get(0).title != null && forcedQuestions.get(position).get(0).title.trim().length()>0){
                holder.tvTitle.setText(forcedQuestions.get(position).get(0).title);
            } else {
                holder.tvTitle.setText("Forced Option " + (position + 1));
            }

        } else {
            forcedQuestions.remove(position);
            notifyDataSetChanged();
        }

        try{
            ((ItemDetailsActivity) context).listExpandOrCollapse(holder.group, holder.view.findViewById(R.id.imageView9), holder.rv);
        } catch (Exception e){e.printStackTrace();}

    }

    @Override
    public int getItemCount() {
        return forcedQuestions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ForcedQuestionDataAdapter dataAdapter;
        RecyclerView rv;
        Group group;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            rv = itemView.findViewById(R.id.rv);
            group = itemView.findViewById(R.id.group);
            dataAdapter = new ForcedQuestionDataAdapter();
            rv.setAdapter(dataAdapter);
        }

        void setDataAdapterData(List<Products> modifiers) {
            if (modifiers == null) {
                modifiers = new ArrayList<>();
            }
            dataAdapter.setAdapterData(modifiers);
        }
    }

    public void setAdapterData(List<List<Products>> forcedQuestions){
        if (forcedQuestions == null) {
            forcedQuestions = new ArrayList<>();
        }
        this.forcedQuestions = forcedQuestions;
        Log.d(Constants.APP_TAG, "forcedQuestions size:"+forcedQuestions);
        notifyDataSetChanged();
    }

    class ForcedQuestionDataAdapter extends RecyclerView.Adapter<ForcedQuestionDataAdapter.ViewHolder> {
        List<Products> dataList = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_units, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Products data = dataList.get(position);
            holder.rbQuestions.setText(data.name);
            holder.tvPrice.setText(data.rate);

            if (selectedIds != null && selectedIds.equals(data)) {
                holder.rbQuestions.setChecked(true);
            } else {
                holder.rbQuestions.setChecked(false);
            }


//
            holder.rbQuestions.setOnClickListener(v -> {
                selectedIds = data;
                try{
                    ((ItemDetailsActivity)context).setTotalPrice();
                    ((ItemDetailsActivity) context).collapseForceList();
                } catch (Exception e){e.printStackTrace();}
                ForcedQuestionAdapter.this.notifyDataSetChanged();
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
            RadioButton rbQuestions;
            TextView tvPrice;
            View viewUnderline;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                rbQuestions = itemView.findViewById(R.id.rbUnit);
                viewUnderline = itemView.findViewById(R.id.viewUnderline);
            }
        }

        public void setAdapterData(List<Products> modifiers) {
            dataList = modifiers;
            this.notifyDataSetChanged();
        }
    }
}
