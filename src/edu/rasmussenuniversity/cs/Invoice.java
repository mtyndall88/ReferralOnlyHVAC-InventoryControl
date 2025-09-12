package edu.rasmussenuniversity.cs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Basic invoice for customer with total amount. 
// Next week I will attempt to link invoices to products and adjust stock automatically.
public class Invoice {
	public void addInvoice(String customerName, double total, long userId) {
		String sql = "INSERT INTO invoices(customer_name, total, created_by) VALUES (?,?,?)";
		try (Connection connect = DBConnection.getInstance();
			PreparedStatement ps = connect.prepareStatement(sql)) {
			ps.setString(1, customerName);
			ps.setDouble(2, total);
			ps.setLong(3, userId);
			ps.executeUpdate();
			System.out.println("Invoice added for " + customerName + " $" + total);
		}
		catch (Exception ex) {
			System.out.println("Error adding invoice: " + ex.getMessage());
		}
	}
	
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
