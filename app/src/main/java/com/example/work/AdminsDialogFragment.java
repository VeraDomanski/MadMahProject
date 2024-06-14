package com.example.work;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminsDialogFragment extends DialogFragment {
    private static final String TAG = "AdminsDialogFragment";
    private Request request;
    private String userMail;
    private FirebaseUser user;
    private TextView textViewFrom;
    private TextView textViewPhone;

    public AdminsDialogFragment(Request request) {
        this.request = request;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.admins_pop_up, null);

        TextView textViewTime = view.findViewById(R.id.textViewTime);
        TextView textViewDate = view.findViewById(R.id.textViewDate);
        TextView textViewDay = view.findViewById(R.id.textViewDay);
        TextView textViewSub = view.findViewById(R.id.textViewSub);
        TextView textViewTables = view.findViewById(R.id.textViewTables);
        TextView textViewExtra = view.findViewById(R.id.textViewExtra);
        textViewPhone = view.findViewById(R.id.textViewPhone);
        textViewFrom = view.findViewById(R.id.textViewFrom);
        Button approveButton = view.findViewById(R.id.approve);
        Button approveReject = view.findViewById(R.id.reject);

        ForFrom();

        textViewTime.setText(request.getHour());
        textViewDate.setText(request.getDate());
        textViewDay.setText(request.getDay());
        textViewSub.setText(request.getSubject());
        textViewExtra.setText(request.getExtra());

        // Handle tables text
        List<String> tables = request.getTables();
        StringBuilder tablesText = new StringBuilder();
        for (int i = 0; i < tables.size(); i++) {
            tablesText.append(tables.get(i));
            if (i < tables.size() - 1) {
                tablesText.append(", ");
            }
        }
        textViewTables.setText(tablesText.toString());

        if (request.getTaken()) {
            approveButton.setVisibility(View.INVISIBLE);
            approveReject.setVisibility(View.INVISIBLE);
        } else {
            approveButton.setOnClickListener(v -> {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Requests");
                Query query = databaseRef.orderByChild("date").equalTo(request.getDate());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Request firebaseRequest = snapshot.getValue(Request.class);
                            if (firebaseRequest != null && firebaseRequest.getHour().equals(request.getHour())
                                    && firebaseRequest.getFrom().equals(request.getFrom())
                                    && firebaseRequest.getExtra().equals(request.getExtra())) {
                                snapshot.getRef().child("taken").setValue(true);
                                Utils.sendEmailSingle(getActivity(), "בקשתך אושרה", "בקשתך אושרה",request.getFrom());
                                dismiss();
                                return;
                            }
                        }
                        Toast.makeText(getActivity(), "Error: Request not found", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
            approveReject.setOnClickListener(v -> {
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("Requests");
                Query query = databaseRef.orderByChild("date").equalTo(request.getDate());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Request firebaseRequest = snapshot.getValue(Request.class);
                            if (firebaseRequest != null && firebaseRequest.getHour().equals(request.getHour())
                                    && firebaseRequest.getFrom().equals(request.getFrom())
                                    && firebaseRequest.getExtra().equals(request.getExtra())) {
                                snapshot.getRef().child("rejected").setValue(true);
                                Utils.sendEmailSingle(getActivity(), "בקשתך נדחתה","השולחן שנבחר אינו זמין ביום ושעה שנבחרו" ,request.getFrom());
                                dismiss();
                                return;
                            }
                        }
                        Toast.makeText(getActivity(), "Error: Request not found", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        builder.setView(view);
        return builder.create();
    }

    public void ForFrom() {
        userMail = request.getFrom();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("teachers");
        Query query = databaseReference.orderByChild("email").equalTo(userMail);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            Log.d(TAG, "User found: " + user.getMail());
                            textViewFrom.setText(user.getName());
                            textViewPhone.setText(user.getPhoneNum());
                            return;
                        }
                    }
                } else {
                    Log.e(TAG, "No user found with email: " + userMail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error querying user: " + error.getMessage());
            }
        });
    }
}
