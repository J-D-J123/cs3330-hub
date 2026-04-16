package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.haunted.model.Key;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;

/**
 * File:    TestQuestTracker.java
 * Author:  Jack Belleville
 * Desc:    Mutation-analysis tests for QuestTracker. Targets the null checks,
 *          the case-insensitive name match, and the "monster must be dead"
 *          branch.
 */
public class TestQuestTracker {

    private QuestTracker tracker;
    private Quest quest;

    @BeforeEach
    void setUp() {
        tracker = new QuestTracker();
        quest = new Quest("Test", "Test desc");
    }

    @Test
    void pickingUpLostGradebookMarksQuestFlag() {
        tracker.updateQuestForItem(quest, new QuestItem("Lost Gradebook", "d"));
        assertTrue(quest.isGradebookRecovered());
    }

    @Test
    void pickingUpLostGradebookIsCaseInsensitive() {
        tracker.updateQuestForItem(quest, new QuestItem("lost gradebook", "d"));
        assertTrue(quest.isGradebookRecovered());
    }

    @Test
    void pickingUpOtherItemDoesNotMarkGradebook() {
        tracker.updateQuestForItem(quest, new Key("Random Key", "d"));
        assertFalse(quest.isGradebookRecovered());
    }

    @Test
    void nullItemDoesNotCrashAndHasNoEffect() {
        tracker.updateQuestForItem(quest, null);
        assertFalse(quest.isGradebookRecovered());
    }

    @Test
    void killingFinalExamPhantomMarksPhantomFlag() {
        Monster phantom = new Monster("Final Exam Phantom", 1, 1, 1, List.of());
        phantom.takeDamage(100);
        tracker.updateQuestForMonster(quest, phantom);
        assertTrue(quest.isPhantomDefeated());
    }

    @Test
    void killingFinalExamPhantomIsCaseInsensitive() {
        Monster phantom = new Monster("FINAL EXAM PHANTOM", 1, 1, 1, List.of());
        phantom.takeDamage(100);
        tracker.updateQuestForMonster(quest, phantom);
        assertTrue(quest.isPhantomDefeated());
    }

    @Test
    void aliveFinalExamPhantomDoesNotMarkDefeated() {
        Monster phantom = new Monster("Final Exam Phantom", 10, 1, 1, List.of());
        // still alive
        tracker.updateQuestForMonster(quest, phantom);
        assertFalse(quest.isPhantomDefeated());
    }

    @Test
    void killingOtherMonsterDoesNotMarkPhantom() {
        Monster other = new Monster("Plagiarism Ghost", 1, 1, 1, List.of());
        other.takeDamage(100);
        tracker.updateQuestForMonster(quest, other);
        assertFalse(quest.isPhantomDefeated());
    }

    @Test
    void nullMonsterDoesNotCrash() {
        tracker.updateQuestForMonster(quest, null);
        assertFalse(quest.isPhantomDefeated());
    }

    @Test
    void updatingItemDoesNotTouchPotionName() {
        // potion named "Coffee Potion" should not satisfy the gradebook condition
        tracker.updateQuestForItem(quest, new Potion("Coffee Potion", "d", 10));
        assertFalse(quest.isGradebookRecovered());
    }
}
