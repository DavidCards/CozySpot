package com.example.cozyspot.database.Classes;

public class BookingWithHouse {
    public final Booking booking;
    public final House house;
    public BookingWithHouse(Booking booking, House house) {
        this.booking = booking;
        this.house = house;
    }
}
