package com.example.cozyspot.database.Classes;


import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
    @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = ForeignKey.CASCADE),
    @ForeignKey(entity = House.class, parentColumns = "id", childColumns = "houseId", onDelete = ForeignKey.CASCADE)
})
public class Review {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private int houseId;
    private int rating;
    private String comment;

    public Review(int userId, int houseId, int rating, String comment) {
        this.userId = userId;
        this.houseId = houseId;
        this.rating = rating;
        this.comment = comment;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getHouseId() { return houseId; }
    public void setHouseId(int houseId) { this.houseId = houseId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
