package com.eitan.shopik.Company;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.eitan.shopik.CompanyFragments.NewItemFragment;
import com.eitan.shopik.LandingPageActivity;
import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyMainActivity extends AppCompatActivity {

    private DocumentReference companiesFS;
    String companyId,imageUrl,name,cover;
    private Uri resultUri;
    FirebaseAuth mAuth;
    TextView mName;
    CircleImageView mImage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == -1){
            assert data != null;
            resultUri = data.getData();
            mImage.setImageURI(resultUri);
            if(resultUri != null){
                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("CompanyLogo").child(companyId);
                Bitmap bitmap = null;
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                assert bitmap != null;
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50,baos);

                Glide.with(this).load(bitmap).into(mImage);

                byte[] data2 = baos.toByteArray();

                UploadTask uploadTask = filePath.putBytes(data2);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        return;
                    }
                });


                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map newImage = new HashMap();
                                newImage.put("logo_url", uri.toString());
                               // mCompanyDB.updateChildren(newImage);
                                companiesFS.set(newImage);

                            }
                        }).addOnFailureListener(new OnFailureListener(){
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                            }
                        });
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        init();

        companiesFS.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                imageUrl = documentSnapshot.get("logo_url")!=null ? Objects.requireNonNull(documentSnapshot.get("logo_url")).toString() : null;
                name = documentSnapshot.get("name") != null ? Objects.requireNonNull(documentSnapshot.get("name")).toString() : null;
                cover = documentSnapshot.get("cover_image_url") != null ? Objects.requireNonNull(documentSnapshot.get("cover_image_url")).toString() : null;
            }
        }).addOnCompleteListener(task -> {
            Glide.with(CompanyMainActivity.this).load(imageUrl).into(mImage);
            mName.setText(name);
        });

        final BottomNavigationView bottomNav = findViewById(R.id.company_bottom_nav);

        BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_my_profile:
                    Intent intent = new Intent(CompanyMainActivity.this, CompanyProfileActivity.class);
                    intent.putExtra("id",companyId);
                    intent.putExtra("customer_id", companyId);
                    startActivity(intent);
                    //selectedFragment = new MyProfileFragment().newInstance(companyId);
                    break;
                case R.id.nav_all_items:
                  //  selectedFragment = new com.eitan.shopik.CompanyFragments.AllItemsFragment();
                    break;
                case R.id.nav_sold_items:
                 //   selectedFragment = new MyProfileFragment().newInstance(companyId);
                    break;
                case R.id.nav_messages:
                 //   selectedFragment = new MyProfileFragment().newInstance(companyId);
                    break;
                default:
                 //   selectedFragment = new MyProfileFragment().newInstance(companyId);
                    break;
            }
          //  getSupportFragmentManager().beginTransaction().replace(R.id.company_cards_container, selectedFragment).addToBackStack(null).commit();
            return true;
        };
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        mImage.setOnClickListener(v -> changeLogo());

        final androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.company_toolbar);
        androidx.appcompat.widget.Toolbar.OnMenuItemClickListener topNavListener = item -> {
            Fragment selectedFragment = null;
            Intent intent = null;
            switch (item.getTitle().toString()) {
                case "Company Profile":
                    intent = new Intent(CompanyMainActivity.this, CompanyProfileActivity.class);
                    intent.putExtra("id",companyId);
                    intent.putExtra("customer_id", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    startActivity(intent);
                    break;
                case "New Item":
                    selectedFragment = new NewItemFragment();
                    break;
                case "Analytics":
                    intent = new Intent(CompanyMainActivity.this, CompanyProfileActivity.class);
                    intent.putExtra("id",companyId);
                    intent.putExtra("customer_id", companyId);
                    startActivity(intent);
                case "Log Out":
                    // OneSignal.setSubscription(false);
                   mAuth.signOut();
                    //TODO SIGN OUT FROM SMART LOCK
                    if(mAuth.getCurrentUser() == null ) {
                        intent = new Intent(CompanyMainActivity.this, LandingPageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    return true;
                default:
                    break;
            }
            if(selectedFragment!=null)
                getSupportFragmentManager().beginTransaction().replace(R.id.company_cards_container, selectedFragment).addToBackStack(null).commit();

            return true;
        };
        toolbar.setOnMenuItemClickListener(topNavListener);
        Objects.requireNonNull(toolbar.getOverflowIcon()).setTint(ContextCompat.getColor(this, R.color.colorWhite));
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mImage = findViewById(R.id.company_logo);
        companyId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mName = findViewById(R.id.company_name);
        companiesFS = FirebaseFirestore.getInstance().collection(Macros.COMPANIES).document(companyId);
    }

    private void changeLogo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
        if(resultUri != null){
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("CompanyLogo").child(companyId);
            Bitmap bitmap = null;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            }
            catch (IOException e){
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            assert bitmap != null;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50,baos);

            Glide.with(this).load(bitmap).into(mImage);

            byte[] data = baos.toByteArray();

            UploadTask uploadTask = filePath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    return;
                }
            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("logo_url", uri.toString());
                           // mCompanyDB.updateChildren(newImage);
                            companiesFS.set(newImage);

                        }
                    }).addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
                }
            });
        }
    }

}
