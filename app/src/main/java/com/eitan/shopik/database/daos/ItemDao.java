package com.eitan.shopik.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.eitan.shopik.database.models.ShoppingItem;

import java.util.List;

@Dao
public interface ItemDao {

    @Query("SELECT * FROM shopik_items")
    LiveData<List<ShoppingItem>> getAllItems();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ShoppingItem item);

    @Query("SELECT * FROM shopik_items WHERE type LIKE :category AND gender LIKE :gender")
    LiveData<List<ShoppingItem>> getAllInteractedItemsByCategory(String category, String gender);

    @Delete
    void delete(ShoppingItem item);
}
