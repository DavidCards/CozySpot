package com.example.cozyspot.database.Classes;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
    @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.CASCADE),
    @ForeignKey(entity = House.class, parentColumns = "id", childColumns = "houseId", onDelete = ForeignKey.CASCADE)
})
public class Booking {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int houseId;
    private String startDate;
    private String endDate;
    private double totalPrice;

    public Booking(int userId, int houseId, String startDate, String endDate, double totalPrice) {
        this.userId = userId;
        this.houseId = houseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getHouseId() { return houseId; }
    public void setHouseId(int houseId) { this.houseId = houseId; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public double getPricePerNight(double housePricePerNight, int nights) {
        if (nights <= 0) return 0;
        return totalPrice / nights;
    }
}
