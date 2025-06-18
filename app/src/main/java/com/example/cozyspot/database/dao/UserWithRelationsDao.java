package com.example.cozyspot.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cozyspot.database.Classes.UserWithBookings;
import com.example.cozyspot.database.Classes.UserWithFavorites;
import com.example.cozyspot.database.Classes.UserWithSentMessages;

import java.util.List;

@Dao
public interface UserWithRelationsDao {
    @Transaction
    @Query("SELECT * FROM user")
    List<UserWithBookings> getUsersWithBookings();

    @Transaction
    @Query("SELECT * FROM user WHERE id = :userId")
    UserWithBookings getUserWithBookings(int userId);

    @Transaction
    @Query("SELECT * FROM user")
    List<UserWithFavorites> getUsersWithFavorites();

    @Transaction
    @Query("SELECT * FROM user WHERE id = :userId")
    UserWithFavorites getUserWithFavorites(int userId);

    @Transaction
    @Query("SELECT * FROM user")
    List<UserWithSentMessages> getUsersWithMessages();

    @Transaction
    @Query("SELECT * FROM user WHERE id = :userId")
    UserWithSentMessages getUserWithMessages(int userId);
}
