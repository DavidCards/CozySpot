package com.example.cozyspot.database.dao;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cozyspot.database.Classes.ReviewWithUserAndHouse;

import java.util.List;

@Dao
public interface ReviewWithRelationsDao {
    @Transaction
    @Query("SELECT * FROM review")
    List<ReviewWithUserAndHouse> getReviewsWithUserAndHouse();

    @Transaction
    @Query("SELECT * FROM review WHERE id = :reviewId")
    ReviewWithUserAndHouse getReviewWithUserAndHouse(int reviewId);
}
