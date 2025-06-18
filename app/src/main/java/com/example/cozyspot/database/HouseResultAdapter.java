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

    public HouseResultAdapter(Context context, List<House> houseList, String searchStartDate, String searchEndDate, int userId) {
        this.context = context;
        this.houseList = houseList;
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
        holder.textViewDetails.setText("Hóspedes: " + house.getGuests());
        holder.textViewPrice.setText(String.format(Locale.getDefault(), "Preço por noite: %.2f€", house.getPricePerNight()));
        Glide.with(context)
                .load(house.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.imageViewHouse);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HouseDetailActivity.class);
            intent.putExtra("HOUSE_ID", house.getId());
            intent.putExtra("USER_ID", userId);
            if (searchStartDate != null) intent.putExtra("SEARCH_START_DATE", searchStartDate);
            if (searchEndDate != null) intent.putExtra("SEARCH_END_DATE", searchEndDate);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.textViewRating.setText("Avaliação: .../5");
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            ReviewDao reviewDao = db.reviewDao();
            float avgRating = reviewDao.getAverageRatingForHouse(house.getId());
            int ratingInt = Math.round(avgRating);
            new Handler(Looper.getMainLooper()).post(() -> {
                holder.textViewRating.setText("Avaliação: " + ratingInt + "/5");
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
