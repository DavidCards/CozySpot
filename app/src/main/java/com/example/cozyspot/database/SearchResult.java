package com.example.cozyspot.database;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.House;
import com.example.cozyspot.database.Classes.Booking;
import com.example.cozyspot.database.creator.AppDatabase;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchResult extends AppCompatActivity {

    private AppDatabase db;
    private ExecutorService executorService;

    private EditText editTextLocationFilter;
    private EditText editTextStartDateFilter;
    private EditText editTextEndDateFilter;
    private EditText editTextGuestsFilter;
    private RecyclerView recyclerViewHouses;
    private HouseResultAdapter houseResultAdapter;

    private TextView textViewNoResults;
    private TextView textViewLocation;
    private TextView textViewDate;
    private TextView textViewGuests;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private int userId;

    private boolean sortByPriceAsc = true;
    private boolean sortByRatingAsc = true;
    private List<House> currentHouseList = new java.util.ArrayList<>();
    private String searchStartDate = null;
    private String searchEndDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.menu_profile) {
                startActivity(new Intent(this, Profile.class));
            } else if (id == R.id.menu_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
            } else if (id == R.id.menu_messages) {
                startActivity(new Intent(this, MessagesActivity.class));
            } else if (id == R.id.menu_bookings) {
                drawerLayout.closeDrawers();
                drawerLayout.postDelayed(() -> {
                    Intent intent = new Intent(this, MyBookingsActivity.class);
                    intent.putExtra("USER_ID", userId);
                    startActivity(intent);
                }, 250);
                return true;
            } else if (id == R.id.menu_logout) {
                drawerLayout.closeDrawers();
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }, 250);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        textViewLocation = findViewById(R.id.textViewLocation);
        textViewDate = findViewById(R.id.textViewDate);
        textViewGuests = findViewById(R.id.textViewGuests);
        db = AppDatabase.getInstance(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        initializeViews();

        findViewById(R.id.button8).setOnClickListener(v -> {
            sortByPriceAsc = !sortByPriceAsc;
            sortByRatingAsc = true;
            sortAndDisplayHouses("price");
        });
        findViewById(R.id.button9).setOnClickListener(v -> {
            sortByRatingAsc = !sortByRatingAsc;
            sortByPriceAsc = true;
            sortAndDisplayHouses("rating");
        });

        Intent intent = getIntent();
        if (intent != null) {
            String location = intent.getStringExtra("SEARCH_LOCATION");
            String startDate = intent.getStringExtra("SEARCH_START_DATE");
            String endDate = intent.getStringExtra("SEARCH_END_DATE");
            int guests = intent.getIntExtra("SEARCH_GUESTS", 0);
            userId = intent.getIntExtra("USER_ID", -1);

            searchStartDate = startDate;
            searchEndDate = endDate;

            if (location != null) {
                textViewLocation.setText(location);
            }
            if (startDate != null && endDate != null) {
                textViewDate.setText("de " + startDate + " a " + endDate);
            }
            if (guests > 0) {
                textViewGuests.setText(String.valueOf(guests));
            }
            performSearch(location, startDate, endDate, guests);
        } else {
            if (textViewNoResults != null) {
                textViewNoResults.setText("Nenhum critério de pesquisa fornecido.");
                textViewNoResults.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initializeViews() {
        recyclerViewHouses = findViewById(R.id.recyclerViewHouses);
        recyclerViewHouses.setLayoutManager(new LinearLayoutManager(this));
    }

    private void sortAndDisplayHouses(String criteria) {
        if (currentHouseList == null || currentHouseList.isEmpty()) return;
        if (criteria.equals("price")) {
            currentHouseList.sort((h1, h2) -> sortByPriceAsc ?
                    Double.compare(h1.getPricePerNight(), h2.getPricePerNight()) :
                    Double.compare(h2.getPricePerNight(), h1.getPricePerNight()));
            houseResultAdapter = new HouseResultAdapter(this, currentHouseList, house -> {
                Intent intent = new Intent(SearchResult.this, HouseDetailActivity.class);
                intent.putExtra("HOUSE_ID", house.getId());
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            }, searchStartDate, searchEndDate, userId);
            recyclerViewHouses.setAdapter(houseResultAdapter);
        } else if (criteria.equals("rating")) {
            executorService.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(this);
                java.util.Map<Integer, Float> ratings = new java.util.HashMap<>();
                for (House h : currentHouseList) {
                    float r = db.reviewDao().getAverageRatingForHouse(h.getId());
                    ratings.put(h.getId(), r);
                }
                currentHouseList.sort((h1, h2) -> sortByRatingAsc ?
                        Float.compare(ratings.get(h1.getId()), ratings.get(h2.getId())) :
                        Float.compare(ratings.get(h2.getId()), ratings.get(h1.getId())));
                runOnUiThread(() -> {
                    houseResultAdapter = new HouseResultAdapter(this, currentHouseList, house -> {
                        Intent intent = new Intent(SearchResult.this, HouseDetailActivity.class);
                        intent.putExtra("HOUSE_ID", house.getId());
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                    }, searchStartDate, searchEndDate, userId);
                    recyclerViewHouses.setAdapter(houseResultAdapter);
                });
            });
        }
    }

    private void performSearch(String location, String startDate, String endDate, int guests) {
        String locationQuery = "%" + location + "%";
        executorService.execute(() -> {
            List<House> houses = db.houseDao().findHousesByCriteria(locationQuery, guests);
            List<House> availableHouses = new java.util.ArrayList<>();
            for (House house : houses) {
                List<Booking> bookings = db.bookingWithDateDao().getBookingsForHouseOnDate(house.getId(), startDate);
                if (bookings == null || bookings.isEmpty()) {
                    availableHouses.add(house);
                }
            }
            java.util.Set<Integer> seenIds = new java.util.HashSet<>();
            List<House> uniqueAvailableHouses = new java.util.ArrayList<>();
            for (House house : availableHouses) {
                if (!seenIds.contains(house.getId())) {
                    uniqueAvailableHouses.add(house);
                    seenIds.add(house.getId());
                }
            }
            runOnUiThread(() -> {
                currentHouseList = uniqueAvailableHouses;
                searchStartDate = startDate;
                searchEndDate = endDate;
                if (uniqueAvailableHouses.isEmpty()) {
                    if (textViewNoResults != null) {
                        textViewNoResults.setVisibility(View.VISIBLE);
                    }
                    recyclerViewHouses.setVisibility(View.GONE);
                } else {
                    if (textViewNoResults != null) {
                        textViewNoResults.setVisibility(View.GONE);
                    }
                    recyclerViewHouses.setVisibility(View.VISIBLE);
                    houseResultAdapter = new HouseResultAdapter(this, uniqueAvailableHouses, house -> {
                        Intent intent = new Intent(SearchResult.this, HouseDetailActivity.class);
                        intent.putExtra("HOUSE_ID", house.getId());
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                    }, searchStartDate, searchEndDate, userId);
                    recyclerViewHouses.setAdapter(houseResultAdapter);
                }
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public int getUserId() {
        return userId;
    }
}