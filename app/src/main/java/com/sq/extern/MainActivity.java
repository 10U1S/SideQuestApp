package com.sq.extern;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hier wird die Verbindung zum Layout hergestellt
        setContentView(R.layout.activity_main);

        // 1. Buttons aus dem XML-Layout finden (per ID)
        Button btn30Min = findViewById(R.id.btn_30min);
        Button btn1Hour = findViewById(R.id.btn_1hour);

        // 2. Klick-Event für den 30-Minuten-Button
        btn30Min.setOnClickListener(v -> {
            startQuest(30); // Startet die Methode unten mit dem Wert 30
        });

        // 3. Klick-Event für den 1-Stunden-Button
        btn1Hour.setOnClickListener(v -> {
            startQuest(60); // Startet die Methode unten mit dem Wert 60
        });
    }

    /**
     * Diese Methode öffnet die QuestActivity und übergibt die gewählte Zeit.
     */
    private void startQuest(int minutes) {
        // Ein Intent ist der "Befehl", eine neue Activity zu öffnen
        Intent intent = new Intent(MainActivity.this, QuestActivity.class);

        // Wir geben der nächsten Activity eine Info mit (die Minuten)
        intent.putExtra("MINUTES", minutes);

        startActivity(intent);
    }
}