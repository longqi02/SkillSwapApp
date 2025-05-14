package com.example.GroupAssignment2.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GroupAssignment2.R;
import com.example.GroupAssignment2.Activity.SkillProfileActivity;
import com.example.GroupAssignment2.Domain.SkillDomain;

import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {
    private List<SkillDomain> skills;
    private Context context;
    private boolean isWishlist;

    public SkillAdapter(List<SkillDomain> skills, Context context, boolean isWishlist) {
        this.skills = skills;
        this.context = context;
        this.isWishlist = isWishlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.viewholder_skill_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SkillDomain skill = skills.get(position);

        // Bind values
        holder.title.setText(skill.getTitle());
        holder.type.setText(skill.getType());
        holder.description.setText(skill.getDescription());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, SkillProfileActivity.class);
            intent.putExtra("skillId", skill.getId());
            intent.putExtra("title", skill.getTitle());
            intent.putExtra("type", skill.getType());
            intent.putExtra("description", skill.getDescription());
            intent.putExtra("ownerId", skill.getOwnerId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, type, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvSkillTitle);
            type = itemView.findViewById(R.id.tvSkillType);
            description = itemView.findViewById(R.id.tvSkillDescription);
        }
    }
}