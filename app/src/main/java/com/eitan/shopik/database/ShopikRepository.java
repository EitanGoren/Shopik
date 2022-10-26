package com.eitan.shopik.database;

import static com.eitan.shopik.Macros.SWIMWEAR;
import static com.eitan.shopik.database.Database.MEN;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.eitan.shopik.database.daos.CompanyDao;
import com.eitan.shopik.database.daos.ItemDao;
import com.eitan.shopik.database.daos.UserDao;
import com.eitan.shopik.database.models.Company;
import com.eitan.shopik.database.models.ShoppingItem;
import com.eitan.shopik.database.models.User;

import java.util.List;

public class ShopikRepository {

    private final UserDao mUserDao;
    private final CompanyDao mCompanyDao;
    private final ItemDao mItemDao;
    private final Database mFirebaseDatabase;
    private final LiveData<List<Company>> mAllCompanies;
    private final LiveData<List<ShoppingItem>> mMenSwimwearLikes;
    private final LiveData<List<ShoppingItem>> getAllItems;

    public ShopikRepository(Application application){
        AppSqlDatabase db = AppSqlDatabase.getInstance(application);
        mUserDao = db.userDao();
        mCompanyDao = db.CompanyDao();
        mItemDao = db.ItemDao();
        mAllCompanies = mCompanyDao.getAllCompanies();
        mMenSwimwearLikes = mItemDao.getAllInteractedItemsByCategory(SWIMWEAR, MEN);
        getAllItems = mItemDao.getAllItems();
        mFirebaseDatabase = Database.getInstance();
    }

    public void insertUser(User user) {
        AppSqlDatabase.databaseWriteExecutor.execute(() -> {
            mUserDao.insert(user);
        });
    }

    public void listenToItems(String item_type, String item_gender){
        Database.databaseWriteExecutor.execute(() -> {
            mFirebaseDatabase.listenToItems(item_type, item_gender);
        });
    }

    public void insertCompany(Company company) {
        AppSqlDatabase.databaseWriteExecutor.execute(() -> {
            mCompanyDao.insert(company);
        });
    }

    public LiveData<List<Company>> getAllCompanies(){
        if(mAllCompanies == null) {
            Database.getInstance().getAllCompaniesInfo();
        }
        return mAllCompanies;
    }

    public void insertNewInteractedItem(ShoppingItem item){
        AppSqlDatabase.databaseWriteExecutor.execute(() -> {
            mItemDao.insert(item);
        });
    }

    public LiveData<List<ShoppingItem>> getAllLikedItemsByCatagory(String category, String gender){
        LiveData<List<ShoppingItem>> allLikedByCategory = mItemDao.getAllInteractedItemsByCategory(category, gender);
        if(allLikedByCategory == null) {
            Database.getInstance().fetchLikedUnlikedItems(category, gender);
        }

        return allLikedByCategory;
    }

    public LiveData<List<ShoppingItem>> getAllInteractedSwimwearMen(){
        if(mMenSwimwearLikes == null) {
            Database.getInstance().fetchLikedUnlikedItems(SWIMWEAR, MEN);
        }

        return mItemDao.getAllInteractedItemsByCategory(SWIMWEAR, MEN);
    }
}
