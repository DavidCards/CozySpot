package com.example.cozyspot.database;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.House;
import com.example.cozyspot.database.Classes.Booking;
import com.example.cozyspot.database.creator.AppDatabase;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private EditText editTextLocation;
    private EditText editTextStartDate;
    private EditText editTextEndDate;
    private EditText editTextGuests;
    private Button buttonSearch;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private int userId;
    private String userRole;
    private RecyclerView recyclerViewTopRated;
    private HouseResultAdapter topRatedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_drawer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID", -1);
        userRole = intent.getStringExtra("USER_ROLE");
        if (userRole == null) userRole = "guest";

        View mainContent = getLayoutInflater().inflate(R.layout.activity_main, drawerLayout, false);
        drawerLayout.addView(mainContent, 0);

        editTextLocation = mainContent.findViewById(R.id.editTextText);
        editTextStartDate = mainContent.findViewById(R.id.editTextStartDate);
        editTextEndDate = mainContent.findViewById(R.id.editTextEndDate);
        editTextGuests = mainContent.findViewById(R.id.editTextNumber);
        buttonSearch = mainContent.findViewById(R.id.button);
        recyclerViewTopRated = mainContent.findViewById(R.id.recyclerViewTopRated);
        recyclerViewTopRated.setLayoutManager(new LinearLayoutManager(this));

        editTextStartDate.setOnClickListener(v -> showDatePickerDialog(editTextStartDate));
        editTextEndDate.setOnClickListener(v -> showDatePickerDialog(editTextEndDate));

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = editTextLocation.getText().toString().trim();
                String startDate = editTextStartDate.getText().toString().trim();
                String endDate = editTextEndDate.getText().toString().trim();
                String guestsString = editTextGuests.getText().toString().trim();
                int guests = 0;
                try {
                    guests = Integer.parseInt(guestsString);
                } catch (Exception ignored) {
                }
                if (location.isEmpty()) {
                    editTextLocation.setError("Localização é obrigatória");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, SearchResult.class);
                intent.putExtra("SEARCH_LOCATION", location);
                intent.putExtra("SEARCH_START_DATE", startDate);
                intent.putExtra("SEARCH_END_DATE", endDate);
                intent.putExtra("SEARCH_GUESTS", guests);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }
        });
        loadTopRatedHouses();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            startActivity(new Intent(this, MainActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
        } else if (id == R.id.menu_profile) {
            startActivity(new Intent(this, Profile.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
        } else if (id == R.id.menu_favorites) {
            startActivity(new Intent(this, FavoritesActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
        } else if (id == R.id.menu_messages) {
            startActivity(new Intent(this, MessagesActivity.class).putExtra("USER_ID", userId).putExtra("USER_ROLE", userRole));
        } else if (id == R.id.menu_bookings) {
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
    }

    private void loadTopRatedHouses() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<House> allHouses = db.houseDao().getAll();
            List<HouseWithRating> houseWithRatings = new ArrayList<>();
            for (House h : allHouses) {
                float rating = db.reviewDao().getAverageRatingForHouse(h.getId());
                houseWithRatings.add(new HouseWithRating(h, rating));
            }
            houseWithRatings.sort((a, b) -> Float.compare(b.rating, a.rating));
            List<House> top10 = new ArrayList<>();
            for (int i = 0; i < Math.min(10, houseWithRatings.size()); i++) {
                top10.add(houseWithRatings.get(i).house);
            }
            runOnUiThread(() -> {
                topRatedAdapter = new HouseResultAdapter(this, top10, null, null, userId);
                recyclerViewTopRated.setAdapter(topRatedAdapter);
            });
        }).start();
    }

    private static class HouseWithRating {
        House house;
        float rating;
        HouseWithRating(House house, float rating) {
            this.house = house;
            this.rating = rating;
        }
    }

    private void showDatePickerDialog(final EditText editText) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editText.setText(dateStr);
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }
}