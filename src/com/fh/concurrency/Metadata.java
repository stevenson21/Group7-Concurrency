package com.fh.concurrency;



import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Metadata {
    private String metadataFile;

    public Metadata() {
        this.metadataFile = "metadata.log";  // Metadata log file
    }

    // Method to log file operations (create, move, delete, archive)
    public void logOperation(String fileName, String operation) {
        String logEntry = "File: " + fileName + " | Operation: " + operation + " | Timestamp: " + new Date();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(metadataFile), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(logEntry + "\n");
            System.out.println("Metadata logged: " + logEntry);
        } catch (IOException e) {
            System.err.println("Error logging metadata: " + e.getMessage());
        }
    }

    // Method to log search operations (search by date or equipment)
    public void logSearch(String searchTerm, String searchType) {
        String logEntry = "Search Term: " + searchTerm + " | Search Type: " + searchType + " | Timestamp: " + new Date();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(metadataFile), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(logEntry + "\n");
            System.out.println("Search metadata logged: " + logEntry);
        } catch (IOException e) {
            System.err.println("Error logging search metadata: " + e.getMessage());
        }
    }
}
