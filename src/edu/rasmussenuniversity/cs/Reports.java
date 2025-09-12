package edu.rasmussenuniversity.cs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reports {
	private static final Logger log = AppLogger.getLogger();
	
	public static void lowStockReport() {
		System.out.println("=== Low Stock Report ===" + "\n");
		
		String sql = "SELECT id, sku, name, stock_qty, reorder_level FROM low_stock_v ORDER BY stock_qty ASC, name ASC";
		
		boolean any = false;
		
		try {
			Connection connect = DBConnection.getInstance(); // Singleton: one shared DB connection
			
			try (PreparedStatement ps = connect.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
				
				while (rs.next()) {
					any = true;
					long id = rs.getLong("id");
					String sku = rs.getString("sku");
					String name = rs.getString("name");
					int qty = rs.getInt("stock_qty");
					int rl = rs.getInt("reorder_level");
					
					String row = String.format("ID: %d  SKU: %s  Name: %s  Qty: %d  Reorder: %d",
			                        id, sku, name, qty, rl);				
					System.out.println(row);
				}
			}
			if (!any) {
				System.out.println("No products are currently below reorder level.");
			}
			log.info("Low stock report generated successfully");
		}
		catch (Exception ex) {
			log.log(Level.SEVERE, "Error generating low stock report", ex);
		}
	}
			
	public static void auditLogReport(int maxRows) {
		if (maxRows <= 0) maxRows = 20; 
		System.out.println("=== Audit Log Report (most recent " + maxRows + ") ===\n");
		
		String sql = "SELECT al.id, al.created_at, u.username, al.entity, al.entity_id, al.action, al.details"
				+ " FROM audit_log al LEFT JOIN users u ON u.id = al.user_id ORDER BY al.created_at DESC LIMIT ?";
		try (Connection connect = DBConnection.getInstance(); // Singleton: one shared DB connection
			PreparedStatement ps = connect.prepareStatement(sql)) {
			
			ps.setInt(1, maxRows);
			
			try (ResultSet rs = ps.executeQuery()) {
				boolean any = false;
				while (rs.next()) {
					any = true;
					long id = rs.getLong("id");
					String when = rs.getString("created_at");
					String who = rs.getString("username");
					String entity = rs.getString("entity");
					long entityId = rs.getLong("entity_id");
					String action = rs.getString("action");
					String details = rs.getString("details");
					
					System.out.printf("#%d  %s  user=%s  %s(%d)  action=%s  details=%s%n",
							id, when, (who == null ? "unknown" : who), entity, entityId, action, details);
				}
				if (!any) {
					System.out.println("No audit entries found.");
				}
				log.info("Audit log report generated successfully");
			}
		}
		catch (Exception ex) {
			log.log(Level.SEVERE, "Error generating audit log report", ex);
		}
	}
}