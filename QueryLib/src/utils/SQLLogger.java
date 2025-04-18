package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class SQLLogger {
	private static final String LOG_FILE = "sql_queries_file1.log";

    public static void log(String query) {
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(LocalDateTime.now() + " - " + query);
        } catch (IOException e) {
            System.err.println("Log error: " + e.getMessage());
        }
    }
	
}
