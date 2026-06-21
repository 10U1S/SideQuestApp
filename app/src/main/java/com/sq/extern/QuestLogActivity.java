package com.sq.extern;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.util.List;

public class QuestLogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_log);

        setupToolbar(R.id.toolbar, false);

        RecyclerView rvLog = findViewById(R.id.rv_quest_log);
        rvLog.setLayoutManager(new GridLayoutManager(this, 2));

        List<Quest> completedQuests = AppDatabase.getInstance(this).questDao().getCompletedQuests();
        
        if (completedQuests.isEmpty()) {
            findViewById(R.id.tv_empty_log).setVisibility(View.VISIBLE);
            rvLog.setVisibility(View.GONE);
        } else {
            LogAdapter adapter = new LogAdapter(completedQuests, this::showDetailDialog);
            rvLog.setAdapter(adapter);
        }
    }

    private void showDetailDialog(Quest quest) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_polaroid_detail);
        
        if (dialog.getWindow() != null) {
            // Hintergrund des Fensters transparent machen, damit das Layout-Overlay wirkt
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Auf volle Größe setzen für das zentrierte Overlay
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // System-Dimming (Standard-Dialog-Schatten) beibehalten
            dialog.getWindow().setDimAmount(0.7f);
        }

        View polaroidCard = dialog.findViewById(R.id.polaroid_card_container);
        if (polaroidCard != null) {
            polaroidCard.setScaleX(0.1f);
            polaroidCard.setScaleY(0.1f);
            polaroidCard.animate().scaleX(1f).scaleY(1f).setDuration(400).setInterpolator(new android.view.animation.OvershootInterpolator()).start();
        }

        ImageView iv = dialog.findViewById(R.id.iv_detail_photo);
        TextView tv = dialog.findViewById(R.id.tv_detail_info);
        View rarity = dialog.findViewById(R.id.detail_rarity_indicator);
        
        if (quest.photoPath != null) {
            Glide.with(this).load(new File(quest.photoPath)).into(iv);
        }
        
        String info = quest.title;
        if (quest.completionDate != null) {
            info += " • " + quest.completionDate;
        }
        tv.setText(info);
        applyRarityGlow(rarity, quest.getRarityColor());
        
        // Klick auf das Overlay um das Polaroid herum schließt den Dialog
        View overlay = (View) polaroidCard.getParent();
        overlay.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

    private static class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
        private final List<Quest> quests;
        private final OnQuestClickListener listener;

        interface OnQuestClickListener {
            void onQuestClick(Quest quest);
        }

        LogAdapter(List<Quest> quests, OnQuestClickListener listener) {
            this.quests = quests;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_polaroid, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Quest q = quests.get(position);
            String displayInfo = q.title;
            if (q.completionDate != null) {
                displayInfo += "\n" + q.completionDate;
            }
            holder.tvTitle.setText(displayInfo);
            
            if (q.photoPath != null) {
                Glide.with(holder.itemView.getContext())
                        .load(new File(q.photoPath))
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .centerCrop()
                        .into(holder.ivPhoto);
            }
            
            applyRarityGlow(holder.rarityMark, q.getRarityColor());
            holder.itemView.setRotation((float) (Math.random() * 10 - 5));
            holder.itemView.setOnClickListener(v -> listener.onQuestClick(q));
        }

        @Override
        public int getItemCount() {
            return quests.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPhoto;
            TextView tvTitle;
            View rarityMark;

            ViewHolder(View itemView) {
                super(itemView);
                ivPhoto = itemView.findViewById(R.id.iv_log_photo);
                tvTitle = itemView.findViewById(R.id.tv_log_title);
                rarityMark = itemView.findViewById(R.id.rarity_indicator_dot);
            }
        }
    }
}
