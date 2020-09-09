package com.eitan.shopik;

import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

public class CustomItemAnimator extends DefaultItemAnimator {
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.
                        itemView.getContext(), R.anim.viewholder_remove_item));
        return super.animateRemove(holder);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {

        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.
                        itemView.getContext(),R.anim.viewholder_add_item));
        return super.animateAdd(holder);
    }

    @Override
    public long getAddDuration() {
        return 500;
    }

    @Override
    public long getRemoveDuration() {
        return 500;
    }
}
