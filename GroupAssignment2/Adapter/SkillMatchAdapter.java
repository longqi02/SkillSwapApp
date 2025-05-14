package com.example.GroupAssignment2.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.GroupAssignment2.Domain.SkillDomain;
import com.example.GroupAssignment2.R;

import java.util.ArrayList;
import java.util.List;

public class SkillMatchAdapter
        extends RecyclerView.Adapter<SkillMatchAdapter.ViewHolder> {

    public interface WishListener {
        void onWishClick(SkillDomain skill);
    }

    private final List<SkillDomain> items = new ArrayList<>();
    private final WishListener wishListener;

    public SkillMatchAdapter(WishListener listener) {
        this.wishListener = listener;
    }

    public void setItems(List<SkillDomain> skills) {
        items.clear();
        items.addAll(skills);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_skill_with_wishlist, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        SkillDomain s = items.get(position);
        h.tvName.setText(s.getTitle());
        h.tvDesc.setText(s.getDescription());
        h.btnWish.setOnClickListener(v -> wishListener.onWishClick(s));
        
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        Button btnWish;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvSkillName);
            tvDesc = v.findViewById(R.id.tvSkillDesc);
            btnWish= v.findViewById(R.id.btnSaveWishlist);
        }
    }
}