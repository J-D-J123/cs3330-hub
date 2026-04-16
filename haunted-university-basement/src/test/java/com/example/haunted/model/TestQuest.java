package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * File:    TestQuest.java
 * Author:  Jack Belleville
 * Desc:    Mutation-analysis tests for Quest. Targets the status-transition
 *          branches inside updateStatus (NOT_STARTED -> IN_PROGRESS ->
 *          COMPLETED) and the isComplete check.
 */
public class TestQuest {

    @Test
    void newQuestStartsNotStarted() {
        Quest q = new Quest("Escape", "desc");
        assertEquals(QuestStatus.NOT_STARTED, q.getStatus());
        assertFalse(q.isComplete());
        assertFalse(q.isGradebookRecovered());
        assertFalse(q.isPhantomDefeated());
    }

    @Test
    void gradebookAloneMovesQuestToInProgress() {
        Quest q = new Quest("Escape", "desc");
        q.markGradebookRecovered();
        assertTrue(q.isGradebookRecovered());
        assertEquals(QuestStatus.IN_PROGRESS, q.getStatus());
        assertFalse(q.isComplete());
    }

    @Test
    void phantomAloneMovesQuestToInProgress() {
        Quest q = new Quest("Escape", "desc");
        q.markPhantomDefeated();
        assertTrue(q.isPhantomDefeated());
        assertEquals(QuestStatus.IN_PROGRESS, q.getStatus());
        assertFalse(q.isComplete());
    }

    @Test
    void bothFlagsCompleteQuest() {
        Quest q = new Quest("Escape", "desc");
        q.markGradebookRecovered();
        q.markPhantomDefeated();
        assertEquals(QuestStatus.COMPLETED, q.getStatus());
        assertTrue(q.isComplete());
    }

    @Test
    void completingInReverseOrderAlsoWorks() {
        Quest q = new Quest("Escape", "desc");
        q.markPhantomDefeated();
        q.markGradebookRecovered();
        assertEquals(QuestStatus.COMPLETED, q.getStatus());
        assertTrue(q.isComplete());
    }

    @Test
    void nameAndDescriptionStoredAsGiven() {
        Quest q = new Quest("Name", "Desc");
        assertEquals("Name", q.getName());
        assertEquals("Desc", q.getDescription());
    }

    @Test
    void nullNameIsRejected() {
        assertThrows(NullPointerException.class, () -> new Quest(null, "d"));
    }

    @Test
    void nullDescriptionIsRejected() {
        assertThrows(NullPointerException.class, () -> new Quest("n", null));
    }

    @Test
    void updateStatusIsIdempotent() {
        Quest q = new Quest("Escape", "desc");
        q.markGradebookRecovered();
        q.markGradebookRecovered();
        assertEquals(QuestStatus.IN_PROGRESS, q.getStatus());
        assertTrue(q.isGradebookRecovered());
    }
}
