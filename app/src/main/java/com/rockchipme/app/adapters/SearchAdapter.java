package com.rockchipme.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.text.Normalizer;

import com.rockchipme.app.R;
import com.rockchipme.app.activities.ItemDetailsActivity;
import com.rockchipme.app.models.Products;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alisons on 10/22/2015.
 */
public class SearchAdapter extends RecyclerView.Adapter {

    // public static List<MenuCategory> newproductslist, anotherlist;
    private Context context;
    //List<MenuCategory> productslist;
    private List<Products> productslist = new ArrayList<>();
    private String searchkey;

    /*public SearchAdapter(Context context, List<MenuCategory> datas, ListView lv_search, TextView tv_noresults) {
        mcontext = context;
        productslist = datas;
        newproductslist = datas;
        this.lv_search = lv_search;
        this.tv_noresults = tv_noresults;
        anotherlist = new ArrayList<MenuCategory>();
    }*/

    public SearchAdapter(Context context) {
        this.context = context;
    }

    // to highlighting the searching string
    CharSequence highlight(String search, String originalText) {
        // ignore case and accents
        // the same thing should have been done for the search text
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

        int start = normalizedText.indexOf(search);
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());

                highlighted.setSpan(new BackgroundColorSpan(Color.YELLOW), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.row_search_list, parent, false);
            vh = new SearchViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (searchkey == null) {
            //tvSearchItemName.setText(newproductslist.get(position).getCategoryName());

        } else {
            if (productslist.get(position).name.toLowerCase().contains(searchkey)) {
                ((SearchViewHolder)holder).tvSearchItemName.setText(highlight(searchkey, productslist.get(position).name));
            } else {
                ((SearchViewHolder)holder).tvSearchItemName.setText(productslist.get(position).name);
            }
        }

        ((SearchViewHolder)holder).rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("pdtId", productslist.get(position).pdtId);
                try{
                    View view = ((Activity)context).getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                } catch (Exception e){e.printStackTrace();}
                context.startActivity(intent);
            }
        });

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return productslist.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder{
        TextView tvSearchItemName;
        View rowView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            rowView = itemView;
            tvSearchItemName = itemView.findViewById(R.id.tv_searchedItem);
        }
    }



    public void  setAdapterDatas(List<Products> searchList, String searchkey){
        productslist = searchList;
        this.searchkey = searchkey;
        notifyDataSetChanged();
    }

}








