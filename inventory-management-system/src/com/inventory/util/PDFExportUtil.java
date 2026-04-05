package com.inventory.util;

import com.inventory.model.Product;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.List;

public class PDFExportUtil {
    public static void exportProductsToPDF(List<Product> products, String filePath) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        PdfPTable table = new PdfPTable(7);
        table.addCell("ID");
        table.addCell("Name");
        table.addCell("Category");
        table.addCell("Price");
        table.addCell("Quantity");
        table.addCell("SupplierID");
        table.addCell("ExpiryDate");
        for (Product p : products) {
            table.addCell(String.valueOf(p.getId()));
            table.addCell(p.getName());
            table.addCell(p.getCategory());
            table.addCell(String.valueOf(p.getPrice()));
            table.addCell(String.valueOf(p.getQuantity()));
            table.addCell(String.valueOf(p.getSupplierId()));
            table.addCell(p.getExpiryDate());
        }
        document.add(table);
        document.close();
    }
}
