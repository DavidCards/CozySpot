package com.example.cozyspot.database.dao;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cozyspot.database.Classes.HouseWithOwner;

import java.util.List;

@Dao
public interface HouseWithOwnerDao {
    @Transaction
    @Query("SELECT * FROM house")
    List<HouseWithOwner> getHousesWithOwner();

    @Transaction
    @Query("SELECT * FROM house WHERE id = :houseId")
    HouseWithOwner getHouseWithOwner(int houseId);
}
