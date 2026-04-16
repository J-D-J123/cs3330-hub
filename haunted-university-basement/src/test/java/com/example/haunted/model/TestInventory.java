package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * File:    TestInventory.java
 * Author:  Jack Belleville
 * Desc:    Mutation analysis tests for Inventory. Targets the isFull boundary
 *          (>= capacity), case-insensitive item lookup, and removeItem edge
 *          cases (present/absent).
 */
public class TestInventory {

    @Test
    void newInventoryIsEmptyButNotFull() {
        Inventory inv = new Inventory(3);
        assertEquals(3, inv.getCapacity());
        assertFalse(inv.isFull());
        assertTrue(inv.getItems().isEmpty());
    }

    @Test
    void addItemReturnsTrueWhenSpaceAvailable() {
        Inventory inv = new Inventory(2);
        assertTrue(inv.addItem(new Key("A", "d")));
        assertTrue(inv.addItem(new Key("B", "d")));
        assertEquals(2, inv.getItems().size());
    }

    @Test
    void addItemReturnsFalseWhenFull() {
        Inventory inv = new Inventory(1);
        assertTrue(inv.addItem(new Key("A", "d")));
        assertFalse(inv.addItem(new Key("B", "d")));
        assertEquals(1, inv.getItems().size());
    }

    @Test
    void isFullBoundary() {
        Inventory inv = new Inventory(2);
        assertFalse(inv.isFull());
        inv.addItem(new Key("A", "d"));
        assertFalse(inv.isFull());
        inv.addItem(new Key("B", "d"));
        assertTrue(inv.isFull()); // boundary: size == capacity
    }

    @Test
    void zeroCapacityInventoryIsAlwaysFull() {
        Inventory inv = new Inventory(0);
        assertTrue(inv.isFull());
        assertFalse(inv.addItem(new Key("A", "d")));
    }

    @Test
    void findItemIsCaseInsensitive() {
        Inventory inv = new Inventory(3);
        inv.addItem(new Key("Archive Key", "d"));
        assertTrue(inv.findItem("archive key").isPresent());
        assertTrue(inv.findItem("ARCHIVE KEY").isPresent());
        assertTrue(inv.findItem("Archive Key").isPresent());
        assertTrue(inv.findItem("missing").isEmpty());
    }

    @Test
    void removeItemReturnsItemWhenPresent() {
        Inventory inv = new Inventory(3);
        Key k = new Key("A", "d");
        inv.addItem(k);
        Item removed = inv.removeItem("a");
        assertSame(k, removed);
        assertFalse(inv.contains("A"));
    }

    @Test
    void removeItemReturnsNullWhenAbsent() {
        Inventory inv = new Inventory(3);
        assertNull(inv.removeItem("nope"));
    }

    @Test
    void containsReturnsAccurateResult() {
        Inventory inv = new Inventory(3);
        inv.addItem(new Key("A", "d"));
        assertTrue(inv.contains("a"));
        assertFalse(inv.contains("b"));
    }

    @Test
    void getItemsReturnsUnmodifiableView() {
        Inventory inv = new Inventory(3);
        inv.addItem(new Key("A", "d"));
        assertThrows(UnsupportedOperationException.class,
                () -> inv.getItems().add(new Key("B", "d")));
    }
}
