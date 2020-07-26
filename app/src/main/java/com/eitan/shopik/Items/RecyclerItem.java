package com.eitan.shopik.Items;

import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.io.Serializable;
import java.util.ArrayList;

public class RecyclerItem implements Serializable {

    private String link,text,type,gender,userImageUrl,item_sub_category,
            price,reduced_price,id,video_link,sellerImageUrl,brand,seller,seller_id;
    private ArrayList<String> images;
    private ArrayList<String> description;
    private long likes;
    private boolean isOutlet,isVideo,isSale,isAd;
    private String image_resource;
    private transient UnifiedNativeAd nativeAd;

    public RecyclerItem(String text, String link){
        this.text = text;
        this.link = link;
    }

    public String getSeller() {
        return seller;
    }
    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public long getLikes() {
        return likes;
    }
    public void setLikes(long likes) {
        this.likes = likes;
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    public ArrayList<String> getImages() {
        return images;
    }
    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public void setImage_resource(String image_resource) {
        this.image_resource = image_resource;
    }
    public String getImage_resource() {
        return image_resource;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }
    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getItem_sub_category() {
        return item_sub_category;
    }
    public void setItem_sub_category(String item_sub_category) {
        this.item_sub_category = item_sub_category;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getReduced_price() {
        return reduced_price;
    }
    public void setReduced_price(String reduced_price) {
        this.reduced_price = reduced_price;
    }

    public boolean isOutlet() {
        return isOutlet;
    }
    public void setOutlet(boolean outlet) {
        isOutlet = outlet;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getDescription() {
        return description;
    }
    public void setDescription(ArrayList<String> description) {
        this.description = description;
    }

    public boolean isVideo() {
        return isVideo;
    }
    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getVideo_link() {
        return video_link;
    }
    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public boolean isSale() {
        return isSale;
    }
    public void setSale(boolean sale) {
        isSale = sale;
    }

    public String getSellerLogoUrl() {
        return sellerImageUrl;
    }
    public void setSellerImageUrl(String sellerImageUrl) {
        this.sellerImageUrl = sellerImageUrl;
    }

    public boolean isAd() {
        return isAd;
    }
    public void setAd(boolean ad) {
        isAd = ad;
    }

    public void setNativeAd(UnifiedNativeAd nativeAd) {
        this.nativeAd = nativeAd;
    }
    public UnifiedNativeAd getNativeAd() {
        return nativeAd;
    }

    public String getSeller_id() {
        return seller_id;
    }
    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }
}
