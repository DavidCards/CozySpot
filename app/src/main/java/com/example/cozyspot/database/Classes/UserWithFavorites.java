package com.example.cozyspot.database.Classes;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class UserWithFavorites {
    @Embedded
    public User user;

    @Relation(parentColumn = "id", entityColumn = "userId", entity = Favorite.class, projection = {"houseId"})
    public List<Favorite> favorites;
}
