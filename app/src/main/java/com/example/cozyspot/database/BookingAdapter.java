package com.example.cozyspot.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cozyspot.database.Classes.Booking;
import com.example.cozyspot.R;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    public interface OnBookingClickListener {
        void onBookingClick(Booking booking);
    }
    private final List<Booking> bookings;
    private final OnBookingClickListener listener;

    public BookingAdapter(List<Booking> bookings, OnBookingClickListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.textViewBookingInfo.setText("Reserva: " + booking.getStartDate() + " a " + booking.getEndDate());
        holder.itemView.setOnClickListener(v -> listener.onBookingClick(booking));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookingInfo;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBookingInfo = itemView.findViewById(R.id.textViewBookingInfo);
        }
    }
}
