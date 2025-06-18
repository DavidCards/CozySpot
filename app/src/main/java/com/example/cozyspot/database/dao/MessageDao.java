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
}
