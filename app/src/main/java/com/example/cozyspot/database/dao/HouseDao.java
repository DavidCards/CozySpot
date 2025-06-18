package com.example.cozyspot.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cozyspot.database.Classes.House;

import java.util.List;

@Dao
public interface HouseDao {
    @Query("SELECT * FROM house")
    List<House> getAll();

    @Insert
    long[] insert(House... houses);

    @Update
    void update(House... houses);

    @Delete
    void delete(House house);

    @Query("SELECT * FROM house WHERE LOWER(location) LIKE LOWER(:locationQuery) AND guests >= :minGuests")
    List<House> findHousesByCriteria(String locationQuery, int minGuests);

    @Query("SELECT * FROM house WHERE LOWER(location) = LOWER(:exactLocation) AND guests >= :minGuests")
    List<House> findHousesByExactLocationAndGuests(String exactLocation, int minGuests);

    @Query("SELECT * FROM house WHERE id = :houseId")
    House findHouseById(int houseId);

    @Query("DELETE FROM house")
    void deleteAll();
}