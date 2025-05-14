package com.example.GroupAssignment2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GroupAssignment2.Domain.BookingDomain;
import com.example.GroupAssignment2.R;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookedSkillAdapter extends RecyclerView.Adapter<BookedSkillAdapter.ViewHolder> {

    private List<BookingDomain> bookedSkills;
    private Context context;
    private SimpleDateFormat dateFormat;

    public BookedSkillAdapter(List<BookingDomain> bookedSkills) {
        this.bookedSkills = bookedSkills;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.viewholder_booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingDomain bookedSkill = bookedSkills.get(position);

        // Set booking details
        holder.tvSkillTitle.setText(bookedSkill.getSkillId());
        holder.tvSkillType.setText(bookedSkill.getUserId());

        // Format and set date
        String bookingDate = dateFormat.format(new Date(bookedSkill.getTimestamp()));
        holder.tvBookingDate.setText(bookingDate);

        // Set button click listeners
        holder.btnViewDetails.setOnClickListener(v -> {
            Toast.makeText(context,
                    "Viewing details for " + bookedSkill.getSkillId(),
                    Toast.LENGTH_SHORT).show();
            // Implementation to view skill details
        });

        holder.btnCancel.setOnClickListener(v -> {
            cancelBooking(bookedSkill.getSkillId(), position);
        });
    }

    private void cancelBooking(String bookingId, int position) {
        FirebaseDatabase.getInstance().getReference("bookings")
                .child(bookingId)
                .removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                    bookedSkills.remove(position);
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context,
                            "Failed to cancel booking: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return bookedSkills.size();
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