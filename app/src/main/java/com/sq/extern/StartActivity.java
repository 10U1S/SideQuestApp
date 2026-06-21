package com.sq.extern;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        setupToolbar(R.id.toolbar, true);

        Button btnStart = findViewById(R.id.btn_start_app);
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, CitySelectionActivity.class);
            startActivity(intent);
        });

        Button btnLogbook = findViewById(R.id.btn_open_logbook);
        btnLogbook.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, QuestLogActivity.class);
            startActivity(intent);
        });

        addPressAnimation(btnStart, btnLogbook);
    }
}
