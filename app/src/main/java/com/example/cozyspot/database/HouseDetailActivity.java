package com.example.cozyspot.database;

import static android.content.Intent.getIntent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.Favorite;
import com.example.cozyspot.database.Classes.House;
import com.example.cozyspot.database.Classes.Message;
import com.example.cozyspot.database.dao.FavoriteDao;
import com.example.cozyspot.database.dao.MessageDao;
import com.example.cozyspot.database.creator.AppDatabase;
import com.google.android.material.navigation.NavigationView;

import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.annotations.MarkerOptions;
import org.maplibre.android.annotations.IconFactory;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapLibre.getInstance(this);
        setContentView(R.layout.activity_search_details);

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
                ImageView imageView = findViewById(R.id.imageView7);
                TextView title = findViewById(R.id.textView18);
                TextView location = findViewById(R.id.textView21);
                TextView description = findViewById(R.id.textView23);
                TextView price = findViewById(R.id.textView22);
                TextView rating = findViewById(R.id.textViewRatingDetail);
                ImageButton buttonFavorite = findViewById(R.id.buttonFavorite);
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                EditText editTextDuvida = findViewById(R.id.editTextText3);
                Button buttonEnviar = findViewById(R.id.button3);
                Button buttonBook = findViewById(R.id.buttonBook);
                EditText editTextStartDate = findViewById(R.id.editTextStartDate);
                EditText editTextEndDate = findViewById(R.id.editTextEndDate);
                MapView mapView = findViewById(R.id.mapView);
                ImageButton centerLocationButton = findViewById(R.id.centerLocationButton);

                if (imageView == null || title == null || location == null || description == null || price == null || rating == null || buttonFavorite == null || ratingBar == null || editTextDuvida == null || buttonEnviar == null || buttonBook == null || editTextStartDate == null || editTextEndDate == null || mapView == null || centerLocationButton == null) {
                    Toast.makeText(HouseDetailActivity.this, "Erro: view não encontrada no layout.", Toast.LENGTH_LONG).show();
                    return;
                }

                mapView.getMapAsync(map -> {
                    mapLibreMap = map;
                    double lat = house.getLatitude();
                    double lon = house.getLongitude();
                    houseLat = lat;
                    houseLon = lon;
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
                        // Recheca o estado real após a operação
                        boolean isNowFavorite = db.favoriteDao().exists(userId, houseId) > 0;
                        runOnUiThread(() -> buttonFavorite.setImageResource(isNowFavorite ? R.drawable.ic_heart : R.drawable.ic_heart_outline));
                    });
                });

                rating.setText("Avaliação: .../5");
                executor.execute(() -> {
                    float avgRating = db.reviewDao().getAverageRatingForHouse(houseId);
                    int ratingInt = Math.round(avgRating);
                    runOnUiThread(() -> {
                        rating.setText("Avaliação: " + ratingInt + "/5");
                        ratingBar.setRating(avgRating);
                    });
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
                                rating.setText("Avaliação: " + ratingInt + "/5");
                                ratingBar.setRating(avgRating);
                            });
                        });
                    }
                });
                title.setText(house.getTitle());
                location.setText("Local: " + house.getLocation());
                description.setText(house.getDescription() + "\n\nEsta acomodação oferece uma experiência única, com todas as comodidades para uma estadia confortável, incluindo Wi-Fi rápido, cozinha equipada, proximidade a pontos turísticos e transporte público. Ideal para famílias, casais ou viajantes a trabalho. Aproveite o melhor da cidade com conforto e segurança!");
                price.setText(String.format("Preço por noite: %.2f€", house.getPricePerNight()));

                // Glide image loading with null/empty check
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
        MapView mapView = findViewById(R.id.mapView);
        mapView.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        MapView mapView = findViewById(R.id.mapView);
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        MapView mapView = findViewById(R.id.mapView);
        mapView.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
        MapView mapView = findViewById(R.id.mapView);
        mapView.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapView mapView = findViewById(R.id.mapView);
        mapView.onDestroy();
        executor.shutdown();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MapView mapView = findViewById(R.id.mapView);
        mapView.onLowMemory();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MapView mapView = findViewById(R.id.mapView);
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
}
