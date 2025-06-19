package com.example.cozyspot.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cozyspot.database.Classes.BookingWithHouse;
import com.example.cozyspot.database.creator.AppDatabase;
import com.example.cozyspot.R;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    public interface OnBookingClickListener {
        void onBookingClick(BookingWithHouse bookingWithHouse);
    }
    private final List<BookingWithHouse> bookingWithHouseList;
    private final OnBookingClickListener listener;
    private final Context context;

    public BookingAdapter(List<BookingWithHouse> bookingWithHouseList, OnBookingClickListener listener, Context context) {
        this.bookingWithHouseList = bookingWithHouseList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingWithHouse bwh = bookingWithHouseList.get(position);
        if (bwh.house != null) {
            int imageResource = context.getResources().getIdentifier(bwh.house.getImageName(), "drawable", context.getPackageName());
            if (imageResource != 0) {
                holder.imageViewHouse.setImageResource(imageResource);
            } else {
                holder.imageViewHouse.setImageResource(R.drawable.ic_launcher_background);
            }
            holder.textViewHouseName.setText(bwh.house.getTitle());
        } else {
            holder.textViewHouseName.setText("Casa desconhecida");
            holder.imageViewHouse.setImageResource(R.drawable.ic_launcher_background);
        }
        holder.textViewBookingDate.setText("Reserva: " + bwh.booking.getStartDate() + " a " + bwh.booking.getEndDate());
        holder.textViewTotalPrice.setText(String.format("Total: %.2f€", bwh.booking.getTotalPrice()));
        holder.itemView.setOnClickListener(v -> listener.onBookingClick(bwh));
    }

    @Override
    public int getItemCount() {
        return bookingWithHouseList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewHouse;
        TextView textViewHouseName, textViewBookingDate, textViewTotalPrice;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewHouse = itemView.findViewById(R.id.imageViewHouse);
            textViewHouseName = itemView.findViewById(R.id.textViewHouseName);
            textViewBookingDate = itemView.findViewById(R.id.textViewBookingDate);
            textViewTotalPrice = itemView.findViewById(R.id.textViewTotalPrice);
        }
    }
}
