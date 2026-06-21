package com.sq.extern;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "quests")
public class Quest {
    @PrimaryKey(autoGenerate = true)
    public int uid;
    
    public String title;
    public String description;
    public int durationMin;
    public String rarity;
    public String category; // Neues Feld für die Kategorie
    public double lat;
    public double lon;
    public String city;
    public String photoPath; // Pfad zum Polaroid-Foto
    public boolean isCompleted;
    public String completionDate; // Datum der Fertigstellung

    // No-arg constructor for Firebase
    @Ignore
    public Quest() {}

    public Quest(String title, String description, int durationMin, String rarity, String category, double lat, double lon, String city) {
        this.title = title;
        this.description = description;
        this.durationMin = durationMin;
        this.rarity = rarity;
        this.category = category;
        this.lat = lat;
        this.lon = lon;
        this.city = city;
    }

    public String getRarityLabel() {
        if (durationMin <= 60) return "RARE";
        if (durationMin <= 240) return "EPIC";
        return "LEGENDARY";
    }

    public int getRarityColor() {
        if (durationMin <= 60) return 0xFF3498DB; // Blue
        if (durationMin <= 240) return 0xFF9B59B6; // Purple
        return 0xFFF1C40F; // Gold
    }
}
