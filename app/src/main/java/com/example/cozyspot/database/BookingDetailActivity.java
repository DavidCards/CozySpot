package com.example.cozyspot.database;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.Booking;
import com.example.cozyspot.database.Classes.House;
import com.example.cozyspot.database.creator.AppDatabase;
import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.annotations.IconFactory;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;

public class BookingDetailActivity extends AppCompatActivity {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapLibre.getInstance(this);
        setContentView(R.layout.activity_booking_detail);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            int userId = getIntent().getIntExtra("USER_ID", -1);
            if (itemId == R.id.menu_home) {
                startActivity(new android.content.Intent(this, MainActivity.class).putExtra("USER_ID", userId));
            } else if (itemId == R.id.menu_profile) {
                startActivity(new android.content.Intent(this, Profile.class).putExtra("USER_ID", userId));
            } else if (itemId == R.id.menu_favorites) {
                startActivity(new android.content.Intent(this, FavoritesActivity.class).putExtra("USER_ID", userId));
            } else if (itemId == R.id.menu_messages) {
                startActivity(new android.content.Intent(this, MessagesActivity.class).putExtra("USER_ID", userId));
            } else if (itemId == R.id.menu_bookings) {
                startActivity(new android.content.Intent(this, MyBookingsActivity.class).putExtra("USER_ID", userId));
            }
            drawerLayout.closeDrawers();
            return true;
        });
        int bookingId = getIntent().getIntExtra("BOOKING_ID", -1);
        int userId = getIntent().getIntExtra("USER_ID", -1);
        if (bookingId == -1) { finish(); return; }
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            Booking booking = db.bookingDao().findById(bookingId);
            if (booking == null) { runOnUiThread(this::finish); return; }
            House house = db.houseDao().findHouseById(booking.getHouseId());
            runOnUiThread(() -> {
                TextView textViewTitle = findViewById(R.id.textViewBookingTitle);
                TextView textViewLocation = findViewById(R.id.textViewBookingLocation);
                TextView textViewDates = findViewById(R.id.textViewBookingDates);
                TextView textViewPrice = findViewById(R.id.textViewBookingPrice);
                TextView textViewTotal = findViewById(R.id.textViewBookingTotal);
                TextView textViewDescription = findViewById(R.id.textViewBookingDescription);
                ImageView imageViewHouse = findViewById(R.id.imageViewBookingHouse);
                MapView mapView = findViewById(R.id.mapViewBooking);
                if (house != null) {
                    textViewTitle.setText(house.getTitle());
                    textViewLocation.setText("Local: " + house.getLocation());
                    textViewDescription.setText(house.getDescription());
                    Glide.with(this)
                        .load(house.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(imageViewHouse);
                    mapView.getMapAsync(mapLibreMap -> {
                        double lat = house.getLatitude();
                        double lon = house.getLongitude();
                        mapLibreMap.setStyle("asset://style.json", style -> {
                            IconFactory iconFactory = IconFactory.getInstance(this);
                            mapLibreMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lon))
                                    .title(house.getTitle())
                                    .icon(iconFactory.defaultMarker()));
                        });
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(lat, lon))
                                .zoom(15.0)
                                .build();
                        mapLibreMap.setCameraPosition(position);
                    });
                }
                textViewDates.setText("de " + booking.getStartDate() + " a " + booking.getEndDate());
                // Calcular número de noites
                int nights = 1;
                try {
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                    java.util.Date d1 = sdf.parse(booking.getStartDate());
                    java.util.Date d2 = sdf.parse(booking.getEndDate());
                    long diff = d2.getTime() - d1.getTime();
                    nights = (int) Math.max(1, diff / (1000 * 60 * 60 * 24));
                } catch (Exception ex) { nights = 1; }
                double pricePerNight = house != null ? house.getPricePerNight() : 0;
                textViewPrice.setText(String.format("Preço por noite: %.2f€", pricePerNight));
                textViewTotal.setText(String.format("Total: %.2f€", booking.getTotalPrice()));
            });
        });
    }
}
