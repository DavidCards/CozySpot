package com.example.cozyspot.database.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.example.cozyspot.database.Classes.Booking;
import java.util.List;

@Dao
public interface BookingWithDateDao {
    @Query("SELECT * FROM booking WHERE houseId = :houseId AND ((:date BETWEEN startDate AND endDate) OR (:date = startDate) OR (:date = endDate))")
    List<Booking> getBookingsForHouseOnDate(int houseId, String date);
}
