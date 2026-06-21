package com.sq.extern;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TimeSelectionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_selection);

        setupToolbar(R.id.toolbar, false);

        Button btnShort = findViewById(R.id.btn_short);
        Button btnMedium = findViewById(R.id.btn_medium);
        Button btnAllDay = findViewById(R.id.btn_all_day);

        btnShort.setOnClickListener(v -> startQuest(60));
        btnMedium.setOnClickListener(v -> startQuest(240));
        btnAllDay.setOnClickListener(v -> startQuest(1440));

        addPressAnimation(btnShort, btnMedium, btnAllDay);
    }

    private void startQuest(int minutes) {
        String selectedCity = getIntent().getStringExtra("CITY");
        Intent intent = new Intent(TimeSelectionActivity.this, QuestActivity.class);
        intent.putExtra("MINUTES", minutes);
        intent.putExtra("CITY", selectedCity);
        startActivity(intent);
    }
}
