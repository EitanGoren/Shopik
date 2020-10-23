package com.eitan.shopik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

import com.eitan.shopik.customer.GenderFilteringActivity;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Keep
public class LandingPageActivity extends AppCompatActivity {

    private static String provider,email,imageUrl,id_in_provider;
    private static FirebaseUser user;
    private FirebaseFirestore db;
    private TextView shopik;
    private ImageView tooki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postponeEnterTransition();
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        ShopikApplicationActivity.RefreshAds(5);
        setContentView(R.layout.activity_landing_page);

        //FirebaseAuth.getInstance().signOut();
        init();

        startPostponedEnterTransition();

        if(user == null){
            AuthenticateUser();
        }
        else {
            DocumentReference user_document = db.collection(Macros.CUSTOMERS).document(user.getUid());

            user_document.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        if(document.get("imageUrl") != null) {
                            imageUrl = Objects.requireNonNull(document.get("imageUrl")).toString();
                        }
                        goToApp();
                    }
                    else {
                        registerNewUser();
                    }
                }
            });
        }

    }

    private void AuthenticateUser() {

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());
        // You must provide a custom layout XML resource and configure at least one
        // provider button ID. It's important that that you set the button ID for every provider
        // that you have enabled.
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
                .setGoogleButtonId(R.id.google_sign_in)
                .setFacebookButtonId(R.id.facebook_sign_in)
                .setEmailButtonId(R.id.email_sign_in)
                .build();

        // Create and launch sign-in intent
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setAuthMethodPickerLayout(customLayout)
                .setTheme(R.style.SignInStyle)
                .setLogo(R.mipmap.ic_launcher_shopik)
                .build(),1);
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        shopik = findViewById(R.id.shopik);
    }

    private void registerNewUser() {

        String userId = user.getUid();
        String first_name = Objects.requireNonNull(user.getDisplayName()).split(" ")[0];
        String last_name = Objects.requireNonNull(user.getDisplayName()).split(" ")[1];

        Map<String, String> map = new HashMap<>();
        map.put("email",email);
        map.put("id",userId);
        map.put("provider",provider);
        map.put("imageUrl",imageUrl);
        map.put("first_name",first_name);
        map.put("last_name",last_name);
        map.put("age",null);
        map.put("city",null);
        map.put("id_in_provider",id_in_provider);
        map.put("gender",null);
        map.put("address",null);

        FirebaseFirestore.getInstance().collection(Macros.CUSTOMERS).
                document(userId).
                set(map).
                addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show()).
                addOnCompleteListener(task -> goToCustomer());
    }

    private void checkCustomer(final String user_id) {
        db.collection(Macros.CUSTOMERS).document(user_id).get().
                addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                    goToApp();
                }
                else
                    registerNewUser();
            }
        });
    }

    private void goToApp(){
        if(user == null)
           registerNewUser();
        else
            goToCustomer();
    }

    private void goToCustomer() {

        Intent intent = new Intent(LandingPageActivity.this, GenderFilteringActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("name", user.getDisplayName());
        intent.putExtra("bundle", bundle);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(intent);
            supportFinishAfterTransition();
        },1000 * 4 );
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            assert response != null;
            if (resultCode == RESULT_OK) {

                // Successfully signed in
                User new_user = response.getUser();
                user = FirebaseAuth.getInstance().getCurrentUser();
                email = new_user.getEmail();
                provider = new_user.getProviderId();

                switch (provider) {
                    case Macros.Providers.FACEBOOK:
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest( accessToken, (object, response1) -> {
                            try {
                                id_in_provider = object.getString("id");
                                imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                checkCustomer(user.getUid());
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "picture.type(large),id");
                        request.setParameters(parameters);
                        request.executeAsync();
                       break;
                    case Macros.Providers.GOOGLE:
                        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        try {
                            if (acct != null) {
                                id_in_provider = acct.getId();
                                imageUrl = Objects.requireNonNull(acct.getPhotoUrl()).toString().
                                        split("=", 2)[0].concat("=s700-c");
                                checkCustomer(user.getUid());
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case Macros.Providers.PASSWORD:
                        checkCustomer(user.getUid());
                        break;
                }
            }
            else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                //and handle the error.
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Something went wrong.. " + resultCode, Toast.LENGTH_SHORT).show();
                AuthenticateUser();
            }
        }
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

}