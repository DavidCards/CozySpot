package com.example.cozyspot.database.dao;


import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.cozyspot.database.Classes.BookingWithUserAndHouse;

import java.util.List;

@Dao
public interface BookingWithRelationsDao {
    @Transaction
    @Query("SELECT * FROM booking")
    List<BookingWithUserAndHouse> getBookingsWithUserAndHouse();

    @Transaction
    @Query("SELECT * FROM booking WHERE id = :bookingId")
    BookingWithUserAndHouse getBookingWithUserAndHouse(int bookingId);
}
