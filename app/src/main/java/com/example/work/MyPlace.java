package com.example.work;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyPlace extends AppCompatActivity {

    DatabaseReference reference;

    private RecyclerView recyclerView;
    private ArrayList<Request> list;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private FirebaseUser user;
    private String userID, userMail;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MyAdapter adapter;
    private String  role;
    Boolean isAdmin = false;
    TextView textView14;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_place);

        recyclerView = findViewById(R.id.recyclerView);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userMail = user.getEmail();

        textView14 = findViewById(R.id.textView14);


        reference = FirebaseDatabase.getInstance().getReference("Requests");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);

        // Check user role to determine if admin
        checkUserRole();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setTitle("מקום שלי");
        MenuItem weekListItem = navigationView.getMenu().findItem(R.id.weekList);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.map) {
                    Intent intent = new Intent(MyPlace.this, tablesMap.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }else if (item.getItemId() == R.id.logOut){
                    
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MyPlace.this, LogIn.class));
                    finish();
                }
                if (isAdmin) {
                    weekListItem.setVisible(true);
                    if (item.getItemId()==R.id.weekList){
                        Intent intent = new Intent(MyPlace.this, weekListForAdmin.class);
                        startActivity(intent);
                    }
                } else {
                    weekListItem.setVisible(false);
                }
                return false;
            }
        });
    }
    /**
     * Checks the user role to determine if the user is an admin.
     */
    private void checkUserRole() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = databaseReference.child("teachers").orderByChild("email").equalTo(userMail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        role = user.getRole();
                        if (role != null && role.equals("admin")) {
                            isAdmin = true;
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("userRole", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("role", role); // Store the new role
                        editor.apply();
                        processRequests();

                        return; // Exit the method after processing requests
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }
/**
        * Processes the requests based on the user's role.
            */
    private void processRequests() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear(); // Clear list before populating again
                Calendar currentDate = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
                String formattedDate = dateFormat.format(currentDate.getTime());

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Request request = dataSnapshot.getValue(Request.class);
                    if (request != null && (request.getFrom().equals(userMail)&& request.getTaken() || (isAdmin && !request.getTaken())) && !request.getRejected()) {
                        String requestDateStr = request.getDate();
                        try {
                            Date requestDate = dateFormat.parse(requestDateStr);
                            if (requestDate != null && requestDate.after(currentDate.getTime()) ) {
                                list.add(request);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                if (list.isEmpty()) {
                    textView14.setVisibility(View.VISIBLE);
                    textView14.setText("אין קביעות");
                } else {
                    textView14.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }
}
