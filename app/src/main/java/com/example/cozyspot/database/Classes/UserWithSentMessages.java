package com.example.cozyspot.database.Classes;

import androidx.room.Embedded;
import androidx.room.Relation;
import java.util.List;

public class UserWithSentMessages {
    @Embedded
    public User user;

    @Relation(parentColumn = "id", entityColumn = "senderId")
    public List<Message> sentMessages;
}
