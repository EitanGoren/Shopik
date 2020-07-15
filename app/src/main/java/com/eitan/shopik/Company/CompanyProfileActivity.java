package com.eitan.shopik.Company;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyProfileActivity extends YouTubeBaseActivity {

    private DocumentReference companyFS;
    private String companyId,imageUrl,name,facebook,twitter,youtube,instagram,site,
            cover,customer_id,description,slogan;
    private static String youtube_link;
    private CircleImageView mProfile_image,toolbar_pic;
    private ImageView mFacebook,mTwitter,mSite,mInstagram,mYoutube,bgimage;
    private TextView mRaters,mDescription,mSlogan,mRatingNum;
    private static TextView mVideoTitle;
    private RatingBar ratingBar,smallRating;
    private Map rating_map;
    private Float current_rating;
    private long raters;
    private Map data;
    private boolean isCompany;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer player;
    private YouTubePlayer.OnInitializedListener initializedListener;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 ) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri resultUri = data.getData();
                mProfile_image.setImageURI(resultUri);
                toolbar_pic.setImageURI(resultUri);
                if (resultUri != null) {

                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("companyProfiles").child(companyId);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);

                    Glide.with(getApplicationContext()).load(bitmap).into(mProfile_image);
                    Glide.with(getApplicationContext()).load(bitmap).into(toolbar_pic);

                    byte[] data2 = baos.toByteArray();

                    UploadTask uploadTask = filePath.putBytes(data2);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(Macros.TAG, "failed to load company picture:" + e.getMessage());
                        }
                    });

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String, Object> userInfo = new HashMap<String, Object>();
                                    userInfo.put("logo_url", uri.toString());
                                    imageUrl = uri.toString();
                                    companyFS.update(userInfo);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.d(Macros.TAG, "failed to load company picture:" + exception.getMessage());
                                }
                            });
                        }
                    });

                }
            }
        }
        if(requestCode == 2 ) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                Uri resultUri = data.getData();
                bgimage.setImageURI(resultUri);
                if (resultUri != null) {

                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("companyCovers").child(companyId);
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), resultUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);

                    Glide.with(getApplicationContext()).load(bitmap).into(bgimage);

                    byte[] data2 = baos.toByteArray();

                    UploadTask uploadTask = filePath.putBytes(data2);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(Macros.TAG, "failed to load company picture:" + e.getMessage());
                        }
                    });

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Map<String, Object> userInfo = new HashMap<String, Object>();
                                    userInfo.put("cover_image_url", uri.toString());
                                    cover = uri.toString();
                                    companyFS.update(userInfo);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.d(Macros.TAG, "failed to load company picture:" + exception.getMessage());
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_profile);

        init();

        companyFS.get().addOnSuccessListener(documentSnapshot -> {
            if(documentSnapshot.exists()){
                data = documentSnapshot.getData();
            }
        }).addOnCompleteListener(task -> {

            imageUrl = data.get("logo_url") != null ? Objects.requireNonNull(data.get("logo_url")).toString() : null;
            name = data.get("name") != null ? Objects.requireNonNull(data.get("name")).toString() : "Company Name";
            facebook = data.get("facebook_link") != null ? Objects.requireNonNull(data.get("facebook_link")).toString() : null;
            twitter = data.get("twitter") != null ? Objects.requireNonNull(data.get("twitter")).toString() : null;
            instagram = data.get("instagram_link") != null ? Objects.requireNonNull(data.get("instagram_link")).toString() : null;
            youtube = data.get("yt_link") != null ? Objects.requireNonNull(data.get("yt_link")).toString() : null;
            site = data.get("site_link") != null ? Objects.requireNonNull(data.get("site_link")).toString() : null;
            cover = data.get("cover_image_url") != null ? Objects.requireNonNull(data.get("cover_image_url")).toString() : null;
            description = data.get("description") != null ? Objects.requireNonNull(data.get("description")).toString() : Macros.DEFAULT_DESCRIPTION;
            youtube_link = data.get("youtube_link") != null ? Objects.requireNonNull(data.get("youtube_link")).toString() : null;
            slogan = data.get("slogan") != null ? Objects.requireNonNull(data.get("slogan")).toString() : "Some slogan shit...";

            mDescription.setText(description);
            mSlogan.setText(slogan);

            setYoutubeLink();
            setCollapsingBar();
            setRating();
            updateRating();
            setImages();
        });

        mProfile_image.setOnLongClickListener(v -> {
            if(!isCompany)
                return false;
            else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
                return true;
            }
        });

        bgimage.setOnLongClickListener(v -> {
            if(!isCompany)
                return false;
            else {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
                return true;
            }
        });

        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if(verticalOffset <= -550) {
                toolbar_pic.setVisibility(View.VISIBLE);
                smallRating.setVisibility(View.VISIBLE);
                mRatingNum.setVisibility(View.VISIBLE);
                ratingBar.setVisibility(View.GONE);
                mRaters.setVisibility(View.GONE);
            }
            else {
                toolbar_pic.setVisibility(View.INVISIBLE);
                smallRating.setVisibility(View.INVISIBLE);
                mRatingNum.setVisibility(View.INVISIBLE);
                ratingBar.setVisibility(View.VISIBLE);
                mRaters.setVisibility(View.VISIBLE);
            }
        });

        ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if(fromUser) {
                Float new_rating;
                if(!rating_map.containsKey(customer_id)) {
                    // calculate rating with the new user
                    new_rating = ((current_rating * raters) + rating) / (raters + 1);
                    raters++;
                }
                else{
                    //change existing rating
                    float old_customer_rating = Float.parseFloat(Objects.requireNonNull(rating_map.get(customer_id)).toString());
                    new_rating = ((current_rating * raters) - old_customer_rating + rating) / (raters);
                }

                mRatingNum.setText(String.valueOf(new_rating));
                rating_map.put("total_rating", new_rating);
                rating_map.put(customer_id, rating);

                updateRating();
                Toast.makeText(getApplicationContext(), "Thank you!", Toast.LENGTH_SHORT).show();
            }
        });
        mSite.setOnClickListener(v -> {
            if(site == null) return;
            if (!site.startsWith("http://") && !site.startsWith("https://"))
                site = "http://" + site;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site));
            startActivity(browserIntent);
        });
        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(facebook == null) return;
                if (!facebook.startsWith("http://") && !facebook.startsWith("https://"))
                    facebook = "http://" + facebook;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook));
                startActivity(browserIntent);
            }
        });
        mTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(twitter == null) return;
                if (!twitter.startsWith("http://") && !twitter.startsWith("https://"))
                    twitter = "http://" + twitter;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
                startActivity(browserIntent);
            }
        });
        mInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(instagram == null) return;
                if (!facebook.startsWith("http://") && !instagram.startsWith("https://"))
                    instagram = "http://" + instagram;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagram));
                startActivity(browserIntent);
            }
        });
        mYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(youtube == null) return;
                if (!youtube.startsWith("http://") && !youtube.startsWith("https://"))
                    youtube = "http://" + youtube;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube));
                startActivity(browserIntent);
            }
        });

        initializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer,boolean b) {
                youTubePlayer.loadVideo(youtube_link);
                player = youTubePlayer;
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(Macros.TAG,"onInitializationFailure: " + youTubeInitializationResult.name());
            }
        };

    }

    private void setYoutubeLink() {
        if(youtube_link != null) {
            String[] temp = youtube_link.split("=", 2);
            youtube_link = temp[1];
        }
        else
            youtube_link = Macros.DEFAULT_YOUTUBE_VIDEO;

        youTubePlayerView.initialize( Macros.API_KEY, initializedListener );
        getVideoTitle getVideoTitle = new getVideoTitle();
        getVideoTitle.execute();
    }

    private void setImages() {
        if( imageUrl != null ) {
            Glide.with(getApplicationContext()).load(imageUrl).into(mProfile_image);
            Glide.with(getApplicationContext()).load(imageUrl).into(toolbar_pic);
        }
        if( cover != null ){
            Glide.with(getApplicationContext()).load(cover).into(bgimage);
        }
    }

    private void setRating() {
        if(data.get("rating") != null) {
            rating_map = (Map) data.get("rating");
            assert rating_map != null;
            mRatingNum.setText(String.valueOf(rating_map.get("total_rating")));
        }
        else {
            Map map = new HashMap();
            Map map2 = new HashMap();
            map2.put("total_rating", 0);
            map.put("rating", map2);
            mRatingNum.setText(String.valueOf(0.0));
            //updateDB
            companyFS.update(map);
            rating_map = map;
        }
    }

    private void setNavigationBarButtonsColor(int navigationBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isColorLight(navigationBarColor)) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

    private void setCollapsingBar() {

        toolbar_pic.setBorderColor(Color.BLACK);

        collapsingToolbar.setTitle(name);
        final Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto_medium);
        collapsingToolbar.setCollapsedTitleTypeface(typeface);
        collapsingToolbar.setExpandedTitleTypeface(typeface);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbar.setContentScrimColor(getColor(R.color.CompanyProfileScrim));

        final ImageView imageView = bgimage;
        Glide.with(getApplicationContext()).asBitmap().load(cover).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                imageView.setImageBitmap(resource);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                int height = bitmap.getHeight();

                checkBottomLayoutColor(bitmap,height);
            }
            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {}
        });

    }

    private void checkBottomLayoutColor(Bitmap bitmap, int height) {

        int pixel_bottom_left = bitmap.getPixel(50, height - 50);
        int red_bottom = Color.red(pixel_bottom_left);
        int blue_bottom = Color.blue(pixel_bottom_left);
        int green_bottom = Color.green(pixel_bottom_left);

        if ((red_bottom < 115 && blue_bottom < 115) ||
                (green_bottom < 115 && red_bottom < 115) ||
                (green_bottom < 115 && blue_bottom < 115)){

            collapsingToolbar.setExpandedTitleColor(Color.WHITE);
            mProfile_image.setBorderColor(Color.WHITE);
        }
        else {
            collapsingToolbar.setExpandedTitleColor(Color.BLACK);
            mProfile_image.setBorderColor(Color.BLACK);
        }
    }

    private void updateRating() {

        raters = rating_map.size() - 1;

        if(rating_map.get("total_rating") != null)
            current_rating = Float.parseFloat((Objects.requireNonNull(rating_map.get("total_rating"))).toString());
        else
            current_rating = calculateRating(rating_map);

        ratingBar.setRating(current_rating);
        smallRating.setRating(current_rating);
        String text = "Rated by " + raters + " raters";
        mRaters.setText(text);

        if (rating_map.containsKey(customer_id)) {
            Object key = rating_map.get(customer_id);
            String msg = text + " | Your rate: " + key;
            mRaters.setText(msg);
        }
    }

    private Float calculateRating(Map map){
        Map map2 = (Map) map.get("rating");
        assert map2 != null;
        Set raters = map2.keySet();
        float rating = (float) 0;
        for(Object rater : raters){
            if(!rater.equals("total_rating") && !rater.equals("raters"))
                rating += Float.parseFloat((Objects.requireNonNull(map.get(rater))).toString());
        }
        return rating;
    }

    private void init() {

        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());

        youTubePlayerView = findViewById(R.id.youtube_video_company);
        mVideoTitle  = findViewById(R.id.company_video_title);
        mProfile_image = findViewById(R.id.profile_company_profile);
        mFacebook = findViewById(R.id.company_facebook);
        mTwitter = findViewById(R.id.company_twitter);
        mInstagram = findViewById(R.id.company_instagram);
        mYoutube = findViewById(R.id.company_youtube);
        mSite = findViewById(R.id.company_site);
        mRatingNum = findViewById(R.id.rating_num);

        ratingBar = findViewById(R.id.company_rating);
        smallRating = findViewById(R.id.toolbar_rating);
        mRaters = findViewById(R.id.company_raters_text);
        collapsingToolbar = findViewById(R.id.company_collapsing_toolbar);
        appBar = findViewById(R.id.company_appbar);
        mSlogan = findViewById(R.id.company_slogan);
        toolbar_pic = findViewById(R.id.company_toolbar_pic);
        mDescription = findViewById(R.id.description);
        bgimage = findViewById(R.id.company_bgImage);
        rating_map = new HashMap();

        Glide.with(this).load( Macros.TWITTER_IC ).into(mTwitter);
        Glide.with(this).load( Macros.FACEBOOK_IC ).into(mFacebook);
        Glide.with(this).load( Macros.WEB_IC ).into(mSite);
        Glide.with(this).load( Macros.INSTAGRAM_IC ).into(mInstagram);
        Glide.with(this).load( Macros.YOUTUBE_IC ).into(mYoutube);

        companyId = getIntent().getStringExtra("id");
        customer_id = getIntent().getStringExtra("customer_id");

        isCompany = companyId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        companyFS = FirebaseFirestore.getInstance().collection(Macros.COMPANIES).document(companyId);
    }

    private static class getVideoTitle extends AsyncTask<Void,Void,String> {

        String data = "";

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected String doInBackground(Void... voids) {
            try {
                return getTitle();
            }
            catch (Exception e ){
                Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
            }
            return null;
        }

        private String getTitle() {

            String title = "";
            try {
                URL url = new URL(Macros.YOUTUBE_API + "&id=" + youtube_link + "&key=" + Macros.API_KEY);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONObject JO = new JSONObject(data);
                JSONObject jsonObject = JO.getJSONArray("items").getJSONObject(0);
                JSONObject snippet = jsonObject.getJSONObject("snippet");
                title = snippet.getString("title");
            }
            catch (JSONException | IOException e) {
                e.printStackTrace();
                Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
            }
            return title;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setVideoTitle(s);
        }
    }

    private static void setVideoTitle(String title){
        mVideoTitle.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(player != null) {
            player.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(player != null) {
            player.pause();
        }

        Map map2 = new HashMap();
        map2.put("rating", rating_map);
        companyFS.set(map2, SetOptions.merge());
    }
}
