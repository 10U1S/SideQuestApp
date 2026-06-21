package com.sq.extern;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseActivity {

    private SharedPreferences prefs;
    private TextView tvRadiusLabel;
    private ActivityResultLauncher<String> csvPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("sidequest_prefs", MODE_PRIVATE);

        setupToolbar();
        setupQuestsSection();
        setupDataSection();
        setupAppSection();
        setupFilePicker();
    }

    private void setupToolbar() {
        setupToolbar(R.id.settings_toolbar, false);
    }

    private void setupQuestsSection() {
        tvRadiusLabel = findViewById(R.id.tv_radius_label);
        SeekBar seekBarRadius = findViewById(R.id.seekbar_radius);
        
        int currentRadius = prefs.getInt("pref_radius_km", 50);
        updateRadiusText(currentRadius);
        seekBarRadius.setProgress(currentRadius - 10);

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = progress + 10;
                updateRadiusText(radius);
                prefs.edit().putInt("pref_radius_km", radius).apply();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateRadiusText(int radius) {
        tvRadiusLabel.setText("Quest-Radius: " + radius + " km");
    }

    private void setupDataSection() {
        Button btnImport = findViewById(R.id.btn_import_csv);
        btnImport.setOnClickListener(v -> csvPickerLauncher.launch("text/*"));

        Button btnUpload = findViewById(R.id.btn_upload_to_firebase);
        btnUpload.setOnClickListener(v -> {
            Toast.makeText(this, "Upload gestartet...", Toast.LENGTH_SHORT).show();
            new Thread(() -> {
                QuestSyncManager.uploadLocalDataToFirebase(this);
                runOnUiThread(() -> Toast.makeText(this, "Upload abgeschlossen!", Toast.LENGTH_SHORT).show());
            }).start();
        });

        Button btnDelete = findViewById(R.id.btn_delete_all);
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void setupAppSection() {
        TextView tvVersion = findViewById(R.id.tv_app_version);
        tvVersion.setText("Version: " + BuildConfig.VERSION_NAME);
    }

    private void setupFilePicker() {
        csvPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    importCustomCsv(uri);
                }
            }
        );
    }

    private void importCustomCsv(Uri uri) {
        try (InputStream is = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            
            List<Quest> quests = new ArrayList<>();
            String line = reader.readLine(); // Header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length >= 8) {
                    quests.add(new Quest(
                        clean(parts[1]), clean(parts[2]),
                        Integer.parseInt(parts[3].trim()),
                        clean(parts[4]), clean(parts[5]),
                        Double.parseDouble(parts[6].trim()),
                        Double.parseDouble(parts[7].trim()),
                        "Custom Import"
                    ));
                }
            }
            
            new Thread(() -> {
                AppDatabase.getInstance(this).questDao().insertAll(quests);
                runOnUiThread(() -> Toast.makeText(this, quests.size() + " Quests importiert!", Toast.LENGTH_SHORT).show());
            }).start();

        } catch (Exception e) {
            Toast.makeText(this, "Fehler beim Import!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
            .setTitle("Daten löschen")
            .setMessage("Möchtest du wirklich alle importierten Quests aus der Datenbank entfernen?")
            .setPositiveButton("Löschen", (dialog, which) -> {
                new Thread(() -> {
                    // Hier löschen wir alles aus der Quests Tabelle
                    // Wir können das DAO erweitern oder die DB einfach wipen
                    // Für diesen Scope löschen wir alles:
                    AppDatabase.getInstance(this).clearAllTables();
                    runOnUiThread(() -> Toast.makeText(this, "Datenbank geleert.", Toast.LENGTH_SHORT).show());
                }).start();
            })
            .setNegativeButton("Abbrechen", null)
            .show();
    }

    private String clean(String input) {
        if (input == null) return "";
        String result = input.trim();
        if (result.startsWith("\"") && result.endsWith("\"")) {
            result = result.substring(1, result.length() - 1);
        }
        return result.replace("\"\"", "\"");
    }
}