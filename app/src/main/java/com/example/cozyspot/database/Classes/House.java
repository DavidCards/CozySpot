package com.example.cozyspot.database.Classes;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "id",
        childColumns = "ownerId",
        onDelete = ForeignKey.CASCADE))
public class House {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String location;
    private String description;
    private double pricePerNight;
    private int rooms;
    private int guests;
    private int ownerId;
    private String imageName;
    private double latitude;
    private double longitude;

    public House(String title, String location, String description, double pricePerNight, int rooms, int guests, int ownerId, String imageName, double latitude, double longitude) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.rooms = rooms;
        this.guests = guests;
        this.ownerId = ownerId;
        this.imageName = imageName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }
    public int getGuests() { return guests; }
    public void setGuests(int guests) { this.guests = guests; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}