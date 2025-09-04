package edu.rasmussenuniversity.cs;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Reports {
	private static final Logger log = AppLogger.getLogger();
	
	public static void lowStockReport() {
		StringBuilder result = new StringBuilder();
		System.out.println("\n=== Low Stock Report ===");
		try {
			Connection connect = DBConnection.getInstance(); // Singleton: one shared DB connection
			Statement statement = connect.createStatement();
			ResultSet rs = statement.executeQuery(
					"SELECT id, sku, name, stock_qty, reorder_level FROM products " +
					"WHERE stock_qty <= reorder_level " +
					"ORDER BY stock_qty ASC, name ASC");
			
			boolean any = false;
			while (rs.next()) {
				any = true;
				long id = rs.getLong("id");
				String sku = rs.getString("sku");
				String name = rs.getString("name");
				int qty = rs.getInt("stock_qty");
				int rl = rs.getInt("reorder_level");
				
				String row = String.format("ID: %d  SKU: %s  Name: %s  Qty: %d  Reorder: %d %n",
		                        id, sku, name, qty, rl);
				
				result.append(row).append(System.lineSeparator());
				System.out.println(row);
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
}
