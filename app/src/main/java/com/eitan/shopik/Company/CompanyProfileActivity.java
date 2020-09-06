package com.eitan.shopik.Company;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyProfileActivity extends AppCompatActivity {

    private DocumentReference companyFS;
    private String companyId,imageUrl,name,facebook,twitter,youtube,instagram,site,
                    cover,description;
    private CircleImageView mProfile_image,toolbar_pic;
    private ImageView mFacebook,mTwitter,mSite,mInstagram,mYoutube,bgimage;
    private TextView mDescription;
    private Map<String, Object> data;
    private boolean isCompany;
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBar;

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
                        Log.d(Macros.TAG, Objects.requireNonNull(e.getMessage()));
                    }

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);

                    Glide.with(getApplicationContext()).load(bitmap).into(mProfile_image);
                    Glide.with(getApplicationContext()).load(bitmap).into(toolbar_pic);

                    byte[] data2 = baos.toByteArray();

                    UploadTask uploadTask = filePath.putBytes(data2);
                    uploadTask.addOnFailureListener(e -> Log.d(Macros.TAG, "failed to load company picture:" + e.getMessage()));

                    uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("logo_url", uri.toString());
                        imageUrl = uri.toString();
                        companyFS.update(userInfo);
                    }).addOnFailureListener(exception -> Log.d(Macros.TAG, "failed to load company picture:" + exception.getMessage())));

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
                    uploadTask.addOnFailureListener(e -> Log.d(Macros.TAG, "failed to load company picture:" + e.getMessage()));

                    uploadTask.addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("cover_image_url", uri.toString());
                        cover = uri.toString();
                        companyFS.update(userInfo);
                    }).addOnFailureListener(exception -> Log.d(Macros.TAG, "failed to load company picture:" + exception.getMessage())));
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
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

            mDescription.setText(description);

            setCollapsingBar();
            setImages();

            startPostponedEnterTransition();
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
            if(verticalOffset <= -470) {
                toolbar_pic.setVisibility(View.VISIBLE);
                mProfile_image.setVisibility(View.INVISIBLE);
            }
            else {
                toolbar_pic.setVisibility(View.INVISIBLE);
                mProfile_image.setVisibility(View.VISIBLE);
            }
        });
        mSite.setOnClickListener(v -> {
            if(site == null) return;
            if (!site.startsWith("http://") && !site.startsWith("https://"))
                site = "http://" + site;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(site));
            startActivity(browserIntent);
        });
        mFacebook.setOnClickListener(v -> {
            if(facebook == null) return;
            if (!facebook.startsWith("http://") && !facebook.startsWith("https://"))
                facebook = "http://" + facebook;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook));
            startActivity(browserIntent);
        });
        mTwitter.setOnClickListener(v -> {
            if(twitter == null) return;
            if (!twitter.startsWith("http://") && !twitter.startsWith("https://"))
                twitter = "http://" + twitter;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter));
            startActivity(browserIntent);
        });
        mInstagram.setOnClickListener(v -> {
            if(instagram == null) return;
            if (!facebook.startsWith("http://") && !instagram.startsWith("https://"))
                instagram = "http://" + instagram;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instagram));
            startActivity(browserIntent);
        });
        mYoutube.setOnClickListener(v -> {
            if(youtube == null) return;
            if (!youtube.startsWith("http://") && !youtube.startsWith("https://"))
                youtube = "http://" + youtube;
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube));
            startActivity(browserIntent);
        });
    }

    private void setImages() {
        if( imageUrl != null ) {
            Glide.with(getApplicationContext()).load(imageUrl).into(mProfile_image);
            Glide.with(getApplicationContext()).load(imageUrl).into(toolbar_pic);
        }
        if( cover != null ){
            Macros.Functions.GlidePicture(getApplicationContext(),cover,bgimage);
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

        toolbar_pic.setBorderColor(Color.WHITE);

        collapsingToolbar.setTitle(name);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        final Typeface typeface = ResourcesCompat.getFont(this, R.font.roboto_medium);
        collapsingToolbar.setCollapsedTitleTypeface(typeface);
        collapsingToolbar.setExpandedTitleTypeface(typeface);
        collapsingToolbar.setContentScrimColor(getColor(R.color.CompanyProfileScrim));

        Glide.with(getApplicationContext()).asBitmap().load(cover).into(bgimage);
    }

    private void init() {

        setNavigationBarButtonsColor(getWindow().getNavigationBarColor());

        mProfile_image = findViewById(R.id.profile_company_profile);
        mFacebook = findViewById(R.id.company_facebook);
        mTwitter = findViewById(R.id.company_twitter);
        mInstagram = findViewById(R.id.company_instagram);
        mYoutube = findViewById(R.id.company_youtube);
        mSite = findViewById(R.id.company_site);

        collapsingToolbar = findViewById(R.id.company_collapsing_toolbar);
        appBar = findViewById(R.id.company_appbar);
        toolbar_pic = findViewById(R.id.company_toolbar_pic);
        mDescription = findViewById(R.id.description);
        bgimage = findViewById(R.id.company_bgImage);

        Glide.with(this).load( Macros.TWITTER_IC ).into(mTwitter);
        Glide.with(this).load( Macros.FACEBOOK_IC ).into(mFacebook);
        Glide.with(this).load( Macros.WEB_IC ).into(mSite);
        Glide.with(this).load( Macros.INSTAGRAM_IC ).into(mInstagram);
        Glide.with(this).load( Macros.YOUTUBE_IC ).into(mYoutube);

        companyId = getIntent().getStringExtra("id");

        assert companyId != null;
        isCompany = companyId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        companyFS = FirebaseFirestore.getInstance().collection(Macros.COMPANIES).document(companyId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.supportFinishAfterTransition();
    }
}
