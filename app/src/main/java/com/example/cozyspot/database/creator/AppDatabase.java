package com.example.cozyspot.database.creator;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.cozyspot.database.Classes.Booking;
import com.example.cozyspot.database.Classes.Favorite;
import com.example.cozyspot.database.Classes.House;
import com.example.cozyspot.database.Classes.Message;
import com.example.cozyspot.database.Classes.Review;
import com.example.cozyspot.database.Classes.User;
import com.example.cozyspot.database.dao.BookingDao;
import com.example.cozyspot.database.dao.BookingWithDateDao;
import com.example.cozyspot.database.dao.BookingWithRelationsDao;
import com.example.cozyspot.database.dao.FavoriteDao;
import com.example.cozyspot.database.dao.HouseDao;
import com.example.cozyspot.database.dao.HouseWithOwnerDao;
import com.example.cozyspot.database.dao.HouseWithRelationsDao;
import com.example.cozyspot.database.dao.MessageDao;
import com.example.cozyspot.database.dao.ReviewDao;
import com.example.cozyspot.database.dao.ReviewWithRelationsDao;
import com.example.cozyspot.database.dao.UserDao;
import com.example.cozyspot.database.dao.UserWithRelationsDao;
import com.example.cozyspot.database.dao.UserWithSentMessagesDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {User.class, House.class, Booking.class, Review.class, Favorite.class, Message.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();

    public abstract HouseDao houseDao();

    public abstract BookingDao bookingDao();

    public abstract ReviewDao reviewDao();

    public abstract FavoriteDao favoriteDao();

    public abstract MessageDao messageDao();

    public abstract UserWithRelationsDao userWithRelationsDao();

    public abstract HouseWithRelationsDao houseWithRelationsDao();

    public abstract BookingWithRelationsDao bookingWithRelationsDao();

    public abstract ReviewWithRelationsDao reviewWithRelationsDao();

    public abstract HouseWithOwnerDao houseWithOwnerDao();

    public abstract BookingWithDateDao bookingWithDateDao();

    public abstract UserWithSentMessagesDao userWithSentMessagesDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "BookingDB")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE House ADD COLUMN imageUrl TEXT");
        }
    };

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Executors.newSingleThreadExecutor().execute(() -> {
                if (instance != null) {
                    UserDao userDao = instance.userDao();
                    HouseDao houseDao = instance.houseDao();
                    BookingDao bookingDao = instance.bookingDao();
                    ReviewDao reviewDao = instance.reviewDao();
                    FavoriteDao favoriteDao = instance.favoriteDao();
                    MessageDao messageDao = instance.messageDao();

                    User user1 = new User("Alice Wonderland", "alice@example.com", "password123", "guest", "default_avatar_user.jpg");
                    User user2 = new User("Bob The Builder", "bob@example.com", "securepass", "host", "default_avatar_user.jpg");
                    User user3 = new User("Charlie Brown", "charlie@example.com", "snoopy", "guest", "default_avatar_user.jpg");
                    User user4 = new User("Diana Prince", "diana@example.com", "wonderpass", "host", "default_avatar_user.jpg");
                    User user5 = new User("David Cardoso", "david@email.com", "123", "guest", "default_avatar_user.jpg");
                    long[] userIds = userDao.insert(user1, user2, user3, user4, user5);
                    int aliceId = (int) userIds[0];
                    int bobId = (int) userIds[1];
                    int charlieId = (int) userIds[2];
                    int dianaId = (int) userIds[3];
                    int davidId = (int) userIds[4];

                    List<House> houseList = InitialHouses.getAll(bobId, dianaId);
                    List<Long> houseIds = new ArrayList<>();
                    for (House h : houseList) {
                        houseIds.add(houseDao.insert(h)[0]);
                    }
                    int lisboaId = houseIds.get(0).intValue();
                    int portoId = houseIds.get(5).intValue();
                    int faroId = houseIds.get(10).intValue();
                    int bragaId = houseIds.get(15).intValue();
                    int coimbraId = houseIds.get(20).intValue();
                    Booking booking1 = new Booking(aliceId, lisboaId, "2025-12-10", "2025-12-15", 5 * 120.00);
                    Booking booking2 = new Booking(aliceId, portoId, "2025-12-12", "2025-12-14", 2 * 100.00);
                    Booking booking3 = new Booking(charlieId, faroId, "2025-08-01", "2025-08-10", 9 * 200.00);
                    Booking booking4 = new Booking(dianaId, bragaId, "2025-12-12", "2025-12-13", 1 * 140.00);
                    Booking booking5 = new Booking(aliceId, coimbraId, "2025-07-20", "2025-07-25", 5 * 90.00);
                    bookingDao.insert(booking1, booking2, booking3, booking4, booking5);
                    Review review1 = new Review(aliceId, lisboaId, 5, "Apartamento excelente no centro de Lisboa!");
                    Review review2 = new Review(charlieId, portoId, 4, "Vista incrível para o Douro.");
                    Review review3 = new Review(aliceId, faroId, 5, "A moradia do Algarve é perfeita para férias em família.");
                    Review review4 = new Review(bobId, bragaId, 4, "Casa familiar muito confortável.");
                    Review review5 = new Review(dianaId, coimbraId, 5, "Flat ideal para estudantes.");
                    reviewDao.insert(review1, review2, review3, review4, review5);
                    Favorite favorite1 = new Favorite(aliceId, faroId);
                    Favorite favorite2 = new Favorite(bobId, lisboaId);
                    Favorite favorite3 = new Favorite(charlieId, portoId);
                    Favorite favorite4 = new Favorite(dianaId, bragaId);
                    favoriteDao.insert(favorite1, favorite2, favorite3, favorite4);
                    Message message1 = new Message(aliceId, dianaId, "Olá, a casa de Lisboa está disponível para o Natal?", "2024-06-15 10:00:00");
                    Message message2 = new Message(dianaId, aliceId, "Olá Alice, infelizmente já está reservada de 10 a 15 de dezembro.", "2024-06-15 10:05:00");
                    Message message3 = new Message(charlieId, bobId, "A casa do Porto aceita animais?", "2024-06-16 14:30:00");
                    Message message4 = new Message(bobId, charlieId, "Sim, animais pequenos são bem-vindos!", "2024-06-16 14:35:00");
                    Message message5 = new Message(davidId, bobId, "Olá Bob, a casa de Faro está disponível em agosto?", "2024-06-17 09:00:00");
                    Message message6 = new Message(bobId, davidId, "Olá David, sim, está disponível para as datas que quiser!", "2024-06-17 09:05:00");
                    Message message7 = new Message(aliceId, dianaId, "Gostaria de saber se aceitam pets na casa de Braga.", "2024-06-18 11:00:00");
                    Message message8 = new Message(dianaId, aliceId, "Sim, aceitamos pets na casa de Braga!", "2024-06-18 11:05:00");
                    Message message9 = new Message(charlieId, dianaId, "A casa de Coimbra tem estacionamento?", "2024-06-19 12:00:00");
                    Message message10 = new Message(dianaId, charlieId, "Sim, tem estacionamento gratuito.", "2024-06-19 12:05:00");
                    Message message11 = new Message(davidId, dianaId, "A casa de Cascais tem piscina?", "2024-06-20 13:00:00");
                    Message message12 = new Message(dianaId, davidId, "Não, mas tem acesso fácil à praia!", "2024-06-20 13:05:00");
                    Message message13 = new Message(bobId, aliceId, "Olá Alice, obrigado pela avaliação positiva!", "2024-06-21 15:00:00");
                    Message message14 = new Message(aliceId, bobId, "De nada, adorei a estadia!", "2024-06-21 15:05:00");
                    Message message15 = new Message(davidId, charlieId, "Oi Charlie, já ficou na casa de Nazaré? Recomenda?", "2024-06-22 16:00:00");
                    Message message16 = new Message(charlieId, davidId, "Sim, recomendo muito! Vista incrível.", "2024-06-22 16:05:00");
                    messageDao.insert(message1, message2, message3, message4, message5, message6, message7, message8, message9, message10, message11, message12, message13, message14, message15, message16);
                }
            });
        }
    };
}
