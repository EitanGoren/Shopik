package com.eitan.shopik.database;

import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import com.eitan.shopik.PublicUser;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Set;

public class Converters {

    @TypeConverter
    public static ArrayList<String> fromString(String value) {
        Type listType = new TypeToken<ArrayList<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static ArrayList<PublicUser> publicUserListFromString(String value) {
        Type listType = new TypeToken<ArrayList<PublicUser>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayListPublicUser(ArrayList<PublicUser> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static Set<String> setFromString(String value) {
        Type listType = new TypeToken<Set<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromSet(Set<String> set) {
        Gson gson = new Gson();
        return gson.toJson(set);
    }
}
