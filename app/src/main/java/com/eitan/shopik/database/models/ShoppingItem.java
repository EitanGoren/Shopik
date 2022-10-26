package com.eitan.shopik.database.models;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.eitan.shopik.PublicUser;
import com.eitan.shopik.database.Converters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Keep
@Entity(tableName = "shopik_items")
public class ShoppingItem implements Serializable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "images")
    private String images;

    @ColumnInfo(name = "interactedUsers")
    private String interactedUsers;

    @ColumnInfo(name = "likedUsersIds")
    private String likedUsersIds;

    @ColumnInfo(name = "unlikedUsersIds")
    private String unlikedUsersIds;

    @ColumnInfo(name = "on_sale")
    private boolean on_sale;

    @ColumnInfo(name = "percentage")
    private int percentage;

    @ColumnInfo(name = "brand")
    private String brand;

    @ColumnInfo(name = "price")
    private String price;

    @ColumnInfo(name = "seller")
    private String seller;

    @ColumnInfo(name = "seller_id")
    private String seller_id;

    @ColumnInfo(name = "seller_logo_url")
    private String seller_logo_url;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "site_link")
    private String site_link;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "reduced_price")
    private String reduced_price;

    @ColumnInfo(name = "isFavorite")
    private boolean isFavorite;

    @ColumnInfo(name = "isLiked")
    private boolean isLiked;

    @ColumnInfo(name = "isSeen")
    private boolean isSeen;

    public ShoppingItem(){
        this.interactedUsers = null;
        this.likedUsersIds = Converters.fromSet(new HashSet<>());
        this.unlikedUsersIds = Converters.fromSet(new HashSet<>());
        id = null;
    }

    public boolean isLiked() {
        return isLiked;
    }
    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
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

    public String getName(){
        return name;
    }
    public ArrayList<String> getConvertedName(){
        return Converters.fromString(name);
    }

    public void setName( String name ){
        this.name = name;
    }
    public void setConvertedName( ArrayList<String> name ){
        this.name = Converters.fromArrayList(name);
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

    public long getLikes() {
        return Converters.setFromString(likedUsersIds).size();
    }
    public long getUnlikes() {
        return Converters.setFromString(unlikedUsersIds).size();
    }

    public boolean isFavorite() {
        return isFavorite;
    }
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getInteractedUsers() {
        return interactedUsers;
    }
    public ArrayList<PublicUser> getConvertedInteractedUsers(){
        return Converters.publicUserListFromString(interactedUsers);
    }
    public void addInteractedUser(PublicUser publicUser){
        if(Converters.publicUserListFromString(interactedUsers) == null)
            interactedUsers = Converters.fromArrayListPublicUser(new ArrayList<>());

        ArrayList<PublicUser> list = Converters.publicUserListFromString(interactedUsers);
        list.add(publicUser);
        this.interactedUsers = Converters.fromArrayListPublicUser(list);
    }

    public String getReducedPrice() {
        return reduced_price;
    }
    public void setReducedPrice(String reduced_price) {
        this.reduced_price = reduced_price;
    }

    public String getImages() {
        return images;
    }
    public ArrayList<String> getConvertedImage(){
        return Converters.fromString(images);
    }
    public void setImages(String images) {
        this.images = images;
    }
    public void setConvertedImages(ArrayList<String> images) {
        this.images = Converters.fromArrayList(images);
    }

    public boolean isSeen() {
        return isSeen;
    }
    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getLikedUsersIds() {
        return likedUsersIds;
    }
    public String getUnlikedUsersIds() {
        return unlikedUsersIds;
    }

    public void setLikedUsersIds(String likedUsersIds) {
        this.likedUsersIds = likedUsersIds;
    }
    public void setUnlikedUsersIds(String unlikedUsersIds) {
        this.unlikedUsersIds = unlikedUsersIds;
    }

    public Set<String> getConvertedLikedUsersIds() {
        return Converters.setFromString(likedUsersIds);
    }
    public Set<String> getConvertedUnlikedUsersIds() {
        return Converters.setFromString(unlikedUsersIds);
    }

    public void addLikedUserId(String id){
        Set<String> set = Converters.setFromString(likedUsersIds);
        set.add(id);
        this.likedUsersIds = Converters.fromSet(set);
    }
    public void removeLikedUserId(String id){
        Converters.setFromString(likedUsersIds).remove(id);
    }

    public void addUnlikedUserId(String id){
        Set<String> set = Converters.setFromString(unlikedUsersIds);
        set.add(id);
        this.unlikedUsersIds = Converters.fromSet(set);
    }
    public void removeUnlikedUserId(String id){
        Converters.setFromString(unlikedUsersIds).remove(id);
    }

    public String getSeller_id() {
        return seller_id;
    }
    public String getSeller_logo_url() {
        return seller_logo_url;
    }

    public String getReduced_price() {
        return reduced_price;
    }

    public void setInteractedUsers(String interactedUsers) {
        this.interactedUsers = interactedUsers;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public void setSeller_logo_url(String seller_logo_url) {
        this.seller_logo_url = seller_logo_url;
    }

    public void setReduced_price(String reduced_price) {
        this.reduced_price = reduced_price;
    }

    public int getPercentage(){
        return percentage;
    }

    public void setPercentage(int percentage){
        this.percentage = percentage;
    }

    public boolean isOn_sale() {
        return on_sale;
    }

    public void setOn_sale(boolean on_sale) {
        this.on_sale = on_sale;
    }
}
