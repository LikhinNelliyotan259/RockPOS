package com.rockchipme.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rockchipme.app.R;
import com.rockchipme.app.models.Thumb;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Android on 03/01/2017.
 */
public class ThumbImagesAdapter
        extends RecyclerView.Adapter<ThumbImagesAdapter.ThumbViewHolder> {

    private static final int PHOTO_ANIMATION_DELAY = 600;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    private boolean lockedAnimations = false;
    private int lastAnimatedItem = -1;
    private List<Thumb> listImages;
    Context context;

    public ThumbImagesAdapter(Context context, List<Thumb> listImages) {
        this.context = context;
        this.listImages = listImages;
    }

    @Override
    public ThumbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_single_item_thumb, parent, false);
        return new ThumbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ThumbViewHolder holder, int position) {
        Thumb thumb = listImages.get(position);
        final String image = thumb.getImage();
//        Glide.with(holder.iv.getContext())
//                .load(Constants.BASE_URL + image)
//                .centerCrop()
//                .into(holder.iv);
        bindPhoto(holder, position, thumb.getImage());
        if(thumb.getSelected()){
            holder.ll.setBackgroundResource(R.drawable.rect_border_selected);
        }else {
            holder.ll.setBackgroundResource(R.drawable.rect_border_gray);
        }
    }

    private void bindPhoto(final ThumbViewHolder holder, int position, String image) {
        Picasso.with(holder.iv.getContext())
                .load(image)
                .resize(200, 200)
                .centerCrop()
                .into(holder.iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        animatePhoto(holder);
                    }

                    @Override
                    public void onError() {

                    }
                });
        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

    private void animatePhoto(final ThumbViewHolder holder) {
        if (!lockedAnimations) {
            if (lastAnimatedItem == holder.getPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = PHOTO_ANIMATION_DELAY + holder.getPosition() * 30;

            holder.iv.setScaleY(0);
            holder.iv.setScaleX(0);

            holder.iv.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return listImages.size();
    }

    public static class ThumbViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final ImageView iv;
        public final LinearLayout ll;

        public ThumbViewHolder(View view) {
            super(view);
            this.view = view;
            ll = (LinearLayout) view.findViewById(R.id.ll_thumb_bg);
            iv = (ImageView) view.findViewById(R.id.iv_thumb_horizontal);
        }
    }

    public Thumb getValueAt(int position) {
        return listImages.get(position);
    }
}