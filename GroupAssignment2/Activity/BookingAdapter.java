package com.example.GroupAssignment2.Activity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GroupAssignment2.Domain.BookingDomain;
import com.example.GroupAssignment2.Domain.SkillDomain;
import com.example.GroupAssignment2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<BookingDomain> bookings;
    private SimpleDateFormat dateFormat;

    public BookingAdapter(List<BookingDomain> bookings) {
        this.bookings = bookings;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingDomain booking = bookings.get(position);

        // Set booking date
        String bookingDate = dateFormat.format(new Date(booking.getTimestamp()));
        holder.tvBookingDate.setText(bookingDate);

        // Check if this is a joined skill or a regular booking
        checkIfEnrollment(booking, isEnrollment -> {
            // Load skill details
            loadSkillDetails(booking.getSkillId(), holder);

            // Set up the buttons differently for enrollments
            if (isEnrollment) {
                holder.btnViewDetails.setText("View Skill");
                holder.btnCancel.setText("Leave Skill");
            } else {
                holder.btnViewDetails.setText("View Details");
                holder.btnCancel.setText("Cancel Booking");
            }

            // Set button click listeners
            setupButtonListeners(holder, position, booking, isEnrollment);
        });
    }

    private void checkIfEnrollment(BookingDomain booking, EnrollmentCheckCallback callback) {
        // Check if this booking has a corresponding enrollment document
        FirebaseFirestore.getInstance().collection("skill_enrollments")
                .document(booking.getUserId() + "_" + booking.getSkillId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    callback.onResult(documentSnapshot.exists());
                })
                .addOnFailureListener(e -> {
                    // On failure, assume it's a regular booking
                    callback.onResult(false);
                });
    }

    private void setupButtonListeners(@NonNull ViewHolder holder, int position, BookingDomain booking, boolean isEnrollment) {
        // View details/skill button
        holder.btnViewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), SkillProfileActivity.class);
            intent.putExtra("skillId", booking.getSkillId());
            v.getContext().startActivity(intent);
        });

        // Cancel/Leave button
        holder.btnCancel.setOnClickListener(v -> {
            if (isEnrollment) {
                // Leave the skill (delete from skill_enrollments)
                FirebaseFirestore.getInstance().collection("skill_enrollments")
                        .document(booking.getUserId() + "_" + booking.getSkillId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(v.getContext(), "Left skill successfully", Toast.LENGTH_SHORT).show();
                            bookings.remove(position);
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(v.getContext(), "Failed to leave skill: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Cancel regular booking
                String bookingKey = booking.getSkillId() + "_" + booking.getUserId();
                FirebaseDatabase.getInstance().getReference("bookings")
                        .child(bookingKey)
                        .removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(v.getContext(), "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                            bookings.remove(position);
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(v.getContext(), "Failed to cancel booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void loadSkillDetails(String skillId, ViewHolder holder) {
        // Try to load from Firestore first
        FirebaseFirestore.getInstance().collection("skills").document(skillId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String title = documentSnapshot.getString("title");
                        String type = documentSnapshot.getString("type");

                        if (title != null) {
                            holder.tvSkillTitle.setText(title);
                        } else {
                            holder.tvSkillTitle.setText("Unknown Skill");
                        }

                        if (type != null) {
                            holder.tvSkillType.setText(type);
                        } else {
                            holder.tvSkillType.setText("");
                        }
                    } else {
                        // If not found in Firestore, try Realtime Database
                        loadSkillFromRealtimeDB(skillId, holder);
                    }
                })
                .addOnFailureListener(e -> {
                    // On failure, try Realtime Database
                    loadSkillFromRealtimeDB(skillId, holder);
                });
    }

    private void loadSkillFromRealtimeDB(String skillId, ViewHolder holder) {
        FirebaseDatabase.getInstance().getReference("skills").child(skillId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            SkillDomain skill = dataSnapshot.getValue(SkillDomain.class);
                            if (skill != null) {
                                holder.tvSkillTitle.setText(skill.getTitle());
                                holder.tvSkillType.setText(skill.getType());
                            } else {
                                holder.tvSkillTitle.setText("Unknown Skill");
                                holder.tvSkillType.setText("");
                            }
                        } else {
                            holder.tvSkillTitle.setText("Unknown Skill");
                            holder.tvSkillType.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        holder.tvSkillTitle.setText("Error loading skill");
                        holder.tvSkillType.setText("");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    // Callback interface for enrollment check
    interface EnrollmentCheckCallback {
        void onResult(boolean isEnrollment);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSkillTitle, tvSkillType, tvBookingDate;
        Button btnViewDetails, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSkillTitle = itemView.findViewById(R.id.tvBookingSkillTitle);
            tvSkillType = itemView.findViewById(R.id.tvBookingSkillType);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}