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
            List<com.example.cozyspot.database.Classes.Booking> bookings = db.bookingDao().getAll();
            List<com.example.cozyspot.database.Classes.House> reservedHouses = new java.util.ArrayList<>();
            for (com.example.cozyspot.database.Classes.Booking booking : bookings) {
                if (booking.getUserId() == userId) {
                    com.example.cozyspot.database.Classes.House house = db.houseDao().findHouseById(booking.getHouseId());
                    if (house != null) reservedHouses.add(house);
                }
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
                HouseResultAdapter adapter = new HouseResultAdapter(Profile.this, reservedHouses, null, null, userId);
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
}