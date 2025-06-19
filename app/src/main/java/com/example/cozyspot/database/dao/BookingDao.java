package com.example.cozyspot.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cozyspot.database.Classes.Booking;

import java.util.List;

@Dao
public interface BookingDao {
    @Query("SELECT * FROM booking")
    List<Booking> getAll();

    @Insert
    long[] insert(Booking... bookings);

    @Update
    void update(Booking... bookings);

    @Delete
    void delete(Booking booking);

    @Query("SELECT * FROM booking WHERE id = :id LIMIT 1")
    Booking findById(int id);

    @Query("SELECT * FROM booking WHERE userId = :userId")
    List<Booking> getBookingsForUser(int userId);

    @Query("SELECT COUNT(*) > 0 FROM booking WHERE userId = :userId AND houseId = :houseId")
    boolean hasCompletedReservation(int userId, int houseId);

    @Query("SELECT * FROM booking WHERE userId = :userId AND endDate < :currentDate")
    List<Booking> getCompletedBookingsForUser(int userId, String currentDate);

    @Query("SELECT * FROM booking WHERE houseId = :houseId AND userId = :userId LIMIT 1")
    Booking findBookingByHouseIdAndUserId(int houseId, int userId);
}
