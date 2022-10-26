package com.eitan.shopik.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.eitan.shopik.database.models.Company;

import java.util.List;

@Dao
public interface CompanyDao {

    @Query("SELECT * FROM shopik_companies")
    LiveData<List<Company>> getAllCompanies();

    @Insert
    void insertAll(Company... Companies);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Company company);

    @Query("SELECT * FROM shopik_companies WHERE id LIKE :id LIMIT 1")
    Company getCompanyInfoById(String id);
}
