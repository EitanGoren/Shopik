package com.eitan.shopik.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

import java.util.ArrayList;

public class FullscreenAdapter extends RecyclerView.Adapter<FullscreenAdapter.PicsViewHolder> {

    private final ArrayList<String> imagesUrl;
    private boolean isClicked = true;
    private final LinearLayout mDots;
    private ImageView[] imageViews;

    public FullscreenAdapter(Context context, ArrayList<String> imagesUrl) {
        this.imagesUrl = imagesUrl;
        ViewPager2 viewPager = ((Activity) context).findViewById(R.id.fullscreen_image_viewPager);
        viewPager.registerOnPageChangeCallback (new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int pos) {
                changeTabs(pos, imageViews, context);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        mDots = ((Activity)context).findViewById(R.id.buttons_layout);
    }

    private void changeTabs(int position, ImageView[] imageViews, Context context) {
        for (ImageView imageView : imageViews) {
            imageView.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_panorama_fish_eye_black_24dp));
        }
        imageViews[position].setBackground(ContextCompat.getDrawable(context, R.drawable.ic_lens_black));
    }

    @NonNull
    @Override
    public FullscreenAdapter.PicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PicsViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.fullscreen_pic, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FullscreenAdapter.PicsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return imagesUrl.size();
    }

    class PicsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView photoView;

        public PicsViewHolder(@NonNull View view){
            super(view);
            photoView = view.findViewById(R.id.fullscreen_image_item);

            photoView.setOnLongClickListener(v -> {
                isClicked = !isClicked;
                return true;
            });
        }

        private void setData(int position) {
            TabsScrollingHandler(photoView.getContext(), imagesUrl, position, mDots, photoView);
        }
        private int GetDpValue(Context context, int value){
            float factor = context.getResources().getDisplayMetrics().density;
            return (int)(value * factor);
        }
        private void TabsScrollingHandler(Context context, ArrayList<String> item,
                                          int pos, LinearLayout mDots,
                                          ImageView imageView) {
            mDots.removeAllViews();
            Macros.Functions.GlidePicture(context, item.get(pos), imageView,350);
            int images_num = item.size();

            imageViews = new ImageView[images_num];
            for(int i=0; i < images_num; ++i) {
                ImageView img_v = new ImageView(context);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(GetDpValue(context,10), GetDpValue(context,10));
                layoutParams.setMarginStart(GetDpValue(context,4));
                layoutParams.setMarginEnd(GetDpValue(context,4));
                img_v.setContentDescription("dot " + i);
                img_v.setLayoutParams(layoutParams);
                img_v.setBackground(ContextCompat.
                        getDrawable(context,R.drawable.ic_panorama_fish_eye_black_24dp));
                mDots.addView(img_v);
                imageViews[i] = img_v;
            }
            Macros.Functions.GlidePicture(context, item.get(pos % images_num),imageView,500);
        }
    }
}
