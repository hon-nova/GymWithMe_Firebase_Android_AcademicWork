package com.example.summer2023app.models;

public class CaloriesBurned {
    /*
    *   "name": "Pushing stroller or walking with children",
        "calories_per_hour": 142,
        "duration_minutes": 30,
        "total_calories": 71
    },*/
    String name;
    int calories_per_hour;
    int duration_minutes;
    int total_calories;


    public CaloriesBurned() {
    }

    public CaloriesBurned(String name, int calories_per_hour, int duration_minutes, int total_calories) {
        this.name = name;
        this.calories_per_hour = calories_per_hour;
        this.duration_minutes = duration_minutes;
        this.total_calories =total_calories;
    }

    public String getName() {
        return name;
    }

    public int getCalories_per_hour() {
        return calories_per_hour;
    }

    public int getDuration_minutes() {
        return duration_minutes;
    }

    public int getTotal_calories() {
        return total_calories;
    }
}
