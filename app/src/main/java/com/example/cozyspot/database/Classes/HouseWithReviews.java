package com.example.cozyspot.database.Classes;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class HouseWithReviews {
    @Embedded
    public House house;

    @Relation(parentColumn = "id", entityColumn = "houseId")
    public List<Review> reviews;
}
