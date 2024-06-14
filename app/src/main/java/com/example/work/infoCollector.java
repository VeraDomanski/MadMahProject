package com.example.work;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class infoCollector extends AppCompatActivity {


    private DatabaseReference requestsRef, userRef;

    private Spinner spinnerSub;
    private Spinner spinnerDay;
    private EditText editText;
    private static final String PREFERENCES_NAME = "MyPrefs";
    private final List<String> selectedTablesList = new ArrayList<>();
    private TextView tableNum;
    private int selectedRoom;
    String userMail;
    String selectedDate;
 TextView dateTextView;
 TextView check;
    Task<Void> firebaseDatabase;
    Button collectButton;
private String selectedHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_collector);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userMail = user.getEmail();
        tableNum = findViewById(R.id.tableNum);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        requestsRef = database.getReference("Requests");
        userRef= database.getReference("teachers");
        check = findViewById(R.id.check);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        selectedRoom = preferences.getInt("selectedRoom", -1);
        tableNum.setText("שולחנות שנבחרו: " + String.valueOf(selectedRoom));
        selectedTablesList.add(String.valueOf(selectedRoom));
        final Switch switchButton = findViewById(R.id.switch1);

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showPopup();
                }
            }
        });

        spinnerDay = findViewById(R.id.day);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = setAccurateDateForSelectedDay();
                dateTextView.setText(selectedDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected (if needed)
            }
        });


        Spinner spinnerHour = findViewById(R.id.hour);
        adapter = ArrayAdapter.createFromResource(this, R.array.hours, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHour.setAdapter(adapter);
        spinnerHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHour = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected (if needed)
            }
        });


        spinnerSub = findViewById(R.id.subject);
        adapter = ArrayAdapter.createFromResource(this, R.array.subjects, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSub.setAdapter(adapter);

        editText = findViewById(R.id.extra);
        dateTextView = findViewById(R.id.dateTextView);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfRequestExists(requestsRef,selectedDate, selectedHour);
            }
        });
        collectButton = findViewById(R.id.bakasha);
        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String selectedDay = spinnerDay.getSelectedItem().toString();
                String selectedSub = spinnerSub.getSelectedItem().toString();
                String extra = editText.getText().toString();


                addToDb(selectedHour, selectedSub, extra, false, selectedDay);
                NotificationFroAdmin();
            }
        });
    }

    /**
     * Shows a popup with checkboxes for selecting additional tables.
     * @return A list of selected table numbers.
     */

    private List<String> showPopup() {
        // Inflate the layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.rooms_checkboxs_4switch, null);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true // This enables the dismiss feature when touched outside
        );

        // Set up checkboxes and save button
        final CheckBox checkbox1 = popupView.findViewById(R.id.checkbox1);
        final CheckBox checkbox2 = popupView.findViewById(R.id.checkbox2);
        final CheckBox checkbox3 = popupView.findViewById(R.id.checkbox3);
        final CheckBox checkbox4 = popupView.findViewById(R.id.checkbox4);
        final CheckBox checkbox5 = popupView.findViewById(R.id.checkbox5);

        // Retrieve saved options from SharedPreferences
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        checkbox1.setChecked(preferences.getBoolean("option1", false));
        checkbox2.setChecked(preferences.getBoolean("option2", false));
        checkbox3.setChecked(preferences.getBoolean("option3", false));
        checkbox4.setChecked(preferences.getBoolean("option4", false));
        checkbox5.setChecked(preferences.getBoolean("option5", false));

        Button saveButton = popupView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save the chosen options using SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean("option1", checkbox1.isChecked());
                editor.putBoolean("option2", checkbox2.isChecked());
                editor.putBoolean("option3", checkbox3.isChecked());
                editor.putBoolean("option4", checkbox4.isChecked());
                editor.putBoolean("option5", checkbox5.isChecked());
                editor.apply();

                if (checkbox1.isChecked()) {
                    selectedTablesList.add("7");
                }
                if (checkbox2.isChecked()) {
                    selectedTablesList.add("8");
                }
                if (checkbox3.isChecked()) {
                    selectedTablesList.add("9");
                }
                if (checkbox4.isChecked()) {
                    selectedTablesList.add("10");
                }
                if (checkbox5.isChecked()) {
                    selectedTablesList.add("11");
                }

                // Add selectedRoom to the list if it's not already there
                if (!selectedTablesList.contains(String.valueOf(selectedRoom))) {
                    selectedTablesList.add(String.valueOf(selectedRoom));
                }

                // Check if there's only one item in the list and it's not in the predefined range
                if (selectedTablesList.size() == 1 && !isValidTableNumber(selectedTablesList.get(0))) {
                    // Duplicate the single number to avoid issues with the adapter
                    selectedTablesList.add(selectedTablesList.get(0));
                }

                // Update tableNum TextView
                tableNum.setText("שולחנות שנבחרו: " + TextUtils.join(", ", selectedTablesList));

                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(findViewById(R.id.switch1), Gravity.CENTER, 0, 0);

        // Dismiss the popup when touching outside of it
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

        return selectedTablesList;
    }
    /**
     * Adds the request details to the Firebase database.
     * @param hour The selected hour.
     * @param subject The selected subject.
     * @param extra Any additional notes.
     * @param status The status of the request.
     * @param day The selected day.
     */
    public void addToDb(String hour, String subject, String extra, Boolean status, String day) {

        Map<String, Object> map = new HashMap<>();
        map.put("day", day);
        map.put("date", selectedDate);
        map.put("hour", hour);
        map.put("subject", subject);
        map.put("extra", extra);
        map.put("from", userMail);
        map.put("taken", status);
        map.put("rejected", false);
        map.put("tables", selectedTablesList);

        firebaseDatabase = requestsRef.push().setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>()  {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(infoCollector.this, "הבקשה נשלחה", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(infoCollector.this, MyPlace.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(infoCollector.this, "קרתה תקלה", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    /**
     * Sets the accurate date for the selected day.
     * @return The formatted date string.
     */
    private String setAccurateDateForSelectedDay() {
        // Get the selected day from the spinner
        int selectedDayIndex = spinnerDay.getSelectedItemPosition(); // Spinner position starts from 0

        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Calculate the difference in days between the selected day and the current day
        int daysToAdd = (selectedDayIndex + 1 - currentDayOfWeek + 7) % 7;
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);

        // Format the date and display in TextView
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String selectedDate = dateFormat.format(calendar.getTime());
        return selectedDate;
    }
    /**
     * Checks if the given table number is valid.
     * @param number The table number as a string.
     * @return True if the table number is valid, false otherwise.
     */
    private boolean isValidTableNumber(String number) {
        int tableNumber = Integer.parseInt(number);
        return tableNumber >= 7 && tableNumber <= 11;
    }
    /**
     * Checks if a request already exists for the selected date and hour.
     * @param requestsRef The database reference to the requests.
     * @param date The selected date.
     * @param hour The selected hour.
     */
    public void checkIfRequestExists(DatabaseReference requestsRef, String date, String hour) {
        requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Request s = dataSnapshot.getValue(Request.class);
                    if (s != null && (s.getDate().equals(date)) && (s.getHour().equals(hour))) {
                        for (String table : selectedTablesList) {
                            if (s.getTables().contains(table)) {
                                check.setText("השולחן " + s.getTables().toString() + " אינו זמין בשעה הזאת. בבקשה, בחרו שעה אחרת ");
                                check.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                collectButton.setVisibility(View.GONE);
                                found = true;
                                break;
                            }
                        }
                    }
                }
                if (!found) {
                    check.setText("השולחן זמין ביום ובשעה הנבחרים");
                    check.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    collectButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle cancellation if needed
            }
        });
    }
    /**
     * Notifies administrators about a new request for approval.
     * Fetches the emails of administrators from the Firebase Realtime Database
     * and sends a notification email to each of them.
     */
    public void NotificationFroAdmin(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("teachers");
        List<String> emailList = new ArrayList<>();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String roel = snapshot.child("Role").getValue(String.class);
                    if ((email != null)&& (roel.equals("admin"))) {
                        emailList.add(email);
                        Log.d("EmailRetrieval", "Retrieved email: " + email); // Log the retrieved email
                    } else {
                        Log.d("EmailRetrieval", "Email is null for user: " + snapshot.getKey());
                    }
                }
                String[] emailArray = emailList.toArray(new String[0]);
                Log.d("EmailRetrieval", "Email array: " + Arrays.toString(emailArray)); // Log the email array
                Utils.sendEmailPlural(infoCollector.this, "בקשה להזמנה חדשה מחקה לאישור", emailArray);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("EmailRetrieval", "Database error: " + databaseError.getMessage()); // Log any database errors
            }
        });
    }
}
