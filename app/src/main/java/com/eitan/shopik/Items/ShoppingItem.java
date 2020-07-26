package com.eitan.shopik.Items;

import com.eitan.shopik.LikedUser;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.io.Serializable;
import java.util.ArrayList;

public class ShoppingItem implements Serializable {

    private String id,color,type,brand,price,seller,discount,seller_id,seller_logo_url,
                   site_link,gender,material,id_in_seller,reduced_price,video_link,style,
                   fit,cut,sub_category;

    private ArrayList<String> name,images;
    private ArrayList<LikedUser> likedUsers,unlikedUsers;

    private boolean on_sale,isAd,isFavorite,isOutlet,isExclusive,isDummyLastItem;
    private int percentage,page_num,catagory_num;
    private long likes,unlikes;
    private transient UnifiedNativeAd nativeAd;

    public ShoppingItem(){}

    ShoppingItem(String id, String color, String type, String brand, String price, ArrayList<String> name,
                 String seller, boolean on_sale, String discount, String seller_id,
                 String seller_logo, String site_link, String gender, String material, String id_in_seller,
                 long likes, long unlikes, ArrayList<LikedUser> likedUsers,ArrayList<LikedUser> unlikedUsers,
                 String style,String fit, String cut) {

        this.setAd(false);
        this.setLikes(0);
        this.setUnlikes(0);
        this.setLikedUsers(null);
        this.setUnlikedUsers(null);
        this.isDummyLastItem = false;
        this.id = id;
        this.brand = brand;
        this.color = color;
        this.seller = seller;
        this.price = price;
        this.type = type;
        this.name = name;
        this.on_sale = on_sale;
        this.discount = discount;
        this.seller_id = seller_id;
        this.seller_logo_url = seller_logo;
        this.site_link = site_link;
        this.gender = gender;
        this.material = material;
        this.id_in_seller = id_in_seller;
        this.likes = likes;
        this.unlikes = unlikes;
        this.likedUsers = likedUsers;
        this.unlikedUsers = unlikedUsers;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getColor(){
        return color;
    }
    public void setColor(String color){
        this.color = color;
    }

    public String getType(){
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String getBrand(){
        return brand;
    }
    public void setBrand(String brand){
        this.brand = brand;
    }

    public String getPrice(){
        return price;
    }
    public void setPrice(String price){
        this.price = price;
    }

    public String getSeller(){
        return seller;
    }
    public void setSeller(String seller){
        this.seller = seller;
    }

    public ArrayList<String> getName(){
        return name;
    }
    public void setName( ArrayList<String> name ){
        this.name = name;
    }

    public boolean isOn_sale() {
        return on_sale;
    }
    public void setOn_sale(boolean on_sale) {
        this.on_sale = on_sale;
    }

    public String getDiscount() {
        return discount;
    }
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSellerId() {
        return seller_id;
    }
    public void setSellerId(String seller_id) {
        this.seller_id = seller_id;
    }

    public void setSellerLogoUrl(String seller_logo_url) {
        this.seller_logo_url = seller_logo_url;
    }
    public String getSellerLogoUrl() {
        return seller_logo_url;
    }

    public int getPercentage() {
        return percentage;
    }
    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getSite_link() {
        return site_link;
    }
    public void setSite_link(String site_link) {
        this.site_link = site_link;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaterial() {
        return material;
    }
    public void setMaterial(String material) {
        this.material = material;
    }

    public String getId_in_seller() {
        return id_in_seller;
    }
    public void setId_in_seller(String id_in_seller) {
        this.id_in_seller = id_in_seller;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }
    public long getLikes() {
        return likes;
    }

    public void setUnlikes(long unlikes) {
        this.unlikes = unlikes;
    }
    public long getUnlikes() {
        return unlikes;
    }

    public boolean isAd() {
        return isAd;
    }
    public void setAd(boolean ad) {
        isAd = ad;
    }

    public void setNativeAd(UnifiedNativeAd ad) {
        this.nativeAd = ad;
    }
    public UnifiedNativeAd getNativeAd() {
        return nativeAd;
    }
    public void destroyAd(){
        this.nativeAd.destroy();
    }

    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getCatagory_num() {
        return catagory_num;
    }
    public void setCatagory_num(int catagory_num) {
        this.catagory_num = catagory_num;
    }

    public int getPage_num() {
        return page_num;
    }
    public void setPage_num(int page_num) {
        this.page_num = page_num;
    }

    public ArrayList<LikedUser> getLikedUsers() {
        return likedUsers;
    }
    public void setLikedUsers(ArrayList<LikedUser> likedUsers) {
        this.likedUsers = likedUsers;
    }
    public void addLikedUser(LikedUser likedUser){
        if(this.likedUsers == null)
            this.likedUsers = new ArrayList<>();

        this.likedUsers.add(likedUser);
    }

    public ArrayList<LikedUser> getUnlikedUsers() {
        return unlikedUsers;
    }
    public void setUnlikedUsers(ArrayList<LikedUser> unlikedUsers) {
        this.unlikedUsers = unlikedUsers;
    }
    public void addUnlikedUser(LikedUser unlikedUser){
        if(this.unlikedUsers == null)
            this.unlikedUsers = new ArrayList<>();

        this.unlikedUsers.add(unlikedUser);
    }

    public boolean isOutlet() {
        return isOutlet;
    }
    public void setOutlet(boolean outlet) {
        isOutlet = outlet;
    }

    public String getReduced_price() {
        return reduced_price;
    }
    public void setReduced_price(String reduced_price) {
        this.reduced_price = reduced_price;
    }

    public String getVideo_link() {
        return video_link;
    }
    public void setVideo_link(String video_link) {
        this.video_link = video_link;
    }

    public ArrayList<String> getImages() {
        return images;
    }
    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public String getStyle() {
        return style;
    }
    public void setStyle(String style) {
        this.style = style;
    }

    public String getFit() {
        return fit;
    }
    public void setFit(String fit) {
        this.fit = fit;
    }

    public String getCut() {
        return cut;
    }
    public void setCut(String cut) {
        this.cut = cut;
    }

    public boolean isExclusive() {
        return isExclusive;
    }
    public void setExclusive(boolean exclusive) {
        isExclusive = exclusive;
    }

    public String getSub_category() {
        return sub_category;
    }
    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public boolean isDummyLastItem() {
        return isDummyLastItem;
    }
    public void setDummyLastItem(boolean dummyLastItem) {
        isDummyLastItem = dummyLastItem;
    }
}
