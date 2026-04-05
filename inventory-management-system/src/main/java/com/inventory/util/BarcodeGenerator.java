package com.inventory.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * Utility class for barcode generation using ZXing.
 */
@UtilityClass
@Slf4j
public class BarcodeGenerator {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 150;

    /**
     * Generate barcode image as Base64 string.
     */
    public static String generateBarcode(String barcode) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(barcode, BarcodeFormat.CODE_128, WIDTH, HEIGHT);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);

            byte[] imageBytes = baos.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            return "data:image/png;base64," + base64Image;
        } catch (Exception e) {
            log.error("Failed to generate barcode: {}", barcode, e);
            throw new RuntimeException("Barcode generation failed", e);
        }
    }

    /**
     * Generate barcode image and save to file system.
     */
    public static String generateBarcodeImage(String barcode, String filePath) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(barcode, BarcodeFormat.CODE_128, WIDTH, HEIGHT);
            MatrixToImageWriter.writeToFile(bitMatrix, "PNG", new java.io.File(filePath));
            return filePath;
        } catch (Exception e) {
            log.error("Failed to generate barcode image: {}", barcode, e);
            throw new RuntimeException("Barcode image generation failed", e);
        }
    }

    /**
     * Generate unique barcode based on product SKU and ID.
     */
    public static String generateUniqueBarcode(String sku, Long productId) {
        return String.format("%s-%d", sku, productId);
    }
}
