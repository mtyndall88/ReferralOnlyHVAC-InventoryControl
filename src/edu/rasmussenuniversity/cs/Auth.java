package edu.rasmussenuniversity.cs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Based on GeeksforGeeks Regex Pattern 
//Password policy: At least 12 characters, one lower case, one upper case, one digit, and one special character.

public class Auth {
	private static final Logger log = AppLogger.getLogger();
	
	// Regex to check password requirements
	private static final String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\\-+=])(?=\\S+$).{12,}$";
	
	// Compile the Regex
	private static final Pattern p = Pattern.compile(regex);
	
	// Function to validate the password
	private static boolean isValidPassword(String password) {
		if (password == null) return false;
		
		// See if password matches the Regex
		Matcher m = p.matcher(password);
		return m.matches();
	}
	
	public Long register(String username, String password, String role) {
		if (username == null || username.isBlank()) {
			System.out.println("Username is required.");
			return null;
		}
		boolean isValid =isValidPassword(password);
		if (!isValid) {
			System.out.println("Password must be at least 12 characters with one uppercase, one lowercase, one digit, one special character and no spaces");
			return null;
		}
		if (role == null || role.isBlank()) role = "STAFF";
		
		try (Connection connect = DBConnection.getInstance();
				PreparedStatement ps = connect.prepareStatement("INSERT INTO users (username, password, role, active) VALUES (?,?,?,1)",
						java.sql.Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, username.trim());
			ps.setString(2, password); 
			ps.setString(3, role);
			
			int rows = ps.executeUpdate();
			if (rows == 0) {
				System.out.println("Registration failed.");
				return null;
			}
			try (ResultSet rs = ps.getGeneratedKeys()) {
				 if (rs.next()) {
					 long id = rs.getLong(1);
					 log.info("User registered: " + username + " role=" + role);
					 return id;
				 }
			}
		}
		catch (java.sql.SQLIntegrityConstraintViolationException dup) {
			System.out.println("Username already exists.");
		}
		catch (Exception ex) {
			System.out.println("Registration error.");
			log.log(Level.SEVERE, "Register error for username=" + username, ex);
		}
		return null;
	}
	
	public User login(String username, String password) {
		if (username == null || password == null) {
			System.out.println("Username and password are required.");
			return null;
		}
		try (Connection connect = DBConnection.getInstance();
				PreparedStatement ps = connect.prepareStatement("SELECT id, username, role\r\n FROM users\r\n WHERE username = ? AND password = ? AND active = 1")) {
			ps.setString(1, username.trim());
			ps.setString(2, password);
			
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					long id = rs.getLong("id");
					String u = rs.getString("username");
					String role = rs.getString("role");
					log.info("Login success: " + u + " role=" + role);
					return new User(id, u, role);
				}
				else {
					System.out.println("Invalid username or password.");
					log.info("Login failed for username=" + username);
					return null;
				}
			}
		}
		catch (Exception ex) {
			System.out.println("Login error. See logs for details.");
			log.log(Level.SEVERE, "Login error for username=" + username, ex);
			return null;
		}
	}
}