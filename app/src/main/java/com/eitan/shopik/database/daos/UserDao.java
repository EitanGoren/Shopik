package com.eitan.shopik.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.eitan.shopik.database.models.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM shopik_users")
    List<User> getAll();

    @Query("SELECT * FROM shopik_users WHERE id IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM shopik_users WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);

    @Insert
    void insertAll(User... users);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(User user);

    @Delete
    void delete(User user);
}
