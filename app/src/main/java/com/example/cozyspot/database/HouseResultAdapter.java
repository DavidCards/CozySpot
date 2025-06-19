package com.example.cozyspot.database;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cozyspot.R;
import com.example.cozyspot.database.Classes.House;
import com.example.cozyspot.database.creator.AppDatabase;
import com.example.cozyspot.database.dao.ReviewDao;

import java.util.List;
import java.util.Locale;

public class HouseResultAdapter extends RecyclerView.Adapter<HouseResultAdapter.HouseViewHolder> {
    private final List<House> houseList;
    private final String searchStartDate;
    private final String searchEndDate;
    private final Context context;
    private final int userId;
    private final OnHouseClickListener onHouseClickListener;

    public interface OnHouseClickListener {
        void onHouseClick(House house);
    }

    public HouseResultAdapter(Context context, List<House> houseList, OnHouseClickListener onHouseClickListener, String searchStartDate, String searchEndDate, int userId) {
        this.context = context;
        this.houseList = houseList;
        this.onHouseClickListener = onHouseClickListener;
        this.searchStartDate = searchStartDate;
        this.searchEndDate = searchEndDate;
        this.userId = userId;
    }

    @NonNull
    @Override
    public HouseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_house_result, parent, false);
        return new HouseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseViewHolder holder, int position) {
        House house = houseList.get(position);
        holder.textViewTitle.setText(house.getTitle());
        holder.textViewLocation.setText(house.getLocation());
        
        AppDatabase db = AppDatabase.getInstance(context);
        // Buscar nome do host pelo ownerId
        holder.textViewDetails.setText(context.getString(R.string.host) + " ...");
        new Thread(() -> {
            AppDatabase db1 = AppDatabase.getInstance(context);
            com.example.cozyspot.database.Classes.User host = db1.userDao().findById(house.getOwnerId());
            String hostName = host != null ? host.getUserName() : "";
            new Handler(Looper.getMainLooper()).post(() -> {
                holder.textViewDetails.setText(context.getString(R.string.host) + " " + hostName);
            });
        }).start();
        holder.textViewPrice.setText(context.getString(R.string.price_per_night) + ": " + String.format(Locale.getDefault(), "%.2f€", house.getPricePerNight()));
        Glide.with(context)
                .load(house.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageViewHouse);
        holder.itemView.setOnClickListener(v -> {
            if (onHouseClickListener != null) {
                onHouseClickListener.onHouseClick(house);
            }
        });

        holder.textViewRating.setText(context.getString(R.string.rating_0_5));
        new Thread(() -> {
            ReviewDao reviewDao = db.reviewDao();
            float avgRating = reviewDao.getAverageRatingForHouse(house.getId());
            int ratingInt = Math.round(avgRating);
            new Handler(Looper.getMainLooper()).post(() -> {
                holder.textViewRating.setText(context.getString(R.string.rating) + ": " + ratingInt + "/5");
            });
        }).start();
    }

    @Override
    public int getItemCount() {
        return houseList.size();
    }

    static class HouseViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewHouse;
        TextView textViewTitle, textViewLocation, textViewDetails, textViewPrice, textViewRating;
        public HouseViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewHouse = itemView.findViewById(R.id.imageViewHouse);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewDetails = itemView.findViewById(R.id.textViewDetails);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewRating = itemView.findViewById(R.id.textViewRating);
        }
    }
}
