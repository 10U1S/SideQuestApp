package com.sq.extern;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class CitySelectionActivity extends BaseActivity {

    private RecyclerView rvCities;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        setupToolbar(R.id.toolbar, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        rvCities = findViewById(R.id.rv_cities);
        rvCities.setLayoutManager(new LinearLayoutManager(this));

        // Initial mit leerer Location laden, damit die Liste sofort da ist
        updateList(null);
        checkLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Liste aktualisieren, wenn man aus den Settings zurückkommt (Radius-Änderung)
        loadCitiesWithLocation();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            loadCitiesWithLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadCitiesWithLocation();
        } else {
            // Ohne GPS einfach normal laden
            updateList(null);
        }
    }

    private void loadCitiesWithLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Suche Standort...", Toast.LENGTH_SHORT).show();
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    updateList(location);
                } else {
                    // Falls kein Cache-Standort da ist, frage einmal frisch ab
                    com.google.android.gms.location.LocationRequest request = com.google.android.gms.location.LocationRequest.create()
                            .setPriority(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY)
                            .setInterval(1000)
                            .setNumUpdates(1);
                    
                    fusedLocationClient.requestLocationUpdates(request, new com.google.android.gms.location.LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull com.google.android.gms.location.LocationResult locationResult) {
                            updateList(locationResult.getLastLocation());
                        }
                    }, android.os.Looper.getMainLooper());
                }
            });
        } else {
            updateList(null);
        }
    }

    private void updateList(Location userLocation) {
        List<CityItem> allCities = getCategorizedCities();
        List<CityItem> nearbyCities = new ArrayList<>();

        if (userLocation != null) {
            SharedPreferences prefs = getSharedPreferences("sidequest_prefs", MODE_PRIVATE);
            int radiusKm = prefs.getInt("pref_radius_km", 50);

            for (CityItem city : allCities) {
                float[] results = new float[1];
                Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(), city.lat, city.lon, results);
                city.distance = results[0] / 1000;

                if (city.distance <= radiusKm) {
                    CityItem nearby = new CityItem(city.name, city.countryEmoji, "📍 In deiner Nähe", city.lat, city.lon);
                    nearby.distance = city.distance;
                    nearbyCities.add(nearby);
                }
            }
            
            // Sortieren: Nächste Stadt zuerst
            java.util.Collections.sort(nearbyCities, (c1, c2) -> Float.compare(c1.distance, c2.distance));
        }

        List<Object> items = new ArrayList<>();
        String currentRegion = "";
        
        // Erst die nahen Städte
        if (!nearbyCities.isEmpty()) {
            items.add("📍 In deiner Nähe");
            items.addAll(nearbyCities);
        }

        // Dann die restlichen Kategorien
        for (CityItem city : allCities) {
            if (!city.region.equals(currentRegion)) {
                currentRegion = city.region;
                items.add(currentRegion);
            }
            items.add(city);
        }

        CityAdapter adapter = new CityAdapter(items, this::selectCity);
        rvCities.setAdapter(adapter);
    }

    private List<CityItem> getCategorizedCities() {
        List<CityItem> items = new ArrayList<>();
        
        // DACH Region (Standard-Set mit Koordinaten für GPS)
        // Deutschland
        items.add(new CityItem("Aachen", "🇩🇪", "DACH Region", 50.7753, 6.0839));
        items.add(new CityItem("Augsburg", "🇩🇪", "DACH Region", 48.3705, 10.8978));
        items.add(new CityItem("Berlin", "🇩🇪", "DACH Region", 52.5200, 13.4050));
        items.add(new CityItem("Bielefeld", "🇩🇪", "DACH Region", 52.0302, 8.5325));
        items.add(new CityItem("Bochum", "🇩🇪", "DACH Region", 51.4818, 7.2162));
        items.add(new CityItem("Bonn", "🇩🇪", "DACH Region", 50.7374, 7.0982));
        items.add(new CityItem("Braunschweig", "🇩🇪", "DACH Region", 52.2689, 10.5268));
        items.add(new CityItem("Bremen", "🇩🇪", "DACH Region", 53.0793, 8.8017));
        items.add(new CityItem("Chemnitz", "🇩🇪", "DACH Region", 50.8278, 12.9214));
        items.add(new CityItem("Dortmund", "🇩🇪", "DACH Region", 51.5136, 7.4653));
        items.add(new CityItem("Dresden", "🇩🇪", "DACH Region", 51.0504, 13.7373));
        items.add(new CityItem("Duisburg", "🇩🇪", "DACH Region", 51.4344, 6.7623));
        items.add(new CityItem("Düsseldorf", "🇩🇪", "DACH Region", 51.2277, 6.7735));
        items.add(new CityItem("Erfurt", "🇩🇪", "DACH Region", 50.9848, 11.0299));
        items.add(new CityItem("Essen", "🇩🇪", "DACH Region", 51.4556, 7.0116));
        items.add(new CityItem("Frankfurt", "🇩🇪", "DACH Region", 50.1109, 8.6821));
        items.add(new CityItem("Freiburg", "🇩🇪", "DACH Region", 47.9990, 7.8421));
        items.add(new CityItem("Hamburg", "🇩🇪", "DACH Region", 53.5511, 9.9937));
        items.add(new CityItem("Hannover", "🇩🇪", "DACH Region", 52.3759, 9.7320));
        items.add(new CityItem("Karlsruhe", "🇩🇪", "DACH Region", 49.0069, 8.4037));
        items.add(new CityItem("Kassel", "🇩🇪", "DACH Region", 51.3127, 9.4797));
        items.add(new CityItem("Kiel", "🇩🇪", "DACH Region", 54.3233, 10.1228));
        items.add(new CityItem("Köln", "🇩🇪", "DACH Region", 50.9375, 6.9603));
        items.add(new CityItem("Leipzig", "🇩🇪", "DACH Region", 51.3397, 12.3731));
        items.add(new CityItem("Mainz", "🇩🇪", "DACH Region", 49.9929, 8.2473));
        items.add(new CityItem("Mannheim", "🇩🇪", "DACH Region", 49.4875, 8.4660));
        items.add(new CityItem("Mönchengladbach", "🇩🇪", "DACH Region", 51.1805, 6.4428));
        items.add(new CityItem("München", "🇩🇪", "DACH Region", 48.1351, 11.5820));
        items.add(new CityItem("Münster", "🇩🇪", "DACH Region", 51.9607, 7.6261));
        items.add(new CityItem("Nürnberg", "🇩🇪", "DACH Region", 49.4521, 11.0767));
        items.add(new CityItem("Rostock", "🇩🇪", "DACH Region", 54.0924, 12.0991));
        items.add(new CityItem("Saarbrücken", "🇩🇪", "DACH Region", 49.2401, 6.9969));
        items.add(new CityItem("Stuttgart", "🇩🇪", "DACH Region", 48.7758, 9.1829));
        items.add(new CityItem("Wiesbaden", "🇩🇪", "DACH Region", 50.0782, 8.2398));
        items.add(new CityItem("Wuppertal", "🇩🇪", "DACH Region", 51.2562, 7.1508));
        // Österreich
        items.add(new CityItem("Graz", "🇦🇹", "DACH Region", 47.0707, 15.4395));
        items.add(new CityItem("Innsbruck", "🇦🇹", "DACH Region", 47.2692, 11.4041));
        items.add(new CityItem("Klagenfurt", "🇦🇹", "DACH Region", 46.6228, 14.3050));
        items.add(new CityItem("Linz", "🇦🇹", "DACH Region", 48.3069, 14.2858));
        items.add(new CityItem("Salzburg", "🇦🇹", "DACH Region", 47.8095, 13.0550));
        items.add(new CityItem("Villach", "🇦🇹", "DACH Region", 46.6111, 13.8558));
        items.add(new CityItem("Wels", "🇦🇹", "DACH Region", 48.1575, 14.0286));
        items.add(new CityItem("Wien", "🇦🇹", "DACH Region", 48.2082, 16.3738));
        // Schweiz
        items.add(new CityItem("Basel", "🇨🇭", "DACH Region", 47.5596, 7.5886));
        items.add(new CityItem("Bern", "🇨🇭", "DACH Region", 46.9480, 7.4474));
        items.add(new CityItem("Genf", "🇨🇭", "DACH Region", 46.2044, 6.1432));
        items.add(new CityItem("Lausanne", "🇨🇭", "DACH Region", 46.5197, 6.6323));
        items.add(new CityItem("Luzern", "🇨🇭", "DACH Region", 47.0502, 8.3093));
        items.add(new CityItem("Winterthur", "🇨🇭", "DACH Region", 47.5001, 8.7238));
        items.add(new CityItem("Zürich", "🇨🇭", "DACH Region", 47.3769, 8.5417));
        
        // Europa
        items.add(new CityItem("Amsterdam", "🇳🇱", "Rest Europa", 52.3676, 4.9041));
        items.add(new CityItem("Athen", "🇬🇷", "Rest Europa", 37.9838, 23.7275));
        items.add(new CityItem("Barcelona", "🇪🇸", "Rest Europa", 41.3851, 2.1734));
        items.add(new CityItem("Bratislava", "🇸🇰", "Rest Europa", 48.1486, 17.1077));
        items.add(new CityItem("Brüssel", "🇧🇪", "Rest Europa", 50.8503, 4.3517));
        items.add(new CityItem("Budapest", "🇭🇺", "Rest Europa", 47.4979, 19.0402));
        items.add(new CityItem("Dublin", "🇮🇪", "Rest Europa", 53.3498, -6.2603));
        items.add(new CityItem("Dubrovnik", "🇭🇷", "Rest Europa", 42.6403, 18.1101));
        items.add(new CityItem("Edinburgh", "🇬🇧", "Rest Europa", 55.9533, -3.1883));
        items.add(new CityItem("Florenz", "🇮🇹", "Rest Europa", 43.7696, 11.2558));
        items.add(new CityItem("Helsinki", "🇫🇮", "Rest Europa", 60.1699, 24.9384));
        items.add(new CityItem("Istanbul", "🇹🇷", "Rest Europa", 41.0082, 28.9784));
        items.add(new CityItem("Kopenhagen", "🇩🇰", "Rest Europa", 55.6761, 12.5683));
        items.add(new CityItem("Krakau", "🇵🇱", "Rest Europa", 50.0647, 19.9450));
        items.add(new CityItem("Lissabon", "🇵🇹", "Rest Europa", 38.7223, -9.1393));
        items.add(new CityItem("Ljubljana", "🇸🇮", "Rest Europa", 46.0569, 14.5058));
        items.add(new CityItem("London", "🇬🇧", "Rest Europa", 51.5074, -0.1278));
        items.add(new CityItem("Lyon", "🇫🇷", "Rest Europa", 45.7640, 4.8357));
        items.add(new CityItem("Madrid", "🇪🇸", "Rest Europa", 40.4168, -3.7038));
        items.add(new CityItem("Mailand", "🇮🇹", "Rest Europa", 45.4642, 9.1900));
        items.add(new CityItem("Oslo", "🇳🇴", "Rest Europa", 59.9139, 10.7522));
        items.add(new CityItem("Paris", "🇫🇷", "Rest Europa", 48.8566, 2.3522));
        items.add(new CityItem("Porto", "🇵🇹", "Rest Europa", 41.1579, -8.6291));
        items.add(new CityItem("Prag", "🇨🇿", "Rest Europa", 50.0755, 14.4378));
        items.add(new CityItem("Reykjavik", "🇮🇸", "Rest Europa", 64.1265, -21.8174));
        items.add(new CityItem("Rom", "🇮🇹", "Rest Europa", 41.9028, 12.4964));
        items.add(new CityItem("Sevilla", "🇪🇸", "Rest Europa", 37.3891, -5.9845));
        items.add(new CityItem("Stockholm", "🇸🇪", "Rest Europa", 59.3293, 18.0686));
        items.add(new CityItem("Venedig", "🇮🇹", "Rest Europa", 45.4408, 12.3155));
        items.add(new CityItem("Warschau", "🇵🇱", "Rest Europa", 52.2297, 21.0122));
        
        // Weltweit
        items.add(new CityItem("New York", "🇺🇸", "Rest der Welt", 40.7128, -74.0060));
        items.add(new CityItem("Tokio", "🇯🇵", "Rest der Welt", 35.6762, 139.6503));
        
        return items;
    }

    private void selectCity(String cityName) {
        // Zuerst prüfen ob wir lokale Daten haben
        QuestDao dao = AppDatabase.getInstance(this).questDao();
        if (dao.getCountByCity(cityName) == 0) {
            // Wenn keine lokalen Daten, versuche Firebase Sync
            Toast.makeText(this, "Lade Quests für " + cityName + "...", Toast.LENGTH_SHORT).show();
            QuestSyncManager.syncQuestsForCity(this, cityName, new QuestSyncManager.SyncCallback() {
                @Override
                public void onSyncComplete() {
                    // Falls Firebase leer war, Fallback auf CSV
                    if (dao.getCountByCity(cityName) == 0) {
                        CsvImporter.importCsvIfNecessary(CitySelectionActivity.this, cityName);
                    }
                    navigateToTimeSelection(cityName);
                }

                @Override
                public void onSyncFailed(Exception e) {
                    // Bei Fehler Fallback auf CSV
                    CsvImporter.importCsvIfNecessary(CitySelectionActivity.this, cityName);
                    navigateToTimeSelection(cityName);
                }
            });
        } else {
            navigateToTimeSelection(cityName);
        }
    }

    private void navigateToTimeSelection(String cityName) {
        Intent intent = new Intent(this, TimeSelectionActivity.class);
        intent.putExtra("CITY", cityName);
        startActivity(intent);
    }

    private static class CityItem {
        String name;
        String countryEmoji;
        String region; // "DACH", "Europa", "Welt"
        double lat;
        double lon;
        float distance;

        CityItem(String name, String countryEmoji, String region, double lat, double lon) {
            this.name = name;
            this.countryEmoji = countryEmoji;
            this.region = region;
            this.lat = lat;
            this.lon = lon;
        }
    }

    private static class CityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_CITY = 1;

        private final List<Object> items;
        private final OnCityClickListener listener;

        interface OnCityClickListener {
            void onCityClick(String cityName);
        }

        CityAdapter(List<Object> items, OnCityClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @Override
        public int getItemViewType(int position) {
            return (items.get(position) instanceof String) ? TYPE_HEADER : TYPE_CITY;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_header, parent, false);
                return new HeaderViewHolder(v);
            }
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
            return new CityViewHolder(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HEADER) {
                String region = (String) items.get(position);
                TextView tv = holder.itemView.findViewById(R.id.tv_header);
                tv.setText(region.toUpperCase());
                
            } else {
                CityItem city = (CityItem) items.get(position);
                CityViewHolder ch = (CityViewHolder) holder;
                ch.tvName.setText(city.countryEmoji + " " + city.name);

                // Scale Animation beim Drücken (Haptisches Feedback)
                ch.itemView.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    }
                    return false;
                });

                ch.itemView.setOnClickListener(v -> listener.onCityClick(city.name));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class HeaderViewHolder extends RecyclerView.ViewHolder {
            HeaderViewHolder(View v) { super(v); }
        }

        static class CityViewHolder extends RecyclerView.ViewHolder {
            TextView tvName;
            CityViewHolder(View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_city_name);
            }
        }
    }
}


