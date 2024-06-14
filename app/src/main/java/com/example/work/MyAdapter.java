package com.example.work;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


/**
 * Adapter class for populating RecyclerView with requests.
 */
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.YourViewHolder> {

        Context context;
        ArrayList<Request> list;
        private String userRole;

        /**
         * Constructor to initialize the adapter with context and list of requests.
         * @param context The context.
         * @param list The list of requests.
         */
        public MyAdapter(Context context, ArrayList<Request> list) {
            this.context = context;
            this.list = list;

            // Retrieve user role from SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("userRole", Context.MODE_PRIVATE);
            userRole = sharedPreferences.getString("role", "");


        }

        @NonNull
        @Override
        public YourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
            return new YourViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull YourViewHolder holder, int position) {
            Request request = list.get(position);
            holder.day.setText(request.getDay());
            holder.time.setText(request.getHour());
            holder.extra.setText(request.getExtra());
            holder.sub.setText(request.getSubject());
            holder.date.setText(request.getDate());

            if (userRole != null && userRole.equals("admin") ) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPopup(request);
                    }
                });
            }
        }
        /**
         * ViewHolder class to hold the views of each item in the RecyclerView.
         */
        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class YourViewHolder extends RecyclerView.ViewHolder {
            TextView extra, day, time, sub, date;

            public YourViewHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.textViewDate);
                extra = itemView.findViewById(R.id.textViewExtra);
                sub = itemView.findViewById(R.id.textViewSub);
                day = itemView.findViewById(R.id.textViewDay);
                time = itemView.findViewById(R.id.textViewTime);
            }
        }
        /**
         * Method to show the popup dialog for admins.
         * @param request The request associated with the clicked item.
         */
        private void showPopup(Request request) {
            AdminsDialogFragment dialogFragment = new AdminsDialogFragment(request);
            dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "AdminsDialogFragment");
        }
    }