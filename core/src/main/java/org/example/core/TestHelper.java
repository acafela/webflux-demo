package org.example.core;

public class TestHelper {
    public static void delay() {
        try {
            Thread.sleep(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
