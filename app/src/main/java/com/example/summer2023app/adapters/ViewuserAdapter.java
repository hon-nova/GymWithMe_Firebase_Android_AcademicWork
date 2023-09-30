package com.example.summer2023app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summer2023app.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import com.example.summer2023app.models.User;

public class ViewuserAdapter extends RecyclerView.Adapter<ViewuserAdapter.MyViewHolder> {

    public Context context;
    ArrayList<User> UserList;
    ArrayList<String> userIdList;
    //constructor
    public ViewuserAdapter(Context context, ArrayList<User> users,ArrayList<String> userIds){
        this.context = context;
        this.UserList =users;
        this.userIdList = userIds;
    }

    public void deleteUser(int position) {
        String userId = userIdList.get(position);
        DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference("users").child(userId);
        dbUser.removeValue();
        UserList.remove(position);
        userIdList.remove(position);
        notifyItemRemoved(position);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_viewuser,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = UserList.get(position);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return UserList.size();
    }

    //nested MyViewHolder class
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView username,email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.txtViewUsername);
            email = itemView.findViewById(R.id.txtViewEmail);
        }
    }
    /* for admin route purpose */
    public String getUserIdAt(int position) {
        return userIdList.get(position);
    }
}
