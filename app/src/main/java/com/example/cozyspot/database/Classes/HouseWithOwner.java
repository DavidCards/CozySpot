package com.example.cozyspot.database.Classes;

import androidx.room.Embedded;
import androidx.room.Relation;

public class HouseWithOwner {
    @Embedded
    public House house;

    @Relation(parentColumn = "ownerId", entityColumn = "id")
    public User owner;
}
