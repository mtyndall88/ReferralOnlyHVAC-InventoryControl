package edu.rasmussenuniversity.cs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Product {
	
	// Logger
	private static final Logger log = AppLogger.getLogger();
	
	public String getAllProducts() {
		StringBuilder result = new StringBuilder();
		
		try {
			Connection connect = DBConnection.getInstance(); // Singleton: one shared DB connection
			Statement statement = connect.createStatement();
			ResultSet rs = statement.executeQuery(
					"SELECT id, sku, name, price, stock_qty, reorder_level, active FROM products");
				
			while (rs.next()) {
				long id = rs.getLong("id");
				String sku = rs.getString("sku");
				String name = rs.getString("name");
                double price = rs.getDouble("price");
                int qty = rs.getInt("stock_qty");
                int rl = rs.getInt("reorder_level");
                boolean active = rs.getBoolean("active");
                
                String row = String.format("ID: %d  SKU: %s  Name: %s  Price: $%s  Qty: %d  Reorder: %d %n",
                        id, sku, name, price, qty, rl, active ? "Yes" : "No");
                
                result.append(row).append(System.lineSeparator());
                System.out.println(row);
			}
			// Log for getAllProducts retrieving successfully
			log.info("getAllProducts executed sucessfully");
		}
		catch(Exception ex) {
			// Log for exception in getAllProducts
			log.log(Level.SEVERE, "Error in getAllProducts", ex);
		}
		return result.toString();
	}
	
	// Product search DB connection coming later
	public void searchProduct(String name) {
		System.out.println("Searching for product by name: " + name + " (DB coming later)");
		log.info("Search requested for product: " + name);
	}
	
	// Add product DB connection coming later
	public void addProduct(String sku, String name, String description, double price, int reorderLevel) {
		System.out.printf("Add product: (DB coming later) %s %s %n", sku, name);
		// Log for adding a product
		log.info("Add product requested: " + name + ", SKU: " +sku);
	}
	
	// Update product DB connection coming later
	public void updateProduct(long id, double price, int reorderLevel) {
		System.out.printf("Update product (DB coming later) ID: %d %n", id);
		// Log for updating a product
		log.info("Update requested for ID: " + id + ", price: " + price + ", reorder: " + reorderLevel);
	}
	
	// Deactivate product DB connection coming later
	public void deactivateProduct(long id, String reason) {
		System.out.printf("Deactivate product (DB coming later) ID: %d, Reason: %s %n", id, reason);
		// Log for deactivating a product
		log.info("Deactivate requested for ID: " + id + ", reason: " + reason);
	}
}
