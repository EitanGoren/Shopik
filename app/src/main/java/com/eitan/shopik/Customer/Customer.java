package com.eitan.shopik.Customer;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer implements Serializable {

    private String id,age,phone,first_name,last_name,imageUrl,sex,address,
            city,provider_id,id_in_provider,email,token;

    private float total_spent;
    private long total_purchases;

    public Customer(){}

    public Customer(String id, String first_name, String last_name, String age, String phone, float total_spent ,long total_purchases,
                    String sex, String address, String city, String imageUrl,
                    String provider_id,String id_in_provider,String email,String token){

        this.id = id;
        this.first_name = first_name;
        this.imageUrl = imageUrl;
        this.city = city;
        this.sex = sex;
        this.address = address;
        this.age = age;
        this.phone = phone;
        this.last_name = last_name;
        this.total_purchases = total_purchases;
        this.total_spent = total_spent;
        this.email = email;
        this.provider_id = provider_id;
        this.id_in_provider = id_in_provider;
        this.token = token;
    }

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getFirst_name(){
        return first_name;
    }
    public void setFirst_name(String first_name){
        this.first_name = first_name;
    }

    public String getLast_name(){
        return last_name;
    }
    public void setLast_name(String last_name){
        this.last_name = last_name;
    }

    public String getAge(){
        return age;
    }
    public void setAge(String age){
        this.age = age;
    }

    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }

    public String getSex(){
        return sex;
    }
    public void setSex(String sex){
        this.sex = sex;
    }

    public String getCity(){
        return city;
    }
    public void setCity(String city){
        this.city = city;
    }

    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }

    public float getTotalSpent(){
        return total_spent;
    }
    public void setTotalSpent(float total_spent){
        this.total_spent = total_spent;
    }

    public long getTotalPurchases(){
        return total_purchases;
    }
    public void getTotalPurchases(long total_purchases){
        this.total_purchases = total_purchases;
    }

    public String getImageUrl(){
        return imageUrl;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
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
}
