package com.example.cozyspot.database;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.Booking;
import com.example.cozyspot.database.Classes.BookingWithHouse;
import com.example.cozyspot.database.Classes.House;
import com.example.cozyspot.database.creator.AppDatabase;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBookingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textViewNoBookings;
    private int userId;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);
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
                startActivity(new Intent(this, MainActivity.class).putExtra("USER_ID", userId));
            } else if (itemId == R.id.menu_profile) {
                startActivity(new Intent(this, Profile.class).putExtra("USER_ID", userId));
            } else if (itemId == R.id.menu_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class).putExtra("USER_ID", userId));
            } else if (itemId == R.id.menu_messages) {
                startActivity(new Intent(this, MessagesActivity.class).putExtra("USER_ID", userId));
            } else if (itemId == R.id.menu_bookings) {
                drawerLayout.closeDrawers();
                return true;
            }
            drawerLayout.closeDrawers();
            return true;
        });
        recyclerView = findViewById(R.id.recyclerViewBookings);
        textViewNoBookings = findViewById(R.id.textViewNoBookings);
        userId = getIntent().getIntExtra("USER_ID", -1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadBookings();
    }

    private void loadBookings() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<Booking> bookings = db.bookingDao().getBookingsForUser(userId);
            List<BookingWithHouse> bookingWithHouseList = new java.util.ArrayList<>();
            for (Booking booking : bookings) {
                House house = db.houseDao().findHouseById(booking.getHouseId());
                bookingWithHouseList.add(new BookingWithHouse(booking, house));
            }
            runOnUiThread(() -> {
                if (bookingWithHouseList.isEmpty()) {
                    textViewNoBookings.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    textViewNoBookings.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new BookingAdapter(bookingWithHouseList, bwh -> {
                        Intent intent = new Intent(this, BookingDetailActivity.class);
                        intent.putExtra("BOOKING_ID", bwh.booking.getId());
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                    }, this));
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
