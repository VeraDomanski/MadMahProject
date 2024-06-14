package com.example.work;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LogIn extends AppCompatActivity {

    private EditText username;
    private EditText pass;
    private Button enter;

    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.userName);
        pass = findViewById(R.id.pass);
        enter = findViewById(R.id.enter);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUsername() && validatePassword()) {
                   checkUser();
                }
            }
        });

        TextView t = findViewById(R.id.register);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogIn.this, registration.class);
                startActivity(intent);
            }
        });
    }
    /**
     * it is the cool red cercule to fill the fiald
     * @return
     */
    public Boolean validateUsername() {
        String val = username.getText().toString();
        if (val.isEmpty()) {
            username.setError("Username cannot be empty");
            return false;
        } else {
            username.setError(null);
            return true;
        }
    }

    /**
     * it is the cool red cercule to fill the fiald
     * @return
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
     * checks the user in basa and logs in
     */
    public void checkUser() {
        firebaseAuth.signInWithEmailAndPassword(username.getText().toString(),pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            Intent intent = new Intent(LogIn.this, MyPlace.class);
                            startActivity(intent);

                        }
                        else {
                            Toast.makeText(LogIn.this, "שם משתמש או הסיסמה אינם תקנים",Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

}