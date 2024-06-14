package com.example.work;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class weekListForAdmin extends AppCompatActivity {
    DatabaseReference reference;

    private RecyclerView recyclerView;
    private ArrayList<Request> list;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    MyAdapter adapter;
    Boolean isAdmin = false;
    TextView textView14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_list_for_admin); // Move setContentView here

        reference = FirebaseDatabase.getInstance().getReference("Requests");
        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);
        processRequests();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView14 = findViewById(R.id.textView16);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setTitle("בקשות שאושרו");
        MenuItem weekListItem = navigationView.getMenu().findItem(R.id.weekList);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.map) {
                    Intent intent = new Intent(weekListForAdmin.this, tablesMap.class);
                    startActivity(intent);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                } else if (item.getItemId() == R.id.logOut) {

                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(weekListForAdmin.this, LogIn.class));
                    finish();
                } else if (item.getItemId() == R.id.MyPlace) {
                    Intent intent = new Intent(weekListForAdmin.this, MyPlace.class);
                    startActivity(intent);
                }
                if (isAdmin) {
                    weekListItem.setVisible(true);
                    if (item.getItemId() == R.id.weekList) {
                        Intent intent = new Intent(weekListForAdmin.this, weekListForAdmin.class);
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
     * Processes the requests from the database and populates the RecyclerView.
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
                    if (request.getTaken()) {
                        String requestDateStr = request.getDate();
                        try {
                            Date requestDate = dateFormat.parse(requestDateStr);
                            if (requestDate != null && requestDate.after(currentDate.getTime())) {
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
