package com.example.summer2023app.utilities;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    RequestQueue requestQueue;
    static VolleySingleton instance;
    public VolleySingleton(Context context){
        requestQueue = Volley.newRequestQueue(context);
    }
    public static synchronized VolleySingleton getInstance(Context context){
        if(instance ==null){
            instance= new VolleySingleton((context));
        }
        return instance;
    }
    public RequestQueue getRequestQueue(){
        return requestQueue;
    }
}
