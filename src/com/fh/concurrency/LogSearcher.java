package com.fh.concurrency;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class LogSearcher extends Thread {
    private final String logDirectory;
    private final Metadata metadata;
    private final String searchCriteria;
    private List<String> searchResults = new ArrayList<>();

    public LogSearcher(String logDirectory, Metadata metadata, String searchCriteria) {
        this.logDirectory = logDirectory;
        this.metadata = metadata;
        this.searchCriteria = searchCriteria;
    }

    @Override
    public void run() {
        try {
            if (searchCriteria.matches("\\d{4}-\\d{2}-\\d{2}")) {
                searchResults = searchByDate(searchCriteria);
            } else {
                searchResults = searchByEquipment(searchCriteria);
            }

            // Print search results
            if (searchResults.isEmpty()) {
                System.out.println("No results found for: " + searchCriteria);
            } else {
                System.out.println("Search results for " + searchCriteria + ":");
                searchResults.forEach(System.out::println);
            }
        } catch (EMSException e) {
            System.err.println("An error occurred during search: " + e.getMessage());
        }
    }

    // Method to search by date
    public List<String> searchByDate(String date) throws EMSException {
        List<String> matchedFiles = new ArrayList<>();
        String regex = ".*" + date + ".*\\.log$";
        Pattern pattern;

        try {
            // Compile the regex pattern
            pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            // Throw a specific exception for invalid regex
            throw new EMSInvalidRegexException("Invalid date format for search pattern", e);
        }

        String homeDirectory = System.getProperty("user.home");
        Path logDirPath = Paths.get(homeDirectory, "Documents", logDirectory);

        // Using try-with-resources to ensure proper resource management
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, "*.log")) {
            for (Path entry : stream) {
                Matcher matcher = pattern.matcher(entry.getFileName().toString());
                if (matcher.matches()) {
                    matchedFiles.add(entry.toString());
                }
            }
            // Log the search operation in metadata
            metadata.logSearch(date, "searched by date");

            if (matchedFiles.isEmpty()) {
                System.out.println("No log files found for the date: " + date);
            } else {
                System.out.println("Files found:");
                matchedFiles.forEach(System.out::println);
            }

        } catch (NoSuchFileException e) {
            // Throw specific file not found exception
            throw new EMSFileNotFoundException("Log directory not found", e);
        } catch (IOException e) {
            // Handle general I/O exceptions with a custom exception
            throw new EMSFileReadException("Error reading log directory or files", e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new EMSException("Unexpected error during search", e);
        }

        return matchedFiles;
    }

    // Method to search by equipment name
    public List<String> searchByEquipment(String equipmentName) throws EMSException {
        List<String> matchedFiles = new ArrayList<>();
        String regex = ".*" + equipmentName + ".*\\.log$";
        Pattern pattern;

        try {
            // Compile the regex pattern
            pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            // Throw a specific exception for invalid regex
            throw new EMSInvalidRegexException("Invalid equipment name format for search pattern", e);
        }

        String homeDirectory = System.getProperty("user.home");
        Path logDirPath = Paths.get(homeDirectory, "Documents", logDirectory);

        // Using try-with-resources to ensure proper resource management
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDirPath, "*.log")) {
            for (Path entry : stream) {
                Matcher matcher = pattern.matcher(entry.getFileName().toString());
                if (matcher.matches()) {
                    matchedFiles.add(entry.toString());
                }
            }
            // Log the search operation in metadata
            metadata.logSearch(equipmentName, "searched by equipment");

            if (matchedFiles.isEmpty()) {
                System.out.println("No log files found for equipment: " + equipmentName);
            } else {
                System.out.println("Files found:");
                matchedFiles.forEach(System.out::println);
            }

        } catch (NoSuchFileException e) {
            // Throw specific file not found exception
            throw new EMSFileNotFoundException("Log directory not found", e);
        } catch (IOException e) {
            // Handle general I/O exceptions with a custom exception
            throw new EMSFileReadException("Error reading log directory or files", e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new EMSException("Unexpected error during search", e);
        }

        return matchedFiles;
    }

    // Method to open and display the contents of a log file
    public void openLogFile(String fileName) throws EMSException {
        String homeDirectory = System.getProperty("user.home");
        Path logFilePath = Paths.get(homeDirectory, "Documents", logDirectory, fileName);

        // Check if the file exists
        if (Files.exists(logFilePath)) {
            // Using try-with-resources to ensure resource management
            try (BufferedReader reader = Files.newBufferedReader(logFilePath)) {
                String line;
                System.out.println("\nContents of " + fileName + ":");
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
                // Log the operation in metadata
                metadata.logOperation(fileName, "Log file opened");
            } catch (IOException e) {
                // Handle general I/O exceptions with a custom exception
                throw new EMSFileReadException("Error reading log file: " + fileName, e);
            }
        } else {
            // Throw specific file not found exception
            throw new EMSFileNotFoundException("Log file does not exist: " + fileName);
        }
    }
}
