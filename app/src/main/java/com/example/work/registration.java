package com.example.work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
/**
 * Activity for user registration.
 */

public class registration extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    Task<Void> firebaseDatabase;
    EditText etUserName;
    EditText phoneNum;
    EditText mail;
    EditText pass;
    private CheckBox stan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
/**
 * colects stuff
 */
        etUserName = findViewById(R.id.Name);
        Button reg = findViewById(R.id.enter);
        phoneNum = findViewById(R.id.editTextPhone);
        mail = findViewById(R.id.etEmailAddress);
        pass = findViewById(R.id.etPassword);
        CheckBox checkBoxLibrarian = findViewById(R.id.checkBoxLibrarian);
        CheckBox checkBoxTeacher = findViewById(R.id.checkBoxTeacher);

        if ((checkBoxLibrarian.isChecked()) && (!checkBoxTeacher.isChecked())) {
            stan = checkBoxLibrarian;
        } else if (((!checkBoxLibrarian.isChecked()) && (checkBoxTeacher.isChecked()))) {
            stan = checkBoxTeacher;
        }

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() | !validatePassword() | !validateMail() | !validatePhone()) {
                } else {
                    Map<String, Object> map = new HashMap<>();

                    if (checkBoxLibrarian.isChecked()) {
                        map.put("Role", "admin");
                    } else if (checkBoxTeacher.isChecked()) {
                        map.put("Role", "teacher");
                    }

                    map.put("name", etUserName.getText().toString());
                    map.put("phoneNum", phoneNum.getText().toString());
                    map.put("email", mail.getText().toString());
                    map.put("pass", pass.getText().toString());

                    firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("teachers").push()
                            .setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(registration.this, "all cool", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(registration.this, MyPlace.class);
                                    addToFireAuth();
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(registration.this, "not cool", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }

    /**
     * Adds user to FirebaseAuth.
     */

    private void addToFireAuth() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mail.getText().toString(), pass.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(registration.this, "Successfully registered", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(registration.this, "Registration Error", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    /**
     * Validates the entered username.
     * @return True if username is valid, false otherwise.
     */
    public Boolean validateUsername() {
        String val = etUserName.getText().toString();
        if (val.isEmpty()) {
            etUserName.setError("Username cannot be empty");
            return false;
        } else {
            etUserName.setError(null);
            return true;
        }
    }

    /**
     * Validates the entered password.
     * @return True if password is valid, false otherwise.
     */
    public Boolean validatePassword() {
        String val = pass.getText().toString();
        if (val.isEmpty()) {
            pass.setError("Password cannot be empty");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }
    /**
     * Validates the entered phone number.
     * @return True if phone number is valid, false otherwise.
     */
    public Boolean validatePhone() {
        String val = phoneNum.getText().toString();
        if (val.isEmpty()) {
            phoneNum.setError("Username cannot be empty");
            return false;
        } else {
            phoneNum.setError(null);
            return true;
        }
    }
    /**
     * Validates the entered email address.
     * @return True if email address is valid, false otherwise.
     */
    public Boolean validateMail() {
        String val = mail.getText().toString();
        if (val.isEmpty()) {
            mail.setError("Password cannot be empty");
            return false;
        } else {
            mail.setError(null);
            return true;
        }
    }
}

