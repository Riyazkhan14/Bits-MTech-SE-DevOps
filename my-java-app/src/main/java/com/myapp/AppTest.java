package com.myapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class AppTest {
    @Test
    public void testAddition() {
        assertEquals(5, App.add(2, 3));
    }
}