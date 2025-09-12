package edu.rasmussenuniversity.cs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static Connection connection = null;
	
	private DBConnection() { // private constructor to prevent instantiation
	}
	
	// Singleton: always return the same database connection instance
	public static Connection getInstance() throws SQLException {
		if (connection == null || connection.isClosed()) {
			
			// Hardcoded credentials are for production only
			try {
				String url = "jdbc:mysql://localhost/referral_inventory";
                String user = "root";
                String password = "root";
                
                connection = DriverManager.getConnection(url, user, password);
			} 
			catch (SQLException e) {
				throw new SQLException("Failed to connect to database", e);
			}
		}
		return connection;
	}
}

