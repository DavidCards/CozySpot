package com.example.cozyspot.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cozyspot.database.Classes.UserWithSentMessages;

import java.util.List;

@Dao
public interface UserWithSentMessagesDao {
    @Transaction
    @Query("SELECT * FROM user")
    List<UserWithSentMessages> getUsersWithSentMessages();

    @Transaction
    @Query("SELECT * FROM user WHERE id = :userId")
    UserWithSentMessages getUserWithSentMessages(int userId);
}
