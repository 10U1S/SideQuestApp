package com.sq.extern;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends BaseActivity {
    private MapView map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Daten aus dem Intent holen
        double lat = getIntent().getDoubleExtra("LAT", 48.1351);
        double lon = getIntent().getDoubleExtra("LON", 11.5820);
        String title = getIntent().getStringExtra("TITLE");

        // UI Elemente setzen
        TextView titleView = findViewById(R.id.map_quest_title);
        titleView.setText(title);

        Button btnBack = findViewById(R.id.btn_back);
        Button btnComplete = findViewById(R.id.btn_complete);
        Button btnGoogleMaps = findViewById(R.id.btn_google_maps);

        btnBack.setOnClickListener(v -> finish());
        
        btnComplete.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, CompletionActivity.class);
            intent.putExtra("QUEST_UID", getIntent().getIntExtra("QUEST_UID", -1));
            intent.putExtra("QUEST_TITLE", title);
            // Für die Dauer müssten wir sie entweder übergeben oder aus der DB holen
            // Da wir sie hier nicht haben, übergeben wir sie als 0 (wird dann Rare)
            startActivity(intent);
        });

        btnGoogleMaps.setOnClickListener(v -> {
            String uri = "google.navigation:q=" + lat + "," + lon;
            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                // Fallback if Google Maps is not installed
                startActivity(new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lon)));
            }
        });

        // Karten Setup
        Configuration.getInstance().setUserAgentValue(getPackageName());
        map = findViewById(R.id.mapview);
        map.setMultiTouchControls(true);
        
        GeoPoint questPoint = new GeoPoint(lat, lon);
        map.getController().setZoom(17.0);
        map.getController().setCenter(questPoint);

        // Marker setzen
        Marker questMarker = new Marker(map);
        questMarker.setPosition(questPoint);
        questMarker.setTitle(title);
        questMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(questMarker);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}