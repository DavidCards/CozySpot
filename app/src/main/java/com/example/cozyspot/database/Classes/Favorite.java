package com.example.cozyspot.database.Classes;


import androidx.room.Entity;

@Entity(primaryKeys = {"userId", "houseId"})
public class Favorite {
    private int userId;
    private int houseId;

    public Favorite(int userId, int houseId) {
        this.userId = userId;
        this.houseId = houseId;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getHouseId() { return houseId; }
    public void setHouseId(int houseId) { this.houseId = houseId; }
}
