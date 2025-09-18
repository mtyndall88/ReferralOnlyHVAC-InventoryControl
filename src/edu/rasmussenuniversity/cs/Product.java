package edu.rasmussenuniversity.cs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

//DAO for product CRUD + stock adjustments (SoW #3–#5); transactions & audit trail.
public class Product {
	// Logger
	private static final Logger log = AppLogger.getLogger();
	
	// Audit insert
	private void audit(String entity, long entityId, String action, String details, long userId, Connection connect) throws SQLException {
		try (PreparedStatement ps = connect.prepareStatement(
				"INSERT INTO audit_log(user_id, entity, entity_id, action, details) VALUES (?,?,?,?,?)")) {
			ps.setLong(1, userId);
			ps.setString(2, entity);
			ps.setLong(3, entityId);
			ps.setString(4, action);
			ps.setString(5, details);
			ps.executeUpdate();
		}
	}
	// Get all active products
	public String getAllProducts() throws SQLException {
		System.out.println("All Products" + "\n");
		StringBuilder result = new StringBuilder();
		
		try {
			Connection connect = DBConnection.getInstance(); // Singleton: one shared DB connection
			String sql = "SELECT id, sku, name, price, stock_qty, reorder_level, active " +
			"FROM products WHERE active=1 LIMIT 20 OFFSET 0"; // Pagination set at first 20 rows.
			
			try (PreparedStatement ps = connect.prepareStatement(sql);
					ResultSet rs = ps.executeQuery()) {
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
	
	// Search for product by name or SKU
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
					
	
	public void addProduct(String sku, String name, String description, double price, int reorderLevel, long userId) {
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
					if (keys.next()) newId = keys.getLong(1);
				}
				
				audit("PRODUCT", newId, "ADD_PRODUCT", "sku="+sku+", name="+name+", price="+price+", reorder="+reorderLevel, userId, connect);
				System.out.println("Product added. ID = " + newId);
				log.info("Product added successfully: SKU " + sku);
			}
		}
		catch (java.sql.SQLIntegrityConstraintViolationException dup) {
			System.out.println("A product with that SKU already exists.");
		}
		catch (Exception ex) {
			System.out.println("There was an error adding the product. See logs for details.");
			// Log for exception in getAllProducts
			log.log(Level.SEVERE, "Error adding product", ex);
		}
	}
	
	// Update product
	public void updateProduct(long id, double price, int reorderLevel, long userId) {
		try (Connection connect = DBConnection.getInstance();
				PreparedStatement ps = connect.prepareStatement(
						"UPDATE products SET price=?, reorder_level=?, updated_at=CURRENT_TIMESTAMP WHERE id=? AND active=1")) {
			ps.setDouble(1, price);
			ps.setInt(2, reorderLevel);
			ps.setLong(3, id);
			
			int rows=ps.executeUpdate();
			if (rows == 0) {
				System.out.println("No active product updated.");
				return;
			}
			audit("PRODUCT", id, "UPDATE_PRODUCT", "price="+price+", reorder="+reorderLevel, userId, connect);
			System.out.println("Product updated.");
		}
		catch (Exception ex) {
			System.out.println("There was an error updating product. See logs for details.");
			log.log(Level.SEVERE, "Error updating product", ex);
		}
		// Log for updating a product
		log.info("Update requested for ID: " + id + ", price: " + price + ", reorder: " + reorderLevel);
	}
	
	// Deactivate product
	public void deactivateProduct(long id, String reason, long userId) {
		try (Connection connect = DBConnection.getInstance();
				PreparedStatement ps = connect.prepareStatement(
						"UPDATE products SET active=0, deactivated_at=CURRENT_TIMESTAMP WHERE id=? AND active=1")) {
			ps.setLong(1, id);
			
			int rows=ps.executeUpdate();
			if (rows == 0) {
				System.out.println("No active product to deactivate.");
				return;
			}
			audit("PRODUCT", id, "DEACTIVATE_PRODUCT", "reason="+reason, userId, connect);
			System.out.println("Product deactivated.");
		}
		catch (Exception ex) {
			System.out.println("There was an error deactivating product. See logs for details.");
			log.log(Level.SEVERE, "Error deactivating product", ex);
		}
		// Log for deactivating a product
		log.info("Deactivate requested for ID: " + id + ", reason: " + reason);
	}
	
	// Adjust stock
	public void adjustStock(long productId, int deltaQty, String reason, long userId) {
		if (reason == null || reason.trim().isEmpty()) {
			System.out.println("Reason is required.");
			log.warning("Stock adjustment failed: missing reason.");
			return;
	}
		try {
			Connection connect = DBConnection.getInstance();
			// Atomic 
			boolean oldAuto = connect.getAutoCommit();
			
			try {
				connect.setAutoCommit(false); // turn off auto-commit so we control commit/rollback
				
				// Update product quantity (only if active)
				 String updSql = "UPDATE products SET stock_qty = stock_qty + ?, updated_at = CURRENT_TIMESTAMP WHERE id = ? AND active = 1";
				 
				 try (PreparedStatement ps = connect.prepareStatement(updSql)) {
					 ps.setInt(1, deltaQty);
					 ps.setLong(2, productId);
					 int rows = ps.executeUpdate();
					 if (rows == 0) {
						 System.out.println("Product not found or inactive.");
						 log.warning("Stock adjust: no active product updated. id=" + productId);
						 connect.rollback();
						 return;
					 }
				 }
				 // Record the stock movement (required reason + responsible user)
				 String insSql = "INSERT INTO stock_movements(product_id, delta_qty, reason, user_id, created_by) VALUES (?,?,?,?,?)";
				 try (PreparedStatement ps = connect.prepareStatement(insSql)) {
					 ps.setLong(1, productId);
					 ps.setInt(2, deltaQty);
					 ps.setString(3, reason);
					 ps.setLong(4, userId);
					 ps.setLong(5, userId);
					 ps.executeUpdate();
				 }
				 
				 // Audit trail
				 audit("PRODUCT", productId, "STOCK_ADJUST", "deltaQty=" + deltaQty + ", reason=" + reason, userId, connect);
				 connect.commit(); // apply all changes if everything succeeded
				 System.out.println("Stock adjusted successfully.");
				 log.info("Stock adjusted: productId=" + productId + ", deltaQty=" + deltaQty + ", userId=" + userId);
			}	 
			catch (Exception ex) {
				connect.rollback(); // undo changes if something failed
				System.out.println("Error adjusting stock (rolled back). See logs for details.");
				log.log(Level.SEVERE, "Error in adjustStock transaction for id=" + productId, ex);
			}
			finally {
				connect.setAutoCommit(oldAuto);
			}
		}
		catch (Exception outer) {
			System.out.println("Unexpected error adjusting stock. See logs for details.");
			log.log(Level.SEVERE, "Unexpected error in adjustStock for id=" + productId, outer);
		}
	}
}
