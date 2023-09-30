package com.example.summer2023app.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.summer2023app.models.CaloriesBurned;
import com.example.summer2023app.adapters.CaloriesRecyclerViewAdapter;
import com.example.summer2023app.R;
import com.example.summer2023app.utilities.VolleySingleton;
import com.example.summer2023app.databinding.FragmentFitnessBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FitnessFragment extends Fragment {
    public static final String TAG=FitnessFragment.class.getSimpleName();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CaloriesRecyclerViewAdapter adapter;
    RequestQueue requestQueue;
    ArrayList<CaloriesBurned> caloriesList;
    static final String INITIAL_KEY_SEARCH = "skiing";
    FragmentFitnessBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        /* get all REFERENCES of variables*/
        binding = FragmentFitnessBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        recyclerView = rootView.findViewById(R.id.recycler_view_calories_burned);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        requestQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
        caloriesList = new ArrayList<>();
        fetchCaloriesAPI();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void fetchCaloriesAPI() {

        String caloriesUrl= "https://calories-burned-by-api-ninjas.p.rapidapi.com/v1/caloriesburned?activity="+INITIAL_KEY_SEARCH+"&duration=65";
        StringRequest caloriesRequest = new StringRequest(Request.Method.GET, caloriesUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the calories response
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i=0;i<jsonArray.length();i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String name = jsonObject.getString("name");
                                int calories_per_hour = jsonObject.getInt("calories_per_hour");
                                int duration_minutes = jsonObject.getInt("duration_minutes");
                                int total_calories = jsonObject.getInt("total_calories");
                                CaloriesBurned caloriesBurned = new CaloriesBurned(name,calories_per_hour,duration_minutes,total_calories);
                                caloriesList.add(caloriesBurned);
                            }
                            adapter = new CaloriesRecyclerViewAdapter(getContext(), caloriesList, new CaloriesRecyclerViewAdapter.OnClickMe() {
                                @Override
                                public void clickMe(int position) {
                                    //Let's store the data into firebase
                                    CaloriesBurned selectedActivity = caloriesList.get(position);

                                    // Create a Bundle to pass data to the next activity
                                    CaloriesBurned caloriesBurned = caloriesList.get(position);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("caloriesName", caloriesBurned.getName());
                                    bundle.putInt("caloriesDuration",caloriesBurned.getDuration_minutes());
                                    bundle.putInt("caloriesTotal",caloriesBurned.getTotal_calories());

                                    // Navigate to PaymentFragment
                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                                    transaction.addToBackStack(null);
                                    transaction.commit();

                                    FirebaseUser myUser = FirebaseAuth.getInstance().getCurrentUser();
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CALL-API-ERROR","Hello There!");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-RapidAPI-Key", "58b02c42c2mshcdcdb63451ddf85p1cb089jsn6fc8ed02b302");
                headers.put("X-RapidAPI-Host", "calories-burned-by-api-ninjas.p.rapidapi.com");
                return headers;
            }
        };
        requestQueue.add(caloriesRequest);
    }

}