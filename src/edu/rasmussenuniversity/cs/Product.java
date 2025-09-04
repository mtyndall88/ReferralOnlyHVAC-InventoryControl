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
	
	public String getAllProducts() throws SQLException {
		System.out.println("All Products" + "\n");
		StringBuilder result = new StringBuilder();
		
		try {
			Connection connect = DBConnection.getInstance(); // Singleton: one shared DB connection
			String sql = "SELECT id, sku, name, price, stock_qty, reorder_level, active FROM products";
			
			try (Statement statement = connect.createStatement();
					ResultSet rs = statement.executeQuery(sql)) {
					while (rs.next()) {
						long id = rs.getLong("id");
						String sku = rs.getString("sku");
						String name = rs.getString("name");
						double price = rs.getDouble("price");
						int qty = rs.getInt("stock_qty");
						int rl = rs.getInt("reorder_level");
						boolean active = rs.getBoolean("active");
                
						String row = String.format("ID: %d  SKU: %s  Name: %s  Price: $%.2f  Qty: %d  Reorder: %d  Active: %s",
                        id, sku, name, price, qty, rl, active ? "Yes" : "No");
                
						result.append(row).append(System.lineSeparator());
						System.out.println(row);
				}
			}
			// Log for getAllProducts retrieving successfully
			log.info("getAllProducts executed successfully");
		}
		catch (Exception ex) {
			// Log for exception in getAllProducts
			log.log(Level.SEVERE, "Error in getAllProducts", ex);
		}
		return result.toString();
	}
	
	public String searchProduct (String keyword) throws SQLException {
		System.out.println("Searching for product by SKU or name: " + keyword + "\n");
		StringBuilder result = new StringBuilder();
		String sql = "SELECT id, sku, name, price, stock_qty, reorder_level, active FROM products " +
					 "WHERE sku LIKE ? OR name LIKE ?";
		try (Connection connect = DBConnection.getInstance(); // Singleton: one shared DB connection
			PreparedStatement ps = connect.prepareStatement(sql)) {
			
			
			String pattern = "%" + keyword + "%";
			ps.setString(1, pattern);
			ps.setString(2, pattern);
			
			try (ResultSet rs = ps.executeQuery()) {
				boolean any = false;
				while (rs.next()) {
					any = true;
					long id = rs.getLong("id");
					String sku = rs.getString("sku");
					String name = rs.getString("name");
	                double price = rs.getDouble("price");
	                int qty = rs.getInt("stock_qty");
	                int rl = rs.getInt("reorder_level");
	                boolean active = rs.getBoolean("active");
	                
	                String row = String.format("ID: %d  SKU: %s  Name: %s  Price: $%.2f  Qty: %d  Reorder: %d  Active: %s",
	                        id, sku, name, price, qty, rl, active ? "Yes" : "No");
	                
	                result.append(row).append(System.lineSeparator());
	                System.out.println(row);
				}
				if (!any) {
					System.out.println("No matching products found.");
				}
			}
			log.info("Search requested for : " + keyword);
		}
		catch (SQLException ex) {
			// Log for SQL exception in searchProducts
			log.log(Level.SEVERE, "Error in searchProducts", ex);
		}
		return result.toString();
	}
					
	
	public void addProduct(String sku, String name, String description, double price, int reorderLevel) {
		if (sku == null || sku.trim().isEmpty() || name == null || name.trim().isEmpty()) {
			System.out.println("SKU and Name are required to add product.");
			log.warning("Add product failed: missing SKU or Name.");
			return;
		}
		
		try { 
			Connection connect = DBConnection.getInstance();
			
			// Insert with generated ID; stock starts at 0, active = 1 (true)
			String insertSql = "INSERT INTO products (sku, name, description, price, reorder_level, stock_qty, active)" +
						 "VALUES (?, ?, ?, ?, ?, 0, 1)";
		
			try (PreparedStatement ps = connect.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
				ps.setString(1, sku);
				ps.setString(2, name);
				ps.setString(3, description);
				ps.setDouble(4, price);
				ps.setInt(5, reorderLevel);
				
				int rows = ps.executeUpdate();
				if (rows == 0) {
					System.out.println("No product was added.");
					log.warning("Insert returned 0 rows for SKU: " + sku);
					return;
				}
				
				long newId = -1L;
				try (ResultSet keys = ps.getGeneratedKeys()) {
					if (keys.next()) {
						newId = keys.getLong(1);
					}
				}

				String selectSql = "SELECT id, sku, name, price, stock_qty, reorder_level, active FROM products WHERE id = ?";
				try (PreparedStatement sel = connect.prepareStatement(selectSql)) {
					sel.setLong(1,  newId);
					try (ResultSet rs = sel.executeQuery()) {
						if (rs.next()) {
							long id = rs.getLong("id");
							String rSku = rs.getString("sku");
							String rName = rs.getString("name");
			                double rPrice = rs.getDouble("price");
			                int qty = rs.getInt("stock_qty");
			                int rl = rs.getInt("reorder_level");
			                boolean active = rs.getBoolean("active");
			                
			                String row = String.format("ID: %d  SKU: %s  Name: %s  Price: $%.2f  Qty: %d  Reorder: %d  Active: %s",
		                        id, rSku, rName, rPrice, qty, rl, active ? "Yes" : "No");
			                
			                System.out.println("Product added:");
							System.out.println(row);
							log.info("Product added successfully: ID " + id + ", SKU " + rSku);
						}
						else {
							System.out.println("Product added, but could not load the inserted record.");
							log.warning("Inserted product but SELECT by generated id returned no rows. ID: " + newId);
						}
					}
				}
			}
		}
		catch (java.sql.SQLIntegrityConstraintViolationException dup) {
			System.out.println("A product with that SKU already exists.");
			log.warning("Duplicate SKU on insert: " + sku + " -> " + dup.getMessage());
		}
		catch (Exception ex) {
			System.out.println("There was an error adding the product. See logs for details.");
			// Log for exception in getAllProducts
			log.log(Level.SEVERE, "Error adding product: " +name + " (SKU " + sku + ")", ex);
		}
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
