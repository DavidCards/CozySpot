package com.example.cozyspot.database.dao;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cozyspot.database.Classes.HouseWithReviews;

import java.util.List;

@Dao
public interface HouseWithRelationsDao {
    @Transaction
    @Query("SELECT * FROM house")
    List<HouseWithReviews> getHousesWithReviews();

    @Transaction
    @Query("SELECT * FROM house WHERE id = :houseId")
    HouseWithReviews getHouseWithReviews(int houseId);
}
