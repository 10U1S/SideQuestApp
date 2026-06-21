package com.sq.extern;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvImporter {

    public static void importCsvIfNecessary(Context context, String cityName) {
        QuestDao dao = AppDatabase.getInstance(context).questDao();
        
        if (dao.getCountByCity(cityName) > 0) {
            return; 
        }

        String fileName = cityName.toLowerCase()
                .replace("ü", "u")
                .replace("ö", "o")
                .replace("ä", "a")
                .replace(" ", "_") + ".csv";
        List<Quest> quests = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(fileName)))) {
            
            String line = reader.readLine(); // Header überspringen
            while ((line = reader.readLine()) != null) {
                // Einfaches Parsing für Komma-getrennte Werte (beachtet keine komplexen Quotes, 
                // aber reicht für unsere strukturierte Eingabe)
                // Nutze Regex um nur Kommas außerhalb von Anführungszeichen zu treffen
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (parts.length >= 8) {
                    // parts[0] ist die ID aus der CSV, die wir ignorieren (UID wird von Room generiert)
                    String title = clean(parts[1]);
                    String desc = clean(parts[2]);
                    int duration = Integer.parseInt(parts[3].trim());
                    String rarity = clean(parts[4]);
                    String category = clean(parts[5]);
                    double lat = Double.parseDouble(parts[6].trim());
                    double lon = Double.parseDouble(parts[7].trim());
                    
                    // Automatisches Rarity-Fixing basierend auf User-Wunsch
                    String fixedRarity;
                    if (duration <= 60) fixedRarity = "Rare";
                    else if (duration <= 240) fixedRarity = "Epic";
                    else fixedRarity = "Legendary";
                    
                    quests.add(new Quest(title, desc, duration, fixedRarity, category, lat, lon, cityName));
                }
            }
            dao.insertAll(quests);
            Log.d("CsvImporter", "Importiert: " + quests.size() + " Quests für " + cityName);
            
        } catch (Exception e) {
            Log.e("CsvImporter", "Fehler beim Importieren von " + fileName, e);
        }
    }

    private static String clean(String input) {
        if (input == null) return "";
        String result = input.trim();
        if (result.startsWith("\"") && result.endsWith("\"")) {
            result = result.substring(1, result.length() - 1);
        }
        return result.replace("\"\"", "\""); // Doppelte Quotes zurückwandeln
    }
}