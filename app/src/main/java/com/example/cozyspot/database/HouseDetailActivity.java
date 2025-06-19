package com.example.cozyspot.database;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.Favorite;
import com.example.cozyspot.database.Classes.House;
import com.example.cozyspot.database.Classes.Message;
import com.example.cozyspot.database.dao.FavoriteDao;
import com.example.cozyspot.database.dao.MessageDao;
import com.example.cozyspot.database.creator.AppDatabase;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.plugins.annotation.SymbolManager;
import org.maplibre.android.plugins.annotation.SymbolOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HouseDetailActivity extends AppCompatActivity {
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private MapLibreMap mapLibreMap;
    private double houseLat, houseLon;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapLibre.getInstance(this);
        setContentView(R.layout.activity_search_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_home) {
                startActivity(new Intent(this, MainActivity.class).putExtra("USER_ID", getIntent().getIntExtra("USER_ID", -1)));
            } else if (id == R.id.menu_profile) {
                startActivity(new Intent(this, Profile.class).putExtra("USER_ID", getIntent().getIntExtra("USER_ID", -1)));
            } else if (id == R.id.menu_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class).putExtra("USER_ID", getIntent().getIntExtra("USER_ID", -1)));
            } else if (id == R.id.menu_messages) {
                startActivity(new Intent(this, MessagesActivity.class).putExtra("USER_ID", getIntent().getIntExtra("USER_ID", -1)));
            } else if (id == R.id.menu_bookings) {
                startActivity(new Intent(this, MyBookingsActivity.class).putExtra("USER_ID", getIntent().getIntExtra("USER_ID", -1)));
            }
            drawerLayout.closeDrawers();
            return true;
        });

        View content = findViewById(R.id.main_content);

        int houseId = getIntent().getIntExtra("HOUSE_ID", -1);
        if (houseId == -1) {
            finish();
            return;
        }
        int userId = getIntent().getIntExtra("USER_ID", -1);
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            House house = db.houseDao().findHouseById(houseId);
            if (house == null) {
                java.util.List<House> allHouses = db.houseDao().getAll();
                StringBuilder ids = new StringBuilder();
                for (House h : allHouses) ids.append(h.getId()).append(", ");
                runOnUiThread(() -> {
                    finish();
                });
                return;
            }
            runOnUiThread(() -> {
                ImageView imageView = content.findViewById(R.id.imageView7);
                TextView title = content.findViewById(R.id.textView18);
                TextView location = content.findViewById(R.id.textView21);
                TextView description = content.findViewById(R.id.textViewDescriptionLabel);
                TextView price = content.findViewById(R.id.textViewPrice);
                ImageButton buttonFavorite = content.findViewById(R.id.buttonFavorite);
                RatingBar ratingBar = content.findViewById(R.id.ratingBar);
                EditText editTextDuvida = content.findViewById(R.id.editTextQuestion);
                Button buttonEnviar = content.findViewById(R.id.buttonSend);
                Button buttonBook = content.findViewById(R.id.buttonBook);
                EditText editTextStartDate = content.findViewById(R.id.editTextStartDate);
                EditText editTextEndDate = content.findViewById(R.id.editTextEndDate);
                MapView mapView = content.findViewById(R.id.mapView);
                ImageButton centerLocationButton = content.findViewById(R.id.centerLocationButton);
                TextView hostName = content.findViewById(R.id.textViewHostName);

                if (imageView == null || title == null || location == null || description == null || price == null || buttonFavorite == null || ratingBar == null || editTextDuvida == null || buttonEnviar == null || buttonBook == null || editTextStartDate == null || editTextEndDate == null || mapView == null || centerLocationButton == null) {
                    Toast.makeText(HouseDetailActivity.this, getString(R.string.layout_view_error), Toast.LENGTH_LONG).show();
                    return;
                }

                String styleJson = loadStyleFromAssets();
                mapView.getMapAsync(map -> {
                    mapLibreMap = map;
                    map.setStyle(new org.maplibre.android.maps.Style.Builder().fromJson(styleJson), style -> {
                        style.addImage("marker", androidx.core.content.res.ResourcesCompat.getDrawable(getResources(), R.drawable.baseline_location_on_24, getTheme()));
                        org.maplibre.android.plugins.annotation.SymbolManager symbolManager = new org.maplibre.android.plugins.annotation.SymbolManager(mapView, mapLibreMap, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setIconIgnorePlacement(true);
                        double lat = house.getLatitude();
                        double lon = house.getLongitude();
                        symbolManager.create(new org.maplibre.android.plugins.annotation.SymbolOptions()
                                .withLatLng(new org.maplibre.android.geometry.LatLng(lat, lon))
                                .withIconImage("marker")
                                .withIconSize(1.3f)
                        );
                        mapLibreMap.setCameraPosition(new org.maplibre.android.camera.CameraPosition.Builder()
                                .target(new org.maplibre.android.geometry.LatLng(lat, lon))
                                .zoom(15.0)
                                .build());
                    });
                });

                centerLocationButton.setOnClickListener(v -> {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        centerMapOnUserLocation();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });

                executor.execute(() -> {
                    boolean isFavorite = db.favoriteDao().exists(userId, houseId) > 0;
                    runOnUiThread(() -> buttonFavorite.setImageResource(isFavorite ? R.drawable.ic_heart : R.drawable.ic_heart_outline));
                });
                buttonFavorite.setOnClickListener(v -> {
                    executor.execute(() -> {
                        boolean currentlyFavorite = db.favoriteDao().exists(userId, houseId) > 0;
                        if (currentlyFavorite) {
                            db.favoriteDao().delete(new com.example.cozyspot.database.Classes.Favorite(userId, houseId));
                        } else {
                            db.favoriteDao().insert(new com.example.cozyspot.database.Classes.Favorite(userId, houseId));
                        }
                        boolean isNowFavorite = db.favoriteDao().exists(userId, houseId) > 0;
                        runOnUiThread(() -> buttonFavorite.setImageResource(isNowFavorite ? R.drawable.ic_heart : R.drawable.ic_heart_outline));
                    });
                });

                executor.execute(() -> {
                    float avgRating = db.reviewDao().getAverageRatingForHouse(houseId);
                    int ratingInt = Math.round(avgRating);
                    runOnUiThread(() -> ratingBar.setRating(avgRating));
                });
                ratingBar.setOnRatingBarChangeListener((bar, value, fromUser) -> {
                    if (fromUser) {
                        executor.execute(() -> {
                            boolean userExists = db.userDao().findById(userId) != null;
                            boolean houseExists = db.houseDao().findHouseById(houseId) != null;
                            if (!userExists || !houseExists) {
                                runOnUiThread(() -> Toast.makeText(HouseDetailActivity.this, "Erro: usuário ou casa não encontrados.", Toast.LENGTH_LONG).show());
                                return;
                            }
                            db.reviewDao().insert(new com.example.cozyspot.database.Classes.Review(userId, houseId, (int)value, ""));
                            float avgRating = db.reviewDao().getAverageRatingForHouse(houseId);
                            int ratingInt = Math.round(avgRating);
                            runOnUiThread(() -> {
                                ratingBar.setRating(avgRating);
                            });
                        });
                    }
                });
                title.setText(house.getTitle());
                location.setText("Local: " + house.getLocation());
                description.setText(house.getDescription() + "\n\nEsta acomodação oferece uma experiência única, com todas as comodidades para uma estadia confortável, incluindo Wi-Fi rápido, cozinha equipada, proximidade a pontos turísticos e transporte público. Ideal para famílias, casais ou viajantes a trabalho. Aproveite o melhor da cidade com conforto e segurança!");
                price.setText(String.format("Preço por noite: %.2f€", house.getPricePerNight()));

                executor.execute(() -> {
                    com.example.cozyspot.database.Classes.User host = db.userDao().findById(house.getOwnerId());
                    String hostDisplay = host != null ? host.getUserName() : "(desconhecido)";
                    runOnUiThread(() -> hostName.setText(getString(R.string.host) + " " + hostDisplay));
                });

                String imageUrl = house.getImageUrl();
                if (imageView != null && imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(imageView);
                } else if (imageView != null) {
                    imageView.setImageResource(R.drawable.ic_launcher_background);
                }


                buttonEnviar.setOnClickListener(v -> {
                    String duvida = editTextDuvida.getText().toString().trim();
                    if (duvida.isEmpty()) {
                        editTextDuvida.setError("Digite sua dúvida");
                        return;
                    }
                    executor.execute(() -> {
                        int hostId = house.getOwnerId();
                        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        Message mensagem = new Message(userId, hostId, duvida, timestamp);
                        db.messageDao().insert(mensagem);
                        runOnUiThread(() -> {
                            editTextDuvida.setText("");
                            Toast.makeText(HouseDetailActivity.this, "Dúvida enviada ao host!", Toast.LENGTH_SHORT).show();
                        });
                    });
                });
                String startDate = getIntent().getStringExtra("SEARCH_START_DATE");
                String endDate = getIntent().getStringExtra("SEARCH_END_DATE");
                if (startDate != null) editTextStartDate.setText(startDate);
                if (endDate != null) editTextEndDate.setText(endDate);

                editTextStartDate.setOnClickListener(v -> {
                    showDatePickerDialog(editTextStartDate);
                });
                editTextEndDate.setOnClickListener(v -> {
                    showDatePickerDialog(editTextEndDate);
                });

                buttonBook.setOnClickListener(v -> {
                    String sDate = editTextStartDate.getText().toString();
                    String eDate = editTextEndDate.getText().toString();
                    if (sDate.isEmpty() || eDate.isEmpty()) {
                        Toast.makeText(HouseDetailActivity.this, "Selecione as datas para reservar", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int nights = 1;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date d1 = sdf.parse(sDate);
                        Date d2 = sdf.parse(eDate);
                        long diff = d2.getTime() - d1.getTime();
                        nights = (int) Math.max(1, diff / (1000 * 60 * 60 * 24));
                    } catch (Exception ex) { nights = 1; }
                    double totalPrice = house.getPricePerNight() * nights;
                    executor.execute(() -> {
                        AppDatabase db2 = AppDatabase.getInstance(getApplicationContext());
                        db2.bookingDao().insert(new com.example.cozyspot.database.Classes.Booking(userId, houseId, sDate, eDate, totalPrice));
                        runOnUiThread(() -> Toast.makeText(HouseDetailActivity.this, "Reserva realizada com sucesso!", Toast.LENGTH_LONG).show());
                    });
                });
            });
        });
    }

    private void centerMapOnUserLocation() {
        android.location.LocationManager locationManager = (android.location.LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null && mapLibreMap != null) {
            LatLng userLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            CameraPosition position = new CameraPosition.Builder()
                    .target(userLatLng)
                    .zoom(15.0)
                    .build();
            mapLibreMap.setCameraPosition(position);
        } else {
            Toast.makeText(this, "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                centerMapOnUserLocation();
            } else {
                Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        View content = findViewById(R.id.main_content);
        MapView mapView = content.findViewById(R.id.mapView);
        mapView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        View content = findViewById(R.id.main_content);
        MapView mapView = content.findViewById(R.id.mapView);
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        View content = findViewById(R.id.main_content);
        MapView mapView = content.findViewById(R.id.mapView);
        mapView.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        View content = findViewById(R.id.main_content);
        MapView mapView = content.findViewById(R.id.mapView);
        mapView.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        View content = findViewById(R.id.main_content);
        MapView mapView = content.findViewById(R.id.mapView);
        mapView.onDestroy();
        executor.shutdown();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        View content = findViewById(R.id.main_content);
        MapView mapView = content.findViewById(R.id.mapView);
        mapView.onLowMemory();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        View content = findViewById(R.id.main_content);
        MapView mapView = content.findViewById(R.id.mapView);
        mapView.onSaveInstanceState(outState);
    }

    private void showDatePickerDialog(final EditText editText) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            editText.setText(dateStr);
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

    private String loadStyleFromAssets() {
        try {
            java.io.InputStream is = getApplicationContext().getAssets().open("style.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
