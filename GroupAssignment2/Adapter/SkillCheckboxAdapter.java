package com.example.GroupAssignment2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GroupAssignment2.R;

import java.util.ArrayList;
import java.util.List;

public class SkillCheckboxAdapter extends RecyclerView.Adapter<SkillCheckboxAdapter.SkillViewHolder> {

    private final List<String> skills;
    private final List<String> selectedSkills = new ArrayList<>();

    public SkillCheckboxAdapter(List<String> skills) {
        this.skills = skills;
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_skill_checkbox, parent, false);
        return new SkillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder, int position) {
        String skill = skills.get(position);
        holder.checkBox.setText(skill);
        holder.checkBox.setChecked(selectedSkills.contains(skill));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedSkills.contains(skill)) selectedSkills.add(skill);
            } else {
                selectedSkills.remove(skill);
            }
        });
    }

    @Override
    public int getItemCount() {
        return skills.size();
    }

    public List<String> getSelectedSkills() {
        return selectedSkills;
    }

    static class SkillViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public SkillViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.skillCheckbox);
        }
    }
}