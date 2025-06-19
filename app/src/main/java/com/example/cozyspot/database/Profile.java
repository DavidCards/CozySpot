package com.example.cozyspot.database;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.Booking;
import com.example.cozyspot.database.Classes.User;
import com.example.cozyspot.database.creator.AppDatabase;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;

public class Profile extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        int userId = getIntent().getIntExtra("USER_ID", -1);
        String userRole = getIntent().getStringExtra("USER_ROLE");
        ImageView avatarView = findViewById(R.id.imageView2);
        TextView nameView = findViewById(R.id.textViewUserName);
        TextView emailView = findViewById(R.id.textViewUserEmail);
        TextView roleView = findViewById(R.id.textViewUserRole);
        RecyclerView recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            User user = db.userDao().findById(userId);
            String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date());
            List<Booking> bookings = db.bookingDao().getCompletedBookingsForUser(userId, currentDate);
            List<com.example.cozyspot.database.Classes.House> reservedHouses = new java.util.ArrayList<>();
            for (Booking booking : bookings) {
                com.example.cozyspot.database.Classes.House house = db.houseDao().findHouseById(booking.getHouseId());
                if (house != null) reservedHouses.add(house);
            }
            runOnUiThread(() -> {
                if (user != null) {
                    nameView.setText(user.getUserName());
                    emailView.setText(user.getEmail());
                    roleView.setText(user.getRole());
                    int resId = getResources().getIdentifier(
                        user.getAvatar().replace(".jpg", "").replace(".png", ""),
                        "drawable", getPackageName());
                    if (resId != 0) {
                        avatarView.setImageResource(resId);
                    }
                }
                HouseResultAdapter adapter = new HouseResultAdapter(Profile.this, reservedHouses, house -> {
                    executor.execute(() -> {
                        AppDatabase db1 = AppDatabase.getInstance(getApplicationContext());
                        Booking booking = db1.bookingDao().findBookingByHouseIdAndUserId(house.getId(), userId);
                        if (booking != null) {
                            Intent intent = new Intent(Profile.this, BookingDetailActivity.class);
                            intent.putExtra("BOOKING_ID", booking.getId());
                            intent.putExtra("USER_ID", userId);
                            startActivity(intent);
                        }
                    });
                }, null, null, userId);
                recyclerViewHistory.setAdapter(adapter);
            });
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                startActivity(new Intent(this, MainActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
            } else if (itemId == R.id.menu_profile) {
                startActivity(new Intent(this, Profile.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
            } else if (itemId == R.id.menu_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
            } else if (itemId == R.id.menu_messages) {
                startActivity(new Intent(this, MessagesActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
            } else if (itemId == R.id.menu_bookings) {
                startActivity(new Intent(this, MyBookingsActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}