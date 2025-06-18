package com.example.cozyspot.database;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.House;
import com.example.cozyspot.database.creator.AppDatabase;
import com.example.cozyspot.database.dao.FavoriteDao;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private int userId;
    private String userRole;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        userId = getIntent().getIntExtra("USER_ID", -1);
        userRole = getIntent().getStringExtra("USER_ROLE");

        RecyclerView recyclerView = findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            FavoriteDao favoriteDao = db.favoriteDao();
            List<Integer> favoriteHouseIds = favoriteDao.getFavoriteHouseIdsForUser(userId);
            List<House> favoriteHouses = new ArrayList<>();
            for (int houseId : favoriteHouseIds) {
                House house = db.houseDao().findHouseById(houseId);
                if (house != null) favoriteHouses.add(house);
            }
            runOnUiThread(() -> {
                HouseResultAdapter adapter = new HouseResultAdapter(FavoritesActivity.this, favoriteHouses, null, null, userId);
                recyclerView.setAdapter(adapter);
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
                drawerLayout.closeDrawers();
                drawerLayout.postDelayed(() -> {
                    Intent intent = new Intent(this, MyBookingsActivity.class);
                    intent.putExtra("USER_ID", userId);
                    intent.putExtra("USER_ROLE", userRole);
                    startActivity(intent);
                }, 250);
                return true;
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }
}
