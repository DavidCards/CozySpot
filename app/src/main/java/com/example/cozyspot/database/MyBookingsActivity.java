package com.example.cozyspot.database;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.Booking;
import com.example.cozyspot.database.creator.AppDatabase;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBookingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView textViewNoBookings;
    private int userId;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);
        recyclerView = findViewById(R.id.recyclerViewBookings);
        textViewNoBookings = findViewById(R.id.textViewNoBookings);
        userId = getIntent().getIntExtra("USER_ID", -1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadBookings();
        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
    }

    private void loadBookings() {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<Booking> bookings = db.bookingDao().getBookingsForUser(userId);
            runOnUiThread(() -> {
                if (bookings == null || bookings.isEmpty()) {
                    textViewNoBookings.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    textViewNoBookings.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new BookingAdapter(bookings, booking -> {
                        Intent intent = new Intent(this, BookingDetailActivity.class);
                        intent.putExtra("BOOKING_ID", booking.getId());
                        intent.putExtra("USER_ID", userId);
                        startActivity(intent);
                    }));
                }
            });
        });
    }
}
