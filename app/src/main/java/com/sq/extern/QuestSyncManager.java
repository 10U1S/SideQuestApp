package com.sq.extern;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class QuestSyncManager {

    public interface SyncCallback {
        void onSyncComplete();
        void onSyncFailed(Exception e);
    }

    public static void syncQuestsForCity(Context context, String cityName, SyncCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String cityDocId = cityName.toLowerCase()
                .replace("ü", "u")
                .replace("ö", "o")
                .replace("ä", "a")
                .replace(" ", "_");

        db.collection("cities").document(cityDocId).collection("quests")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onSyncComplete();
                        return;
                    }
                    
                    List<Quest> remoteQuests = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Quest quest = document.toObject(Quest.class);
                        quest.city = cityName; 
                        
                        // Rarity Normalisierung beim Download
                        if (quest.durationMin <= 60) quest.rarity = "Rare";
                        else if (quest.durationMin <= 240) quest.rarity = "Epic";
                        else quest.rarity = "Legendary";

                        remoteQuests.add(quest);
                    }

                    if (!remoteQuests.isEmpty()) {
                        // Save to local database
                        AppDatabase localDb = AppDatabase.getInstance(context);
                        localDb.questDao().insertAll(remoteQuests);
                        Log.d("QuestSyncManager", "Synced " + remoteQuests.size() + " quests from Firebase for " + cityName);
                    }
                    callback.onSyncComplete();
                })
                .addOnFailureListener(e -> {
                    Log.e("QuestSyncManager", "Error syncing from Firebase", e);
                    callback.onSyncFailed(e);
                });
    }

    /**
     * Einmalige Funktion, um alle lokalen CSV-Daten zu Firebase hochzuladen.
     * Nutzt Batches für höhere Zuverlässigkeit.
     */
    public static void uploadLocalDataToFirebase(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] cities = {
                // Deutschland
                "aachen", "augsburg", "berlin", "bielefeld", "bochum", "bonn", "braunschweig",
                "bremen", "chemnitz", "dortmund", "dresden", "duisburg", "dusseldorf", "erfurt",
                "essen", "frankfurt", "freiburg", "hamburg", "hannover", "karlsruhe", "kassel",
                "kiel", "koln", "leipzig", "mainz", "mannheim", "monchengladbach", "munchen",
                "munster", "nurnberg", "rostock", "saarbrucken", "stuttgart", "wiesbaden", "wuppertal",
                // Österreich
                "graz", "innsbruck", "klagenfurt", "linz", "salzburg", "villach", "wels", "wien",
                // Schweiz
                "basel", "bern", "genf", "lausanne", "luzern", "winterthur", "zurich",
                // Rest Europa
                "amsterdam", "athen", "barcelona", "bratislava", "brussel", "budapest", "dublin", "dubrovnik", "edinburgh", "florenz", "helsinki", "istanbul", "kopenhagen", "krakau", "lissabon", "ljubljana", "london", "lyon", "madrid", "mailand", "oslo", "paris", "porto", "prag", "reykjavik", "rom", "sevilla", "stockholm", "venedig", "warschau"
        };

        for (String cityName : cities) {
            List<Quest> quests = loadQuestsFromCsv(context, cityName);
            if (quests.isEmpty()) {
                Log.e("Sync", "No quests found for " + cityName);
                continue;
            }

            com.google.firebase.firestore.WriteBatch batch = db.batch();
            for (int i = 0; i < quests.size(); i++) {
                Quest q = quests.get(i);
                // Wir nutzen "stadt_id" als Dokumentnamen um Duplikate zu vermeiden
                String docId = String.valueOf(i + 1); 
                batch.set(db.collection("cities").document(cityName).collection("quests").document(docId), q);
            }

            batch.commit()
                    .addOnSuccessListener(aVoid -> Log.d("Sync", "Successfully uploaded all quests for " + cityName))
                    .addOnFailureListener(e -> Log.e("Sync", "Failed to upload quests for " + cityName, e));
        }
    }

    private static List<Quest> loadQuestsFromCsv(Context context, String cityName) {
        List<Quest> quests = new ArrayList<>();
        String fileName = cityName + ".csv";
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(context.getAssets().open(fileName)))) {
            String line = reader.readLine(); // Header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length >= 8) {
                    int duration = Integer.parseInt(parts[3].trim());
                    String rarity;
                    if (duration <= 60) rarity = "Rare";
                    else if (duration <= 240) rarity = "Epic";
                    else rarity = "Legendary";

                    quests.add(new Quest(clean(parts[1]), clean(parts[2]), duration, rarity, clean(parts[5]), 
                            Double.parseDouble(parts[6].trim()), Double.parseDouble(parts[7].trim()), cityName));
                }
            }
        } catch (Exception e) {
            Log.e("QuestSyncManager", "Error reading " + fileName, e);
        }
        return quests;
    }

    private static String clean(String input) {
        if (input == null) return "";
        String result = input.trim();
        if (result.startsWith("\"") && result.endsWith("\"")) {
            result = result.substring(1, result.length() - 1);
        }
        return result.replace("\"\"", "\"");
    }
}

