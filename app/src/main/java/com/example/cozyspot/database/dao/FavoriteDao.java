package com.example.cozyspot.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cozyspot.database.Classes.Favorite;

import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    List<Favorite> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favorite... favorites);

    @Delete
    void delete(Favorite favorite);

    @Query("SELECT COUNT(*) FROM favorite WHERE userId = :userId AND houseId = :houseId")
    int exists(int userId, int houseId);

    @Query("SELECT houseId FROM favorite WHERE userId = :userId")
    List<Integer> getFavoriteHouseIdsForUser(int userId);
}
