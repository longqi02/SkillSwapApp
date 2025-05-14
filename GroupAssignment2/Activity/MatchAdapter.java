package com.example.GroupAssignment2.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.GroupAssignment2.R;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private List<SkillUser> matchList;

    public MatchAdapter(List<SkillUser> matchList) {
        this.matchList = matchList;
    }

    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        SkillUser user = matchList.get(position);
        holder.tvUserName.setText(user.getUserName());
        holder.tvSkills.setText("Skills Offered: " + user.getOfferedSkills());

        // Fake meeting mode and date for now (since user model doesn't store)
        holder.tvMeetingMode.setText("Meeting Mode: Online"); // (can random later)
        holder.tvAvailableDate.setText("Available: 01/05/2025"); // (can random later)
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvSkills, tvMeetingMode, tvAvailableDate;

        MatchViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvSkills = itemView.findViewById(R.id.tvSkills);
            tvMeetingMode = itemView.findViewById(R.id.tvMeetingMode);
            tvAvailableDate = itemView.findViewById(R.id.tvAvailableDate);
        }
    }
}