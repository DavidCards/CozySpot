package com.example.cozyspot.database.Classes;


import androidx.room.Embedded;
import androidx.room.Relation;

public class BookingWithUserAndHouse {
    @Embedded
    public Booking booking;

    @Relation(parentColumn = "userId", entityColumn = "id")
    public User user;

    @Relation(parentColumn = "houseId", entityColumn = "id")
    public House house;
}
