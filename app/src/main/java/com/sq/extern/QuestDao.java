package com.sq.extern;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface QuestDao {
    @Query("SELECT * FROM quests WHERE city = :city AND durationMin <= :maxTime")
    List<Quest> getQuestsByCityAndTime(String city, int maxTime);

    @Query("SELECT * FROM quests WHERE city = :city AND durationMin == :targetTime")
    List<Quest> getQuestsByCityAndExactTime(String city, int targetTime);

    @Insert
    void insertAll(List<Quest> quests);

    @Query("SELECT COUNT(*) FROM quests WHERE city = :city")
    int getCountByCity(String city);

    @Query("UPDATE quests SET isCompleted = 1, photoPath = :photoPath, completionDate = :date WHERE uid = :uid")
    void completeQuest(int uid, String photoPath, String date);

    @Query("SELECT * FROM quests WHERE isCompleted = 1")
    List<Quest> getCompletedQuests();
}
