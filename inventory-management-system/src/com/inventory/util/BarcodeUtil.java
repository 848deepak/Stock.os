package com.inventory.util;

public class BarcodeUtil {
    // Simulate barcode generation (returns a simple code)
    public static String generateBarcode(int productId) {
        return "BC-" + productId + "-" + System.currentTimeMillis();
    }
}
