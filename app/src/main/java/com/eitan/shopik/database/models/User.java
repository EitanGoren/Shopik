package com.eitan.shopik.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopik_users")
public class User {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "first_name")
    private String mFirstName;

    @ColumnInfo(name = "last_name")
    private String mLastName;

    @ColumnInfo(name = "address")
    private String mAddress;

    @ColumnInfo(name = "age")
    private String mAge;

    @ColumnInfo(name = "entries")
    private String mAppEntries;

    @ColumnInfo(name = "city")
    private String mCity;

    @ColumnInfo(name = "email")
    private String mEmail;

    @ColumnInfo(name = "gender")
    private String mGender;

    @ColumnInfo(name = "id_in_provider")
    private String mIdInProvider;

    @ColumnInfo(name = "image_url")
    private String mImageUrl;

    @ColumnInfo(name = "phone")
    private String mPhone;

    @ColumnInfo(name = "provider")
    private String mProvider;

    @ColumnInfo(name = "cover_photo")
    private String mCoverPhoto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String firstName) {
        this.mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        this.mLastName = lastName;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getAge() {
        return mAge;
    }

    public void setAge(String mAge) {
        this.mAge = mAge;
    }

    public String getAppEntries() {
        return mAppEntries;
    }

    public void setAppEntries(String mAppEntries) {
        this.mAppEntries = mAppEntries;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String mGender) {
        this.mGender = mGender;
    }

    public String getIdInProvider() {
        return mIdInProvider;
    }

    public void setIdInProvider(String mIdInProvider) {
        this.mIdInProvider = mIdInProvider;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getProvider() {
        return mProvider;
    }

    public void setProvider(String mProvider) {
        this.mProvider = mProvider;
    }

    public String getCoverPhoto() {
        return mCoverPhoto;
    }

    public void setCoverPhoto(String mCoverPhoto) {
        this.mCoverPhoto = mCoverPhoto;
    }
}

