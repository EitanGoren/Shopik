package com.eitan.shopik;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eitan.shopik.Customer.Customer;
import com.eitan.shopik.Customer.GenderFilteringActivity;
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
import com.pushbots.push.Pushbots;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LandingPageActivity extends AppCompatActivity {

    private static String provider,token,email,imageUrl,id_in_provider;
    private static FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE;

        decorView.setSystemUiVisibility(uiOptions);
        // inside your activity (if you did not enable transitions in your theme)
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_landing_page);

        init();

        //FirebaseAuth.getInstance().signOut();
        if(!isConnectedToInternet()){
            RelativeLayout LandingLayout = findViewById(R.id.LandingLayout);
            Macros.Functions.showSnackbar (
                     LandingLayout,
                    "No Internet connection",
                    this,
                     R.drawable.ic_baseline_signal_cellular
            );
        }

        setNotifications();

        if(user == null){

            TashieLoader loader = findViewById(R.id.loader);
            TextView shopik = findViewById(R.id.shopik);
            ImageView tooki = findViewById(R.id.imageView);

            ActivityOptions options = ActivityOptions.
                    makeSceneTransitionAnimation(this,
                            Pair.create(tooki,"tooki"),
                            Pair.create(shopik,"Shopik")
                    );

            YoYo.with(Techniques.Tada).duration(2500).repeat(1).playOn(shopik);
            YoYo.with(Techniques.SlideInLeft).duration(2500).onEnd(
                    animator -> YoYo.with(Techniques.SlideOutRight).
                            duration(2500).playOn(tooki)).playOn(tooki);
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

            YoYo.with(Techniques.FadeOut).duration(5000).onEnd(animator -> {
                // Create and launch sign-in intent
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setAuthMethodPickerLayout(customLayout)
                        .setTheme(R.style.SignInStyle)
                        .setLogo(R.mipmap.ic_launcher_shopik)
                        .build(),1,options.toBundle());
            }).playOn(loader);

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
                else {
                    Log.d(Macros.TAG,"Failed with " + task.getException());
                }
            });
        }
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void registerNewUser() {

        Database connection = new Database();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String first_name = Objects.requireNonNull(user.getDisplayName()).split(" ")[0];
        String last_name = Objects.requireNonNull(user.getDisplayName()).split(" ")[1];
        Customer new_customer = new Customer(
                user.getUid(),
                first_name,
                last_name,
                null,
                user.getPhoneNumber(),
                0,
                0,
                "Unknown",
                null,
                null, imageUrl, provider, id_in_provider, email, token);

        connection.pushNewCustomer(new_customer);
        goToCustomer();
    }

    private void checkCustomer(final String user_id) {
        db.collection(Macros.CUSTOMERS).document(user_id).get().addOnCompleteListener(task -> {
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

    private void setNotifications(){

        // Initialize Pushbots Library
        new Pushbots.Builder(this)
                .setFcmAppId("1:260470087065:android:c689b76695ad0a240f4708")
                .setLogLevel(Pushbots.LOG_LEVEL.DEBUG)
                .setWebApiKey("AIzaSyA-NCcR_U7gBNm1BN3lXKTS8wC1W-VC9fE")
                .setPushbotsAppId("5f594a1cc47d3748d20b5214")
                .setProjectId("shopik-2448f")
                .setSenderId("260470087065")
                .build();

        //Register custom fields after user registered on PushBots
        Pushbots.sharedInstance().idsCallback((userId, registrationId) -> {
            if (registrationId != null && userId != null) {
                Log.d("PB3","Registration ID:" + registrationId + " | userId:" + userId);
                // Customer profile
                Pushbots.sharedInstance();
                Pushbots.setFirstName(Objects.requireNonNull(user.getDisplayName()).
                        split(" ")[0]);
                Pushbots.setLastName(user.getDisplayName().
                        split(" ")[1]);
                Pushbots.setName(user.getDisplayName());
                Pushbots.setEmail(user.getEmail());
            }
        });
    }

    private void goToCustomer() {

        TashieLoader loader = findViewById(R.id.loader);
        TextView shopik = findViewById(R.id.shopik);
        ImageView tooki = findViewById(R.id.imageView);

        Intent intent = new Intent(LandingPageActivity.this, GenderFilteringActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", imageUrl);
        bundle.putString("name", user.getDisplayName());
        intent.putExtra("bundle", bundle);

        ActivityOptions options = ActivityOptions.
                makeSceneTransitionAnimation(this,
                        Pair.create(tooki,"tooki"),
                        Pair.create(shopik,"Shopik")
                );

        YoYo.with(Techniques.Tada).duration(2500).repeat(1).playOn(shopik);
        YoYo.with(Techniques.RollIn).duration(3500).playOn(tooki);

        YoYo.with(Techniques.FadeOut).duration(5000).onEnd(animator -> {
            startActivity(intent, options.toBundle());
            supportFinishAfterTransition();
        }).playOn(loader);
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
                token = response.getIdpToken();
                user = FirebaseAuth.getInstance().getCurrentUser();
                email = new_user.getEmail();
                provider = new_user.getProviderId();

                switch (provider) {
                    case Macros.Providers.FACEBOOK:
                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response1) -> {
                            try {
                                id_in_provider = object.getString("id");
                                imageUrl = "http://graph.facebook.com/" + id_in_provider + "/picture?type=large&width=720&height=720";
                                checkCustomer(user.getUid());
                            }
                            catch (JSONException e) {
                                Log.d(Macros.TAG, "facebook failed: " + e.getMessage());
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name");
                        request.setParameters(parameters);
                        request.executeAsync();
                        break;
                    case Macros.Providers.GOOGLE:
                        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                        try {
                            if (acct != null) {
                                id_in_provider = acct.getId();
                                imageUrl = Objects.requireNonNull(acct.getPhotoUrl()).toString().split("=", 2)[0].concat("=s700-c");
                                checkCustomer(user.getUid());
                            }
                        } catch (Exception e) {
                            Log.d(Macros.TAG, "googleAuth failed: " + e.getMessage());
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
                Log.d(Macros.TAG, Objects.requireNonNull(Objects.requireNonNull(response.getError()).getMessage()));
                //and handle the error.
                Toast.makeText(this, "Something went wrong.. try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isConnectedToInternet(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                case ConnectivityManager.TYPE_MOBILE:
                    // connected to mobile data
                    // connected to wifi
                    return internetIsConnected();
                default:
                    return true;
            }
        }
        else {
            // not connected to the internet
            return false;
        }

    }

    public boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }
}