package com.sq.extern;

import org.junit.Test;
import static org.junit.Assert.*;

public class QuestRepositoryTest {

    @Test
    public void getRandomQuest_ShortInterval_ReturnsCorrectQuest() {
        // Test für "Bis 1 Stunde" (60 Minuten)
        Quest quest = QuestRepository.getRandomQuest(60);
        assertNotNull(quest);
        assertTrue("Quest sollte maximal 60 Minuten dauern", quest.durationMin <= 60);
    }

    @Test
    public void getRandomQuest_MediumInterval_ReturnsCorrectQuest() {
        // Test für "2 - 4 Stunden" (240 Minuten)
        Quest quest = QuestRepository.getRandomQuest(240);
        assertNotNull(quest);
        assertTrue("Quest sollte zwischen 61 und 240 Minuten dauern", 
                quest.durationMin > 60 && quest.durationMin <= 240);
    }

    @Test
    public void getRandomQuest_LongInterval_ReturnsCorrectQuest() {
        // Test für "Ganzer Tag" (1440 Minuten)
        Quest quest = QuestRepository.getRandomQuest(1440);
        assertNotNull(quest);
        assertTrue("Quest sollte länger als 240 Minuten dauern", quest.durationMin > 240);
    }
}