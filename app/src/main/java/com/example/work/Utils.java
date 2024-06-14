package com.example.work;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Utils {
    /**
     * Sends an email to multiple recipients.
     * @param context The context of the calling activity.
     * @param text The body of the email.
     * @param to An array of email addresses to send the email to.
     */
    public static void sendEmailPlural(Context context, String text,  String[] to) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject: " + text);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        try {
            context.startActivity(Intent.createChooser(intent, "Choose an email client"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Sends an email to a single recipient.
     * @param context The context of the calling activity.
     * @param subject The subject of the email.
     * @param text The body of the email.
     * @param to The email address of the recipient.
     */
    public static void sendEmailSingle(Context context, String subject, String text, String to) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to}); // Ensure it's an array
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);

        try {
            context.startActivity(Intent.createChooser(intent, "Choose an email client"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error sending email: " + e.getMessage());
        }
    }
}