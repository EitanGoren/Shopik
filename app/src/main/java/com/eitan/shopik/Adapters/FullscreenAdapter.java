package com.eitan.shopik.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;

import java.util.ArrayList;

public class FullscreenAdapter extends RecyclerView.Adapter<FullscreenAdapter.PicsViewHolder> {

    private ArrayList<String> imagesUrl;
    private ImageView mDot1,mDot2,mDot3,mDot4,mDot5;
    private CardView button;
    private CardView buttons_layout,comp_info;
    private String description;
   // private String seller;
    private boolean isClicked = true;
    private ViewPager2 viewPager;

    public FullscreenAdapter (Context context, ArrayList<String> imagesUrl, String description, String id, String seller) {
        this.imagesUrl = imagesUrl;
        this.description = description;
      //  this.seller = seller;

        mDot1 = ((Activity)context).findViewById(R.id.fullscreen_dot_1);
        mDot2 = ((Activity)context).findViewById(R.id.fullscreen_dot_2);
        mDot3 = ((Activity)context).findViewById(R.id.fullscreen_dot_3);
        mDot4 = ((Activity)context).findViewById(R.id.fullscreen_dot_4);
        mDot5 = ((Activity)context).findViewById(R.id.fullscreen_dot_5);

        mDot1.setBackground(ContextCompat.getDrawable(mDot1.getContext(),R.drawable.ic_lens_black_24dp));
        mDot2.setBackground(ContextCompat.getDrawable(mDot2.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
        mDot3.setBackground(ContextCompat.getDrawable(mDot3.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
        mDot4.setBackground(ContextCompat.getDrawable(mDot4.getContext(),R.drawable.ic_baseline_panorama_fish_eye));
        mDot5.setVisibility(View.GONE);

        button = ((Activity)context).findViewById(R.id.close_card);
        buttons_layout = ((Activity)context).findViewById(R.id.buttons_layout);
        comp_info = ((Activity)context).findViewById(R.id.comp_info);

        YoYo.with(Techniques.FadeInRight).playOn(button);
        YoYo.with(Techniques.FadeInDown).playOn(buttons_layout);
        YoYo.with(Techniques.FadeInLeft).playOn(comp_info);

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

        private ImageView photoView;
        private TextView textView, no_video;
        private VideoView mVideoView;
        private MediaController videoMediaController;
        private RelativeLayout videoLayout,anchor;

        public PicsViewHolder(@NonNull View view) {
            super(view);
            photoView = view.findViewById(R.id.fullscreen_image_item);
            mVideoView = view.findViewById(R.id.item_video);
            mVideoView.setVisibility(View.GONE);
            videoMediaController = new MediaController(mVideoView.getContext());
            videoLayout = view.findViewById(R.id.video_layout);
            videoLayout.setVisibility(View.GONE);
            anchor = view.findViewById(R.id.video);
            anchor.setVisibility(View.GONE);
            textView = view.findViewById(R.id.fullscreen_item_info);
            no_video = view.findViewById(R.id.No_video_text);
        }

        private void setData(int position){
            textView.setText(description);
            String no_video_text = "No video for this item";
            no_video.setText(no_video_text);

            if (position != imagesUrl.size()) {
                videoLayout.setVisibility(View.INVISIBLE);
                videoMediaController.hide();
                photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                photoView.setPadding(0,0,0,0);
                Macros.Functions.GlidePicture(photoView.getContext(),imagesUrl.get(position),photoView);
                mVideoView.pause();
                videoMediaController.hide();
            }

            viewPager.registerOnPageChangeCallback (new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

                @Override
                public void onPageSelected(int pos) {
                    changeTabs(pos);
                   /* if(category.equals("ASOS") && (pos == imagesUrl.size())) {

                        YoYo.with(Techniques.FadeOutRight).playOn(button);
                        YoYo.with(Techniques.FadeOutUp).playOn(buttons_layout);
                        YoYo.with(Techniques.FadeOutLeft).playOn(comp_info);

                        if(path != null) {
                            videoLayout.setVisibility(View.VISIBLE);
                            mVideoView.setVisibility(View.VISIBLE);
                            anchor.setVisibility(View.VISIBLE);
                            mVideoView.setVideoPath(path);
                            mVideoView.setMediaController(videoMediaController);
                            no_video.setVisibility(View.INVISIBLE);
                        }
                        else {
                            getVideoLink getVideoLink = new PicsViewHolder.getVideoLink(id);
                            getVideoLink.execute();
                        }

                        videoMediaController.setAnchorView(anchor);
                        mVideoView.setOnPreparedListener(mp -> {
                            mVideoView.requestFocus();
                            mVideoView.start();
                        });

                        mVideoView.setOnCompletionListener(MediaPlayer::start);
                    }
                    else {*/
                   videoLayout.setVisibility(View.GONE);
                   mVideoView.setVisibility(View.GONE);
                   anchor.setVisibility(View.GONE);
                   photoView.setOnLongClickListener(v -> {
                       if (isClicked) {
                           YoYo.with(Techniques.FadeOutRight).playOn(button);
                           YoYo.with(Techniques.FadeOutUp).playOn(buttons_layout);
                           YoYo.with(Techniques.FadeOutLeft).playOn(comp_info);
                       }
                       else {
                           YoYo.with(Techniques.FadeInRight).playOn(button);
                           YoYo.with(Techniques.FadeInDown).playOn(buttons_layout);
                           YoYo.with(Techniques.FadeInLeft).playOn(comp_info);
                       }
                       isClicked = !isClicked;
                       return true;
                   });
                }

                @Override
                public void onPageScrollStateChanged(int state) {}

            });
        }
       /* private class getVideoLink extends AsyncTask<Void, Integer, String> {

            String id,video_path;

            getVideoLink(String id){
                this.id = id;
            }

            @Override
            protected String doInBackground(Void... voids) {

                String video_path ="";
                try {
                    getVideo(id);
                }
                catch (Exception e){
                    Log.d(Macros.TAG, "FullscreenActivity:getVideoLink: " + Objects.requireNonNull(e.getMessage()));
                }
                return video_path;
            }

            private void getVideo(String id) throws IOException {

                URL url = new URL("https://video.asos-media.com/products/0/" + id + "-catwalk-AVS.m3u8");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                try {

                    StringBuilder data = new StringBuilder();

                    String line = "";
                    int i=0;
                    while (line != null && i < 5) {
                        line = bufferedReader.readLine();
                        data.append(line);
                        ++i;
                    }
                    String[] data_split = data.toString().split("_media_",2);
                    String koko = data_split[1];
                    koko = koko.replace(".m3u8","");

                    video_path = koko;
                }
                catch (FileNotFoundException ex){
                    video_path = null;
                }
                catch(Exception e){
                    Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
                }
                finally {
                    httpURLConnection.disconnect();
                    inputStream.close();
                    bufferedReader.close();
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(video_path != null) {
                    videoLayout.setVisibility(View.VISIBLE);
                    path = Macros.VIDEO_LINK + video_path;
                    mVideoView.setVideoPath(path);
                    mVideoView.setMediaController(videoMediaController);
                    no_video.setVisibility(View.INVISIBLE);
                }
                else {
                    videoLayout.setVisibility(View.INVISIBLE);
                    photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    photoView.setImageResource(R.drawable.tooki);
                    photoView.setPadding(25,40,25,40);
                    no_video.setVisibility(View.VISIBLE);
                }
            }
        }*/
    }
}
