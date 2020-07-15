package com.eitan.shopik.Items;

import java.io.Serializable;

public class SubCategory implements Serializable {

    private String name,gender;
    private String resource;

    public SubCategory(String name, String resource, String gender){
        this.resource = resource;
        this.name = name;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}