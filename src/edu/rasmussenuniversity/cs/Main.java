package edu.rasmussenuniversity.cs;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	
	// Logger
	private static final Logger log = AppLogger.getLogger();
	
	public static void main(String[] args) {
		
		// Future extension: Optional Swing demo. Not used in this console-based application.
		// GUIExample g = new GUIExample();
		// g.OpenGui();
		
		// Display welcome message to user
		System.out.println("Welcome to Referral Only HVAC Inventory System ");
		// Log application start
		log.info("Application started.");
		
		Product productActions = new Product(); // DB actions
		Auth auth = new Auth(); // Auth helper
		User currentUser = null; // Current User
		char input = 'q'; // Default input
		
		// Single Scanner for reading user input
		try (Scanner scanner = new Scanner(System.in)) {
			
			// Login / Register gate before the loop
			while (currentUser == null) {
				System.out.println("(L)ogin, (R)egister, or (Q)uit:");
				String t = scanner.nextLine().trim().toLowerCase();
				if (t.isEmpty()) {
					System.out.println("Please enter L, R, or Q.");
					continue;
				}
				char c = t.charAt(0);
				if (c == 'l') {
					System.out.print("Username: ");
					String u = scanner.nextLine().trim();
					System.out.print("Password: ");
					String p = scanner.nextLine().trim();
					currentUser = auth.login(u, p);
					if (currentUser == null) {
						System.out.println("Login failed. Try again.");
					}
					
				}
				else if (c == 'r') {
					System.out.print("New username: ");
					String u = scanner.nextLine().trim();
					System.out.println("Password must be 12+ characters with one upper, lower, digit, and special charater.");
					System.out.print("New password: ");
					String p = scanner.nextLine().trim();
					Long id = auth.register(u, p, "STAFF"); // or "ADMIN" for your first user
					if (id != null) {
						System.out.println("Registered successfully. Please login.");
					}
				}
				else if (c == 'q') {
					System.out.println("Goodbye.");
					log.info("Application exited at auth gate.");
					return;
				}
				else {
					System.out.println("Please enter L, R, or Q.");
				}
			}
			System.out.println("Logged in as " + currentUser.username() + " (" + currentUser.role() + ")");
			log.info("User logged in: " + currentUser.username() + " role=" + currentUser.role());
			
			// Menu Loop
			do {
				// Display Menu
				System.out.println("\nPlease make your selection:");
				System.out.println("g = Get all products");
				System.out.println("s = Search product by name");
				// If viewer is logged in, hide a, u, d, j) 
				if (!"VIEWER".equals(currentUser.role())) { 
					System.out.println("a = Add a new product");
					System.out.println("u = Update product price and reorder level");
					System.out.println("d = Deactivate product");
					System.out.println("j = Adjust stock (+/-)");
					System.out.println("v = Add invoice");
				}
				System.out.println("i = List invoices (Last 10)");
				System.out.println("r = Low stock report");
				System.out.println("t = Audit log report (Last 20)");
				System.out.println("q = Quit");
				System.out.println("Selection: ");
				
				//Get a selection from the user
				String token = scanner.nextLine().trim();
				if (token.isEmpty()) {
					System.out.println("Please enter a selection from the menu.");
					log.warning("Empty menu selection entered.");
					continue;
				}
				input = Character.toLowerCase(token.charAt(0)); // normalize to lower-case
				
				// Log menu choices
				log.info("Menu selection: " + input);
				
				try {
					if (input == 'g') {
					// DB: SELECT * FROM products
					productActions.getAllProducts();
					}
					
					else if (input == 's') {
						System.out.print("Enter product name to search: ");
						String name = scanner.nextLine().trim();
						// DB: WHERE name LIKE
						productActions.searchProduct(name);
					} 
					
					else if (input == 'a') {
						if ("VIEWER".equals(currentUser.role())) {
							System.out.println("Not permitted.");
							continue;
						}
						// Gather product information fields
						System.out.print("SKU: ");
						String sku = scanner.nextLine().trim();
					
						System.out.print("Name: ");
						String name = scanner.nextLine().trim();
					
						System.out.print("Description: ");
						String desc = scanner.nextLine().trim();
					
						System.out.print("Price: ");
						double price = Double.parseDouble(scanner.nextLine().trim());
						
						System.out.print("Reorder level: ");
						int reorder = Integer.parseInt(scanner.nextLine().trim());

						// DB: INSERT into products
						productActions.addProduct(sku, name, desc, price, reorder, currentUser.id());
					} 
					
					else if (input == 'u') {
						if ("VIEWER".equals(currentUser.role())) {
							System.out.println("Not permitted.");
							continue;
						}
						// Gather product information (DB connection later)
						System.out.print("Product ID: ");
						long id = Long.parseLong(scanner.nextLine().trim());
					
						System.out.print("New price: ");
						double price = Double.parseDouble(scanner.nextLine().trim());
						
						System.out.print("New reorder level: ");
						int reorder = Integer.parseInt(scanner.nextLine().trim());
					
						// DB: UPDATE product SET
						productActions.updateProduct(id, price, reorder, currentUser.id());
					}
					
					else if (input == 'd') {
						if ("VIEWER".equals(currentUser.role())) {
							System.out.println("Not permitted.");
							continue;
						}
						System.out.print("Product ID to deactivate: ");
						long id = Long.parseLong(scanner.nextLine().trim());
						
						System.out.print("Reason: ");
						String reason = scanner.nextLine().trim();
						
						// DB: UPDATE products SET active=false
						productActions.deactivateProduct(id, reason, currentUser.id());
					}
					
					else if (input == 'j') {
						if ("VIEWER".equals(currentUser.role())) {
							System.out.println("Not permitted.");
							continue;
						}
						System.out.print("Product ID: ");
						long productId = Long.parseLong(scanner.nextLine().trim());
						
						System.out.print("Quantity change (+ for add, - for remove): ");
						int deltaQty = Integer.parseInt(scanner.nextLine().trim());
						
						System.out.print("Reason: ");
						String reason = scanner.nextLine().trim();
						
						productActions.adjustStock(productId, deltaQty, reason, currentUser.id());
					}
					
					// Basic invoice for customer with total amount. 
					// Next week I will attempt to link invoices to products and adjust stock automatically.
					else if (input == 'v') {
						if ("VIEWER".equals(currentUser.role())) {
							System.out.println("Not permitted.");
							continue;
						}
						System.out.print("Customer name: ");
						String cname = scanner.nextLine().trim();
						
						System.out.print("Total amount: ");
						double total = Double.parseDouble(scanner.nextLine().trim());
						
						new Invoice().addInvoice(cname, total, currentUser.id());
					}
					
					// List invoice
					else if (input == 'i') {
						new Invoice().listInvoices();
					}
					
					else if (input == 'r') {
						Reports.lowStockReport();
							
					// DB: SELECT FROM products WHERE stock_qty <= reorder_level
					}
					
					else if (input == 't') {
						Reports.auditLogReport(20);
					}
					
					else if (input == 'q') {
						System.out.println("Thank you for using the Inventory System!");
						// Log for application exit
						log.info("Application exited normally");
					}
					
					else { 
						System.out.println("Invalid selection. Please try again.");
						log.warning("Invalid menu option: " + input);
					}
				}
				catch (NumberFormatException nfe) {
					System.out.println("Invalid numeric input. Please try again.");
					log.warning("Invalid numeric input: " + nfe.getMessage());
				}
				
			}
			while(input != 'q');
		}
		catch (Exception ex) {
			// Log for unexpected errors
			log.log(Level.SEVERE, "Unexpected exception in main loop", ex);
		}
	}
}
