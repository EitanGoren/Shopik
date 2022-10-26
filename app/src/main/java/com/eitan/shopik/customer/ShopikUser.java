package com.eitan.shopik.customer;

import static com.eitan.shopik.Macros.DEFAULT_COVER_PHOTO;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.eitan.shopik.Macros;
import com.eitan.shopik.R;
import com.eitan.shopik.database.Database;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ShopikUser implements Serializable {

    private static volatile ShopikUser mInstance = null;

    private FirebaseUser mUser;
    private String mAddress;
    private String mAge, mAppEntries, mCity, mEmail, mFirstName, mGender, mId, mIdInProvider,
        mImageUrl, mLastName, mPhone, mProvider, mCoverPhoto;

    private static final String WOMEN = "Women";
    private static final String MEN = "Men";

    private ShopikUser(){
        signIn();
        EventBus.getDefault().register(this);
    }

    public static ShopikUser getInstance() {
        if (mInstance == null) {
            // To make thread safe
            synchronized (ShopikUser.class) {
                // check again as multiple threads
                // can reach above step
                if (mInstance == null)
                    mInstance = new ShopikUser();
            }
        }
        return mInstance;
    }

    public boolean isAuthenticated() {
        return mUser != null;
    }

    public void signIn() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mUser != null){
            mFirstName = mFirstName != null ? mFirstName : Objects.requireNonNull(mUser.getDisplayName()).split(" ")[0];
            mLastName = mLastName != null ? mLastName : Objects.requireNonNull(mUser.getDisplayName()).split(" ")[1];
            mEmail = mEmail != null ? mEmail : mUser.getEmail();
            mPhone = mPhone != null ? mPhone : mUser.getPhoneNumber();
            mId = mId != null ? mId : mUser.getUid();
            mProvider = mProvider != null ? mProvider : mUser.getProviderId();
            mImageUrl = mImageUrl != null ? mImageUrl : mUser.getPhotoUrl() != null ? mUser.getPhotoUrl().toString() : Macros.DEFAULT_PROFILE_IMAGE;
            mCity = mCity != null ? mCity : null;
            mGender = mGender != null ? mGender : WOMEN;
            mAddress = mAddress != null ? mAddress : null;
            mIdInProvider = mIdInProvider != null ? mIdInProvider : null;
            mAge = mAge != null ? mAge : null;
            mAppEntries = mAppEntries != null ? mAppEntries : null;
            mCoverPhoto = mCoverPhoto != null ? mCoverPhoto : DEFAULT_COVER_PHOTO;
        }
    }
    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        mUser = null;
        mFirstName = null;
        mLastName = null;
        mEmail = null;
        mPhone = null;
        mProvider = null;
        mImageUrl = null;
        mCity = null;
        mGender = null;
        mIdInProvider = null;
        mAge = null;
        mAppEntries = null;
        mCoverPhoto = null;
    }
    public void AuthenticateUser(ActivityResultLauncher<Intent> signInLauncher) {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_auth)
                .setGoogleButtonId(R.id.google_sign_in)
                .setFacebookButtonId(R.id.facebook_sign_in)
                .setEmailButtonId(R.id.email_sign_in)
                .build();

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setAuthMethodPickerLayout(customLayout)
                .setTheme(R.style.SignInStyle)
                .setLogo(R.mipmap.ic_launcher_shopik)
                .build();

        signInLauncher.launch(signInIntent);
    }

    public String getAddress() {
        return mAddress;
    }
    public String getAge() {
        return mAge;
    }
    public String getAppEntries() {
        return mAppEntries;
    }
    public String getCity() {
        return mCity;
    }
    public String getEmail() {
        return mEmail;
    }
    public String getFirstName() {
        return mFirstName;
    }
    public String getGender() {
        return mGender;
    }
    public String getId() {
        return mId;
    }
    public String getIdInProvider() {
        return mIdInProvider;
    }
    public String getImageUrl() {
        return mImageUrl;
    }
    public String getLastName() {
        return mLastName;
    }
    public String getPhone() {
        return mPhone;
    }
    public String getProvider() {
        return mProvider;
    }
    public String getCoverPhoto() {
        return mCoverPhoto;
    }

    public void setCoverPhoto(String coverPhoto) {
        this.mCoverPhoto = coverPhoto;
    }
    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }
    public void setAge(String mAge) {
        this.mAge = mAge;
    }
    public void setAppEntries(String mAppEntries) {
        this.mAppEntries = mAppEntries;
    }
    public void setCity(String mCity) {
        this.mCity = mCity;
    }
    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }
    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }
    public void setGender(String mGender) {
        this.mGender = mGender;
    }
    public void setId(String mId) {
        this.mId = mId;
    }
    public void setIdInProvider(String mIdInProvider) {
        this.mIdInProvider = mIdInProvider;
    }
    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }
    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }
    public void setProvider(String mProvider) {
        this.mProvider = mProvider;
    }

    @Subscribe
    public void onUserDataChanged(Database.UserDataChangedEvent event){
        mFirstName = event.userData.get("first_name") != null ? Objects.requireNonNull(event.userData.get("first_name")).toString() : null;
        mLastName = event.userData.get("last_name") != null ? Objects.requireNonNull(event.userData.get("last_name")).toString() : null;
        mEmail = event.userData.get("email") != null ? Objects.requireNonNull(event.userData.get("email")).toString() : null;
        mPhone = event.userData.get("phone") != null ? Objects.requireNonNull(event.userData.get("phone")).toString() : null;
        mProvider = event.userData.get("provider") != null ? Objects.requireNonNull(event.userData.get("provider")).toString() : null;
        mImageUrl = event.userData.get("imageUrl") != null ? Objects.requireNonNull(event.userData.get("imageUrl")).toString() : mImageUrl;
        mCity = event.userData.get("city") != null ? Objects.requireNonNull(event.userData.get("city")).toString() : null;
        mGender = event.userData.get("gender") != null ? Objects.requireNonNull(event.userData.get("gender")).toString() : mGender;
        mAddress = event.userData.get("address") != null ? Objects.requireNonNull(event.userData.get("address")).toString() : null;
        mIdInProvider = event.userData.get("id_in_provider") != null ? Objects.requireNonNull(event.userData.get("id_in_provider")).toString() : null;
        mAge = event.userData.get("age") != null ? Objects.requireNonNull(event.userData.get("age")).toString() : null;
        mAppEntries = event.userData.get("app_entries") != null ? Objects.requireNonNull(event.userData.get("app_entries")).toString() : null;
        mCoverPhoto = event.userData.get("cover_photo") != null ? Objects.requireNonNull(event.userData.get("cover_photo")).toString() : DEFAULT_COVER_PHOTO;;
        mId = event.userData.get("id") != null ? Objects.requireNonNull(event.userData.get("id")).toString() : mId;
    }
}
