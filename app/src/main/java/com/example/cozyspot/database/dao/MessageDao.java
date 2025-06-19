package com.example.cozyspot.database.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cozyspot.database.Classes.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM message")
    List<Message> getAll();

    @Insert
    void insert(Message... messages);

    @Delete
    void delete(Message message);

    @Query("SELECT * FROM message WHERE senderId = :userId OR receiverId = :userId")
    List<Message> getMessagesForUser(int userId);

    @Query("SELECT DISTINCT receiverId FROM message WHERE senderId = :userId")
    List<Integer> getReceiversForUser(int userId);

    @Query("SELECT DISTINCT senderId FROM message WHERE receiverId = :userId")
    List<Integer> getSendersForUser(int userId);
}
