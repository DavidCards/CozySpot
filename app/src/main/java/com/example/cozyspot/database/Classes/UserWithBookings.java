package com.example.cozyspot.database.Classes;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class UserWithBookings {
    @Embedded
    public User user;

    @Relation(parentColumn = "id", entityColumn = "userId")
    public List<Booking> bookings;
}
