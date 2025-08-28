package edu.rasmussenuniversity.cs;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
	
	// Logger
	private static final Logger log = AppLogger.getLogger();
	
	public static void main(String[] args) {
		
		// Display welcome message to user
		System.out.println("Welcome to Referral Only HVAC Inventory System ");
		// Log application start
		log.info("Application started.");
		
		Product productActions = new Product(); // DB actions
		char input = 'q'; // Default input
		
		// Single Scanner for reading user input
		try (Scanner scanner = new Scanner(System.in)) {
			
			// Menu Loop
			do {
				// Display Menu
				System.out.println("\nPlease make your selection:");
				System.out.println("g = Get all products");
				System.out.println("s = Search product by name");
				System.out.println("a = Add a new product");
				System.out.println("u = Update product price and reorder level");
				System.out.println("d = Deactivate product");
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
						productActions.searchProduct(name); //placeholder
					} 
					
					else if (input == 'a') {
						// Gather product information fields (DB connection later)
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
						productActions.addProduct(sku, name, desc, price, reorder);
					} 
					
					else if (input == 'u') {
						// Gather product information (DB connection later)
						System.out.print("Product ID: ");
						long id = Long.parseLong(scanner.nextLine().trim());
					
						System.out.print("New price: ");
						double price = Double.parseDouble(scanner.nextLine().trim());
						
						System.out.print("New reorder level: ");
						int reorder = Integer.parseInt(scanner.nextLine().trim());
					
						// DB: UPDATE product SET
						productActions.updateProduct(id, price, reorder);
					}
					
					else if (input == 'd') {
					System.out.print("Product ID to deactivate: ");
					long id = Long.parseLong(scanner.nextLine().trim());

					System.out.print("Reason: ");
					String reason = scanner.nextLine().trim();
					
					// DB: UPDATE products SET active=false
					productActions.deactivateProduct(id, reason);
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
