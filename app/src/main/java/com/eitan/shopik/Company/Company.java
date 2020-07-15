package com.eitan.shopik.Company;

import java.io.Serializable;

public class Company implements Serializable {

    private String id,name,number,logo_url,provider_id,id_in_provider,
            email,token,facebook_link,twitter_link,site_link,
            cover_image_url,description,youtube_link,instagram_link,yt_link;

    private long total_sales,total_items;

    public Company(){};
    public Company(String id, String name, String number, long total_sales, String logo_url, long total_items,
                     String provider_id,String id_in_provider,String email,String token,String description,
                   String youtube_link,String instagram_link,String yt_link){

        this.id = id;
        this.logo_url = logo_url;
        this.name = name;
        this.number = number;
        this.total_items = total_items;
        this.total_sales = total_sales;
        this.email = email;
        this.provider_id = provider_id;
        this.id_in_provider = id_in_provider;
        this.token = token;
        this.description = description;
        this.youtube_link = youtube_link;
        this.yt_link = yt_link;
        this.instagram_link = instagram_link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getTotal_items() {
        return total_items;
    }

    public long getTotal_sales() {
        return total_sales;
    }

    public void setTotal_sales(long total_sales) {
        this.total_sales = total_sales;
    }

    public void setTotal_items(long total_items) {
        this.total_items = total_items;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId_in_provider() {
        return id_in_provider;
    }

    public void setId_in_provider(String id_in_provider) {
        this.id_in_provider = id_in_provider;
    }

    public String getProvider_id() {
        return provider_id;
    }

    public void setProvider_id(String provider_id) {
        this.provider_id = provider_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSite_link(String site_link) {
        this.site_link = site_link;
    }

    public String getSite_link() {
        return site_link;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }

    public void setFacebook_link(String facebook_link) {
        this.facebook_link = facebook_link;
    }

    public String getFacebook_link() {
        return facebook_link;
    }

    public String getTwitter_link() {
        return twitter_link;
    }

    public void setTwitter_link(String twitter_link) {
        this.twitter_link = twitter_link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYoutube_link() {
        return youtube_link;
    }

    public void setYoutube_link(String youtube_link) {
        this.youtube_link = youtube_link;
    }

    public String getInstagram_link() {
        return instagram_link;
    }

    public String getYt_link() {
        return yt_link;
    }

    public void setInstagram_link(String instagram_link) {
        this.instagram_link = instagram_link;
    }

    public void setYt_link(String yt_link) {
        this.yt_link = yt_link;
    }
}
