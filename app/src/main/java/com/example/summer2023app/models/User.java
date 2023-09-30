package com.example.summer2023app.models;

import android.util.Log;

import java.util.List;

public class User {
        private String username;
        private String email;
        private boolean isSubscribed;
        private boolean isAdmin;
        private int height;
        private double bmi;
        private int numberOfDays;
        private int streakCounter;
        private int totalCaloriesBurned;
        String workoutName;
        double maxCaloriesDump;


        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }
        public User(String name, String email){
            Log.d("Constructor","Using the constructor with 3 params");
            this.username = name;
            this.email = email;
        }

        public User(String username, String email, boolean isSubscribed, boolean isAdmin, int height, double bmi,int numberOfDays,int streakCounter,int totalCaloriesBurned,String workoutName,double maxCaloriesDump) {
            Log.d("Constructor","Using the constructor with more than 3 params");

            this.username = username;
            this.email = email;
            this.isSubscribed = isSubscribed;
            this.isAdmin = isAdmin;
            this.height = height;
            this.bmi = bmi;
            this.numberOfDays = numberOfDays;
            this.streakCounter= streakCounter;
            this.totalCaloriesBurned =totalCaloriesBurned;
            this.workoutName =workoutName;
            this.maxCaloriesDump =maxCaloriesDump;
        }
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isSubscribed() {
            return isSubscribed;
        }

        public void setSubscribed(boolean subscribed) {
            isSubscribed = subscribed;
        }

        public boolean isAdmin() {
            return isAdmin;
        }

        public void setAdmin(boolean admin) {
            isAdmin = admin;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public double getBmi() {
            return bmi;
        }

        public void setBmi(double bmi) {
            this.bmi = bmi;
        }

        public int getNumberOfDays() {
        return numberOfDays;
    }

        public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public int getStreakCounter() {
        return streakCounter;
    }

    public void setStreakCounter(int streakCounter) {
        this.streakCounter = streakCounter;
    }

    public int getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(int totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public void setWorkoutName(String workoutName) {
        this.workoutName = workoutName;
    }

    public double getMaxCaloriesDump() {
        return maxCaloriesDump;
    }

    public void setMaxCaloriesDump(double maxCaloriesDump) {
        this.maxCaloriesDump = maxCaloriesDump;
    }

    @Override
        public String toString() {
            return "User{" +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", isAdmin=" + isAdmin +
                    '}';
        }
}
