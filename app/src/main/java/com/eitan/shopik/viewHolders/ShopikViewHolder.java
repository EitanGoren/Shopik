package com.eitan.shopik.viewHolders;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public abstract class ShopikViewHolder extends RecyclerView.ViewHolder {

    protected int mProgress = 0;
    protected boolean isFinishedFetchingData;
    private final RequestListener<Drawable> listener;
    private ShimmerFrameLayout mShimmerFrameLayout;

    public ShopikViewHolder(@NonNull View itemView) {
        super(itemView);
        listener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if(mShimmerFrameLayout != null) mShimmerFrameLayout.hideShimmer();
                return false;
            }
        };
    }

    protected void TabsScrollingHandler(Context context, ArrayList<String> images,
                                        Button mNext, Button mPrev, LinearLayout mDots,
                                        ImageView imageView, ShimmerFrameLayout shimmerFrameLayout) {

        mShimmerFrameLayout = shimmerFrameLayout;
        if(!images.isEmpty()) {
            final int[] index = {0};
            mDots.removeAllViews();
            if(shimmerFrameLayout != null) shimmerFrameLayout.startShimmer();
            Glide.with(context).load(images.get(index[0])).
                    apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).
                    transition(withCrossFade(650)).
                    listener(listener).
                    into(imageView);
            int images_num = images.size();

            ImageView[] imageViews = new ImageView[images_num];
            for (int i = 0; i < images_num; ++i) {
                ImageView img_v = new ImageView(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(GetDpValue(context, 10), GetDpValue(context, 10));
                layoutParams.setMarginStart(GetDpValue(context, 4));
                layoutParams.setMarginEnd(GetDpValue(context, 4));
                img_v.setContentDescription("dot " + i);
                img_v.setLayoutParams(layoutParams);
                img_v.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp));
                mDots.addView(img_v);
                imageViews[i] = img_v;
            }
            imageViews[0].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_lens_black));

            mNext.setOnClickListener(v -> {
                assert mShimmerFrameLayout != null;
                mShimmerFrameLayout.startShimmer();
                index[0]++;
                if (index[0] == images_num) index[0] = 0;
                Glide.with(context).load(images.get(index[0] % images_num)).apply(new RequestOptions().
                                override(Target.SIZE_ORIGINAL)).
                        transition(withCrossFade(500)).listener(listener).into(imageView);
                changeTabs(index[0] % images_num, imageViews, context);
            });
            mPrev.setOnClickListener(v -> {
                assert mShimmerFrameLayout != null;
                mShimmerFrameLayout.startShimmer();
                index[0]--;
                if (index[0] == -1) index[0] = images_num - 1;
                Glide.with(context).load(images.get(index[0] % images_num)).apply(new RequestOptions().
                                override(Target.SIZE_ORIGINAL).
                                format(DecodeFormat.PREFER_ARGB_8888)).
                        transition(withCrossFade(0)).listener(listener).into(imageView);
                changeTabs(index[0] % images_num, imageViews, context);
            });
        }
    }

    protected int GetDpValue(Context context, int value){
        float factor = context.getResources().getDisplayMetrics().density;
        return (int)(value * factor);
    }
    protected void changeTabs(int position, ImageView[] imageViews, Context context) {
        for (ImageView imageView : imageViews) {
            imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp));
        }
        imageViews[position].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_lens_black));
    }

    public void setFinishedFetchingData(boolean finishedFetchingData) {
        isFinishedFetchingData = finishedFetchingData;
    }
}
