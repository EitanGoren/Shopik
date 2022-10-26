package com.eitan.shopik.database.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopik_companies")
public class Company {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "logoUrl")
    private String logoUrl;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "facebook")
    private String facebook;

    @ColumnInfo(name = "twitter")
    private String twitter;

    @ColumnInfo(name = "youtube")
    private String youtube;

    @ColumnInfo(name = "instagram")
    private String instagram;

    @ColumnInfo(name = "site")
    private String site;

    @ColumnInfo(name = "cover")
    private String cover;

    @ColumnInfo(name = "description")
    private String description;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
    public void setLogoUrl(String imageUrl) {
        this.logoUrl = imageUrl;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getFacebook() {
        return facebook;
    }
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getTwitter() {
        return twitter;
    }
    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getYoutube() {
        return youtube;
    }
    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getInstagram() {
        return instagram;
    }
    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }

    public String getCover() {
        return cover;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
