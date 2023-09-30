package com.example.summer2023app.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.summer2023app.R;
import com.example.summer2023app.adapters.ViewuserAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.summer2023app.interfaces.OnDialogCloseListener;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RecyclerViewTouchHelper extends ItemTouchHelper.SimpleCallback {
    ViewuserAdapter adapter;
    Context context;
    OnDialogCloseListener closeListener;
    DatabaseReference userReference;
    public RecyclerViewTouchHelper(ViewuserAdapter userAdapter,Context context,OnDialogCloseListener closeListener) {
        super(0, ItemTouchHelper.RIGHT);
        this.adapter = userAdapter;
        this.context =context;
        this.closeListener = closeListener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        final int position = viewHolder.getBindingAdapterPosition();
        String swipedUserId = adapter.getUserIdAt(position);


        userReference = FirebaseDatabase.getInstance().getReference().child("users");

        userReference.orderByChild("email").equalTo("admin@admin.com").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String adminId = userSnapshot.getKey();

                    if (swipedUserId.equals(adminId)) {

                        Toast.makeText(context, "Admin user cannot be deleted", Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(viewHolder.getBindingAdapterPosition());
                    } else {
                        if(direction == ItemTouchHelper.RIGHT){

                            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.context);
                            builder.setTitle("Delete Task");
                            builder.setMessage("Are you sure?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Yes, delete
                                    adapter.deleteUser(position);
                                    closeListener.onDialogClose(dialog);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adapter.notifyItemChanged(position);
                                }
                            });
                            AlertDialog dialog =builder.create();
                            dialog.show();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {


        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeRightBackgroundColor(Color.RED)
                .addSwipeRightActionIcon(R.drawable.baseline_delete_24)
                .create()
                .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }
}
