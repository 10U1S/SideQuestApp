package com.sq.extern;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestRepository {

    /**
     * Holt eine zufällige Quest aus der Room-Datenbank basierend auf Stadt und Zeit.
     */
    public static Quest getRandomQuestFromDb(Context context, String city, int targetMinutes) {
        QuestDao dao = AppDatabase.getInstance(context).questDao();
        List<Quest> filtered;
        
        if (targetMinutes == 60) {
            filtered = dao.getQuestsByCityAndTime(city, 60);
        } else if (targetMinutes == 240) {
            // Für das mittlere Intervall filtern wir hier manuell nach dem Laden aus der DB 
            // oder erweitern das DAO. Hier der Einfachheit halber:
            List<Quest> allCity = dao.getQuestsByCityAndTime(city, 240);
            filtered = new ArrayList<>();
            for(Quest q : allCity) {
                if(q.durationMin > 60) filtered.add(q);
            }
        } else {
            // Ganzer Tag
            List<Quest> allCity = dao.getQuestsByCityAndTime(city, 2000);
            filtered = new ArrayList<>();
            for(Quest q : allCity) {
                if(q.durationMin > 240) filtered.add(q);
            }
        }
        
        if (filtered == null || filtered.isEmpty()) return null;
        
        return filtered.get(new Random().nextInt(filtered.size()));
    }
}