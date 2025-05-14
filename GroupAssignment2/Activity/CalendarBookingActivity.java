package com.example.GroupAssignment2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.GroupAssignment2.R;
import com.example.GroupAssignment2.Domain.BookingDomain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class CalendarBookingActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private Button btnConfirm;
    private long selectedDate;
    private String skillId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_booking);

        calendarView  = findViewById(R.id.calendarView);
        btnConfirm    = findViewById(R.id.btnConfirmBooking);
        skillId       = getIntent().getStringExtra("skillId");

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // month is 0-based
            selectedDate = new java.util.GregorianCalendar(year, month, dayOfMonth)
                    .getTimeInMillis();
        });

        btnConfirm.setOnClickListener(v -> {
            // TODO: Persist to Firestore
            // Validate selection
            if (selectedDate == 0) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current user ID from Firebase Auth
            String userId = "user123"; // Default fallback
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }

            BookingDomain booking = new BookingDomain(
                    skillId,
                    /*currentUserId=*/"user123",
                    selectedDate
            );
            // Generate unique booking ID
            String bookingId = userId + "_" + skillId + "_" + selectedDate;

            // Save to Firebase
            FirebaseDatabase.getInstance().getReference("bookings")
                    .child(bookingId)
                    .setValue(booking)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Booked successfully for " +
                                new java.util.Date(selectedDate), Toast.LENGTH_LONG).show();

                        // Navigate to ViewBookedSkillActivity
                        Intent intent = new Intent(CalendarBookingActivity.this, ViewBookedSkillActivity.class);
                        intent.putExtra("skillId", skillId);
                        intent.putExtra("timestamp", selectedDate);
                        startActivity(intent);

                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Booking failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        });
    }
}
