package com.example.work;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class tablesMap extends AppCompatActivity {

    private TextView[] textViews = new TextView[12];
    private PopupWindow[] popups = new PopupWindow[12];

    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables_map);
        back = findViewById(R.id.button2);

        for (int i = 0; i < 12; i++) {
            int resId = getResources().getIdentifier("textView" + (i + 1), "id", getPackageName());
            textViews[i] = findViewById(resId);
            textViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int roomNumber = Integer.parseInt(((TextView) view).getText().toString());
                    showPopup(roomNumber);
                }
            });
        }
        for (int i = 0; i < 12; i++) {
            int resId = getResources().getIdentifier("room" + (i + 1), "layout", getPackageName());
            popups[i] = new PopupWindow(this);
            popups[i].setContentView(getLayoutInflater().inflate(resId, null));
        }
        Button backButton = findViewById(R.id.button2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle back button click if needed
                finish();
            }
        });
    }

    private void showPopup(int roomNumber) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("selectedRoom", roomNumber);
        editor.apply();

        for (PopupWindow popup : popups) {
            if (popup.isShowing()) {
                popup.dismiss();
            }
        }
        // Show the selected popup
        popups[roomNumber - 1].showAtLocation(findViewById(R.id.myImageView), Gravity.CENTER, 0, 0);
        findViewById(android.R.id.content).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                for (PopupWindow popup : popups) {
                    if (popup.isShowing()) {
                        popup.dismiss();
                    }
                }
                return false;
            }
        });

        // Get reference to the button inside the popup
        Button popupButton = popups[roomNumber - 1].getContentView().findViewById(R.id.button4);
        popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Forward to another activity
                Intent intent = new Intent(tablesMap.this, infoCollector.class);
                startActivity(intent);
            }
        });
    }
}
