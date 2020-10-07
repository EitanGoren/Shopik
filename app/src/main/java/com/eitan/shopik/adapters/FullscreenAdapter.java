package com.eitan.shopik.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

import java.util.ArrayList;

public class FullscreenAdapter extends RecyclerView.Adapter<FullscreenAdapter.PicsViewHolder> {

    private final ArrayList<String> imagesUrl;
    private final ImageView mDot1;
    private final ImageView mDot2;
    private final ImageView mDot3;
    private final ImageView mDot4;
    private final ImageView mDot5;
    private boolean isClicked = true;
    private final ViewPager2 viewPager;

    public FullscreenAdapter(Context context, ArrayList<String> imagesUrl, String description) {
        this.imagesUrl = imagesUrl;

        mDot1 = ((Activity)context).findViewById(R.id.fullscreen_dot_1);
        mDot2 = ((Activity)context).findViewById(R.id.fullscreen_dot_2);
        mDot3 = ((Activity)context).findViewById(R.id.fullscreen_dot_3);
        mDot4 = ((Activity)context).findViewById(R.id.fullscreen_dot_4);
        mDot5 = ((Activity)context).findViewById(R.id.fullscreen_dot_5);
        TextView textView = ((Activity) context).findViewById(R.id.fullscreen_item_info);
        textView.setText(description);

        mDot1.setBackground(ContextCompat.getDrawable(mDot1.getContext(),R.drawable.ic_lens_black_24dp));
        mDot2.setBackground(ContextCompat.getDrawable(mDot2.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
        mDot3.setBackground(ContextCompat.getDrawable(mDot3.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
        mDot4.setBackground(ContextCompat.getDrawable(mDot4.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
        mDot5.setVisibility(View.GONE);

        YoYo.with(Techniques.DropOut).delay(500).playOn(mDot1);
        YoYo.with(Techniques.DropOut).delay(800).playOn(mDot2);
        YoYo.with(Techniques.DropOut).delay(1100).playOn(mDot3);
        YoYo.with(Techniques.DropOut).delay(1400).playOn(mDot4);

        viewPager = ((Activity)context).findViewById(R.id.fullscreen_image_viewPager);
    }

    @NonNull
    @Override
    public FullscreenAdapter.PicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FullscreenAdapter.PicsViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.fullscreen_pic,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FullscreenAdapter.PicsViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return imagesUrl.size();
    }

    private void changeTabs(int position) {
        switch (position){
            case 0:
                mDot1.setBackground(ContextCompat.getDrawable(mDot1.getContext(),R.drawable.ic_lens_black_24dp));
                mDot2.setBackground(ContextCompat.getDrawable(mDot2.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
                break;
            case 1:
                mDot1.setBackground(ContextCompat.getDrawable(mDot1.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
                mDot2.setBackground(ContextCompat.getDrawable(mDot2.getContext(),R.drawable.ic_lens_black_24dp));
                mDot3.setBackground(ContextCompat.getDrawable(mDot3.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
                break;
            case 2:
                mDot2.setBackground(ContextCompat.getDrawable(mDot2.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
                mDot3.setBackground(ContextCompat.getDrawable(mDot3.getContext(),R.drawable.ic_lens_black_24dp));
                mDot4.setBackground(ContextCompat.getDrawable(mDot4.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
                break;
            case 3:
                mDot3.setBackground(ContextCompat.getDrawable(mDot3.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
                mDot4.setBackground(ContextCompat.getDrawable(mDot4.getContext(),R.drawable.ic_lens_black_24dp));
                mDot5.setBackground(ContextCompat.getDrawable(mDot5.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
                break;
            case 4:
                mDot4.setBackground(ContextCompat.getDrawable(mDot4.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
                mDot5.setBackground(ContextCompat.getDrawable(mDot5.getContext(),R.drawable.ic_lens_black_24dp));
                break;
        }
    }

    class PicsViewHolder extends RecyclerView.ViewHolder{

        private final ImageView photoView;

        public PicsViewHolder(@NonNull View view) {
            super(view);
            photoView = view.findViewById(R.id.fullscreen_image_item);
        }

        private void setData(int position){

            if (position != imagesUrl.size()) {
                photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                photoView.setPadding(0,0,0,0);
                Macros.Functions.GlidePicture(photoView.getContext(),imagesUrl.get(position),photoView);
            }

            viewPager.registerOnPageChangeCallback (new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int pos) {
                    changeTabs(pos);
                    photoView.setOnLongClickListener(v -> {
                       if (isClicked) {
                          // YoYo.with(Techniques.FadeOutUp).playOn(buttons_layout);
                       }
                       else {
                          // YoYo.with(Techniques.FadeInDown).playOn(buttons_layout);
                       }
                       isClicked = !isClicked;
                       return true;
                   });
                }

                @Override
                public void onPageScrollStateChanged(int state) {}

            });
        }
    }
}
