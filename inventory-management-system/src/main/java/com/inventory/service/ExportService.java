package com.inventory.service;

import com.inventory.model.Product;
import com.inventory.repository.ProductRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for exporting inventory data to CSV and PDF formats.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {
    private final ProductRepository productRepository;

    public byte[] exportProductsToCSV(List<Product> products) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(baos);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                     "ID", "Name", "SKU", "Category", "Price", "Quantity", "Reorder Level", "Status"))) {

            for (Product product : products) {
                csvPrinter.printRecord(
                        product.getId(),
                        product.getName(),
                        product.getSku(),
                        product.getCategory() != null ? product.getCategory().getName() : "N/A",
                        product.getPrice(),
                        product.getQuantity(),
                        product.getReorderLevel(),
                        product.isLowStock() ? "Low Stock" : "Normal"
                );
            }

            csvPrinter.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting products to CSV", e);
            throw new RuntimeException("CSV export failed", e);
        }
    }

    public byte[] exportProductsToPDF(List<Product> products) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Add title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("Inventory Report", titleFont));

            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 10);
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), subtitleFont));
            document.add(new Paragraph(" "));

            // Create table
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            String[] headers = {"ID", "Name", "SKU", "Category", "Price", "Quantity", "Reorder Lvl", "Status"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            // Add product rows
            for (Product product : products) {
                table.addCell(String.valueOf(product.getId()));
                table.addCell(product.getName());
                table.addCell(product.getSku());
                table.addCell(product.getCategory() != null ? product.getCategory().getName() : "N/A");
                table.addCell(String.valueOf(product.getPrice()));
                table.addCell(String.valueOf(product.getQuantity()));
                table.addCell(String.valueOf(product.getReorderLevel()));

                PdfPCell statusCell = new PdfPCell(new Phrase(
                        product.isLowStock() ? "Low Stock" : "Normal"));
                if (product.isLowStock()) {
                    statusCell.setBackgroundColor(new BaseColor(255, 200, 200));
                }
                table.addCell(statusCell);
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting products to PDF", e);
            throw new RuntimeException("PDF export failed", e);
        }
    }

    public byte[] exportInventoryTransactionsToPDF(LocalDateTime startDate, LocalDateTime endDate) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            document.add(new Paragraph("Inventory Transactions Report", titleFont));

            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 10);
            document.add(new Paragraph("Period: " + startDate + " to " + endDate, subtitleFont));
            document.add(new Paragraph("Generated: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), subtitleFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            String[] headers = {"Date", "Product", "Type", "Quantity", "Warehouse"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error exporting transactions to PDF", e);
            throw new RuntimeException("PDF export failed", e);
        }
    }
}
