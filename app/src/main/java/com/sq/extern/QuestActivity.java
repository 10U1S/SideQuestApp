package com.sq.extern;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QuestActivity extends BaseActivity {

    private Quest currentQuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);

        setupToolbar(R.id.toolbar, false);

        // Daten aus der Room-Datenbank laden
        int timeLimit = getIntent().getIntExtra("MINUTES", 30);
        String city = getIntent().getStringExtra("CITY");
        currentQuest = QuestRepository.getRandomQuestFromDb(this, city, timeLimit);

        if (currentQuest == null) {
            // Fallback falls Datenbank leer ist (sollte durch CSV Import verhindert werden)
            currentQuest = new Quest("Keine Quest gefunden", "Leider gibt es für diese Stadt/Zeit noch keine Aufgaben.", 0, "None", "None", 0.0, 0.0, city);
        }

        TextView title = findViewById(R.id.quest_title);
        TextView category = findViewById(R.id.quest_category);
        TextView description = findViewById(R.id.quest_description);
        TextView tvWeather = findViewById(R.id.tv_weather_info);
        Button btnShowMap = findViewById(R.id.btn_show_map);
        Button btnComplete = findViewById(R.id.btn_complete_quest);

        title.setText(currentQuest.title);
        category.setText(currentQuest.category + " • " + currentQuest.getRarityLabel());
        description.setText(currentQuest.description);

        // Apply Rarity Styling
        int rarityColor = currentQuest.getRarityColor();
        category.setTextColor(rarityColor);
        category.setBackgroundColor((rarityColor & 0x00FFFFFF) | 0x20000000); // 20% Alpha

        if ("LEGENDARY".equals(currentQuest.getRarityLabel())) {
            findViewById(R.id.quest_card_container).startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.pulse)
            );
        }

        // Wetter abrufen
        WeatherService.getWeather(currentQuest.lat, currentQuest.lon, new WeatherService.WeatherCallback() {
            @Override
            public void onSuccess(double temp, String desc, boolean isRaining) {
                tvWeather.setText(String.format("Aktuell: %.1f°C, %s", temp, desc));
                if (isRaining && !currentQuest.category.toLowerCase().contains("indoor")) {
                    tvWeather.append("\n⚠️ Achtung: Es regnet! Outdoor-Quest.");
                    tvWeather.setTextColor(android.graphics.Color.RED);
                }
            }
            @Override
            public void onError(Exception e) {
                tvWeather.setText("Wetter nicht verfügbar");
            }
        });

        btnShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(QuestActivity.this, MapActivity.class);
            intent.putExtra("LAT", currentQuest.lat);
            intent.putExtra("LON", currentQuest.lon);
            intent.putExtra("TITLE", currentQuest.title);
            intent.putExtra("QUEST_UID", currentQuest.uid);
            startActivity(intent);
        });

        btnComplete.setOnClickListener(v -> {
            Intent intent = new Intent(QuestActivity.this, CompletionActivity.class);
            intent.putExtra("QUEST_UID", currentQuest.uid);
            intent.putExtra("QUEST_TITLE", currentQuest.title);
            intent.putExtra("QUEST_DURATION", currentQuest.durationMin);
            startActivity(intent);
        });

        addPressAnimation(btnShowMap, btnComplete);
    }


}
