package com.example.cozyspot.database.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cozyspot.database.Classes.Review;

import java.util.List;

@Dao
public interface ReviewDao {
    @Query("SELECT * FROM review")
    List<Review> getAll();

    @Insert
    void insert(Review... reviews);

    @Update
    void update(Review... reviews);

    @Delete
    void delete(Review review);

    @Query("SELECT AVG(rating) FROM review WHERE houseId = :houseId")
    float getAverageRatingForHouse(int houseId);
}
