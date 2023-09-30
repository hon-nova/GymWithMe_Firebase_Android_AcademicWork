package com.example.summer2023app.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summer2023app.R;
import com.example.summer2023app.models.CaloriesBurned;

import java.util.ArrayList;

public class CaloriesRecyclerViewAdapter extends RecyclerView.Adapter<CaloriesRecyclerViewAdapter.MyViewHolder>{

        Context context;
        ArrayList<CaloriesBurned> caloriesList;
        OnClickMe onClickMe;

      //constructor
    public CaloriesRecyclerViewAdapter(Context context, ArrayList<CaloriesBurned> caloriesList, OnClickMe listener) {
        this.context = context;
        this.caloriesList = caloriesList;
        this.onClickMe = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_calories_burned,parent,false);
        //create an object of MyViewHolder, calling its constructor
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        CaloriesBurned caloriesObject= caloriesList.get(position);

        holder.name.setText(caloriesObject.getName());
        holder.calories_per_hour.setText(String.valueOf(caloriesObject.getCalories_per_hour()));
        holder.duration_minutes.setText(String.valueOf(caloriesObject.getDuration_minutes()));
        holder.total_calories.setText(String.valueOf(caloriesObject.getTotal_calories()));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //position = getAdapterPosition();
                //Snackbar.make(view,"Click detected on item: ", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                Log.d("RecyclerItemClick", "Card clicked at position: " + position);
                onClickMe.clickMe(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return caloriesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name, calories_per_hour, duration_minutes,total_calories;
        CardView cardView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.name);
            calories_per_hour = itemView.findViewById(R.id.calories_per_hour);
            duration_minutes = itemView.findViewById(R.id.duration_minutes);
            total_calories = itemView.findViewById(R.id.total_calories);

            //IMPORTANT
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//                    Snackbar.make(v,"Click detected on item: "+position, Snackbar.LENGTH_LONG).setAction("Action",null).show();
//
//                    onClickMe.clickMe(position);
//                }
//            });
        }
    }

    public interface OnClickMe{
        public void clickMe(int position);
    }

}
