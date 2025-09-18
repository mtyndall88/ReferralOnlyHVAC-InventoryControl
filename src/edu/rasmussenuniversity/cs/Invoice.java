package edu.rasmussenuniversity.cs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

// Single product invoice that adjusts stock automatically and audits the invoice creation and stock adjustment.
public class Invoice {
	
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
	
	public void addInvoice(String customerName, long productId, int qty, long userId) {
		String invSql = "INSERT INTO invoices(customer_name, total, created_by) VALUES (?,?,?)";
		String itemSql= "INSERT INTO invoice_items(invoice_id, product_id, qty, price) VALUES (?,?,?,?)";
		String productSql= "UPDATE products SET stock_qty = stock_qty - ? WHERE id=? AND active=1";
		
		try (Connection connect = DBConnection.getInstance()) {
			boolean oldAuto = connect.getAutoCommit();
			try{
				connect.setAutoCommit(false);
				
				// Look up product price
				double price = 0;
				try (PreparedStatement ps = connect.prepareStatement("SELECT price FROM products WHERE id=? AND active=1")) {
					ps.setLong(1, productId);
					try (ResultSet rs = ps.executeQuery()) {
						if (rs.next()) {
							price = rs.getDouble("price");
						}
						else {
							System.out.println("Product not found or inactive.");
							connect.rollback();
							return;
						}
					}
				}
				
				double total = price * qty;
				long invId;
				
				// Insert invoice
				try (PreparedStatement ps = connect.prepareStatement(invSql, Statement.RETURN_GENERATED_KEYS)) {
					ps.setString(1, customerName);
					ps.setDouble(2, total);
					ps.setLong(3, userId);
					ps.executeUpdate();
					try (ResultSet rs = ps.getGeneratedKeys()) {
						if (!rs.next()) {
							throw new Exception("No generated key returned for invoice.");
						}
						invId = rs.getLong(1);
					}
				}
				// Audit for invoice creation
				audit("INVOICE", invId, "CREATE", "customer=" + customerName + ", total=" + total, userId, connect);
				
				// Insert the line item
				try (PreparedStatement ps = connect.prepareStatement(itemSql)) {
					ps.setLong(1, invId);
					ps.setLong(2, productId);
					ps.setInt(3, qty);
					ps.setDouble(4, price);
					ps.executeUpdate();
				}
				
				// Adjust stock
				try (PreparedStatement ps = connect.prepareStatement(productSql)) {	
					ps.setInt(1, qty);
					ps.setLong(2, productId);
					ps.executeUpdate();
				}
				// Audit for stock adjustment
				audit("PRODUCT", productId, "STOCK_ADJUST", "deltaQty=-" + qty + ", reason=INVOICE:" + invId, userId, connect);
				
				connect.commit();
				System.out.println("Invoice created for " + customerName + ", product " + productId + " qty=" + qty + " total=$" + total);
			}
			catch (Exception ex) {
				connect.rollback();
				System.out.println("Error adding invoice (rolled back). See logs for details.");
				log.log(Level.SEVERE, "Error adding invoice", ex);
			}
			finally {
				connect.setAutoCommit(oldAuto);
			}
		}
		catch (Exception ex) {
			System.out.println("Error adding invoice: " + ex.getMessage());
		}
	}
	
	// List recent invoices
	public void listInvoices() {
		String sql = "SELECT id, customer_name, total, created_at FROM invoices ORDER BY created_at DESC LIMIT 10";
		try (Connection connect = DBConnection.getInstance();
			PreparedStatement ps = connect.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {
			
			while (rs.next()) {
				System.out.printf("Invoice #%d %s $%.2f %s%n",
						rs.getLong("id"),
						rs.getString("customer_name"),
						rs.getDouble("total"),
						rs.getString("created_at"));
			}
		}
		catch (Exception ex) {
			System.out.println("Error listing invoices: " + ex.getMessage());
		}
	}
}
