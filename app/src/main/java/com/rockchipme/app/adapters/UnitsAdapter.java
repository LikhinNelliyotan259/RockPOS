package com.rockchipme.app.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ItemDetailsActivity;
import com.rockchipme.app.models.Unit;

import java.util.ArrayList;
import java.util.List;

public class UnitsAdapter extends RecyclerView.Adapter<UnitsAdapter.ViewHolder> {
    Context context;
    private List<Unit> units;
    public Unit selectedUnitId;

    public UnitsAdapter(Context context, List<Unit> units) {
        this.context = context;
        if (units == null) {
            units = new ArrayList<>();
        }
        this.units = units;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_units, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Unit data = units.get(position);
        holder.rbUnit.setText(data.name);
        holder.tvPrice.setText(data.price);
        if (data.primaryUnit.equals("1")) {
            holder.rbUnit.setChecked(true);
            selectedUnitId = data;
        } else {
            holder.rbUnit.setChecked(false);
        }

        holder.rbUnit.setOnClickListener(v -> {
            for (int i = 0; i < units.size(); i++) {
                units.get(i).primaryUnit = "0";
            }
            units.get(position).primaryUnit = "1";
            selectedUnitId = data;
            try {
                ((ItemDetailsActivity) context).setTotalPrice();
                ((ItemDetailsActivity) context).collapseUnitList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            notifyDataSetChanged();
        });

        if (position == units.size() - 1) {
            holder.viewUnderline.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return units.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton rbUnit;
        TextView tvPrice;
        View viewUnderline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            rbUnit = itemView.findViewById(R.id.rbUnit);
            viewUnderline = itemView.findViewById(R.id.viewUnderline);
        }
    }

    public void setAdapterData(List<Unit> units) {
        if (units == null) {
            units = new ArrayList<>();
        }
        this.units = units;
        notifyDataSetChanged();
    }

}
