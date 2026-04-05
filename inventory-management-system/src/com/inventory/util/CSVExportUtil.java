package com.inventory.util;

import com.inventory.model.Product;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExportUtil {
    public static void exportProductsToCSV(List<Product> products, String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write("ID,Name,Category,Price,Quantity,SupplierID,ExpiryDate\n");
        for (Product p : products) {
            writer.write(p.getId() + "," + p.getName() + "," + p.getCategory() + "," + p.getPrice() + "," + p.getQuantity() + "," + p.getSupplierId() + "," + p.getExpiryDate() + "\n");
        }
        writer.close();
    }
}
