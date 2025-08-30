package edu.rasmussenuniversity.cs;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// Console and File logging
public final class AppLogger {
	
	private static final Logger logger = Logger.getLogger("ReferralOnlyHVAC");
	
	static {
		try {
			// LogManager: reset the default configuration before adding custom handlers
			LogManager.getLogManager().reset();
			
			
			// Handler: sends log messages to the console
			ConsoleHandler ch = new ConsoleHandler();
			ch.setLevel(Level.INFO);
			
			// Formatter: defines how log messages look in console/file
			ch.setFormatter(new SimpleFormatter());
			logger.addHandler(ch);
			
			// Handler: sends log messages to a file (logs.txt)
			FileHandler fh = new FileHandler("logs.txt", true);
			fh.setLevel(Level.ALL);
			
			// Formatter: same SimpleFormatter applied to file output
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
		}
		
		catch (IOException e) {
			e.printStackTrace(); // fallback if logging setup fails	
		}
	}
	
	public static Logger getLogger() {
		return logger;
	}
}
