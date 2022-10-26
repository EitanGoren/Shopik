package com.eitan.shopik.database.views;

import androidx.room.DatabaseView;

@DatabaseView("SELECT user.id, user.name, user.departmentId," +
        "department.name AS departmentName FROM user " +
        "INNER JOIN department ON user.departmentId = department.id")
public class UsersLikedItemsByCatagoryView {
    public long id;
    public String name;
    public long departmentId;
    public String departmentName;
}
