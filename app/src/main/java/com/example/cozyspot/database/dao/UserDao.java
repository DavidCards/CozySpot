package com.example.cozyspot.database.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cozyspot.database.Classes.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE id = :id")
    User findById(int id);

    @Query("SELECT * FROM user WHERE email = :email AND password = :password LIMIT 1")
    User findByEmailAndPassword(String email, String password);

    @Insert
    long[] insert(User... users);

    @Update
    void update(User... users);

    @Delete
    void delete(User user);
}
