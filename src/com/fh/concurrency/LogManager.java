package com.fh.concurrency;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LogManager {

    private String logDirectory;

    public LogManager(String logDirectory) {
        this.logDirectory = logDirectory;
    }

    // Create a new log file
    public void createLogFile(String fileName) {
        try {
            // Build the full path
            String homeDirectory = System.getProperty("user.home");
            Path logFilePath = Paths.get(homeDirectory, "Documents", logDirectory, fileName);

            // Ensure directories exist
            Files.createDirectories(logFilePath.getParent());

            // Create the file if it doesn't exist
            if (Files.notExists(logFilePath)) {
                Files.createFile(logFilePath);
                System.out.println("Log file created: " + logFilePath);
            } else {
                System.out.println("Log file already exists: " + logFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Delete a log file
    public void deleteLogFile(String fileName) {
        try {
            String homeDirectory = System.getProperty("user.home");
            Path logFilePath = Paths.get(homeDirectory, "Documents", logDirectory, fileName);

            if (Files.exists(logFilePath)) {
                Files.delete(logFilePath);
                System.out.println("Log file deleted: " + logFilePath);
            } else {
                System.out.println("Log file does not exist: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Move a log file
    public void moveLogFile(String fileName, String targetDirectory) {
        try {
            String homeDirectory = System.getProperty("user.home");
            Path logFilePath = Paths.get(homeDirectory, "Documents", logDirectory, fileName);
            Path targetPath = Paths.get(homeDirectory, "Documents", targetDirectory, fileName);

            if (Files.exists(logFilePath)) {
                Files.createDirectories(targetPath.getParent());
                Files.move(logFilePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Log file moved to: " + targetPath);
            } else {
                System.out.println("Log file does not exist: " + fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Archive log files older than a specified number of days
    public void archiveOldLogs(int daysOld) {
        try {
            String homeDirectory = System.getProperty("user.home");
            Path logDirectoryPath = Paths.get(homeDirectory, "Documents", logDirectory);
            Path archiveDirectoryPath = Paths.get(homeDirectory, "Documents", logDirectory, "archive");

            // Ensure the archive directory exists
            Files.createDirectories(archiveDirectoryPath);

            // Iterate over the files in the log directory
            Files.list(logDirectoryPath).forEach(file -> {
                try {
                    // Get file attributes to determine the creation time
                    BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
                    Instant fileTime = attributes.creationTime().toInstant();

                    // Calculate the file's age
                    //long fileAgeInDays = TimeUnit.DAYS.convert(Date.from(Instant.now()).getTime() - Date.from(fileTime).getTime(), TimeUnit.MILLISECONDS);
                    long fileAgeInMinutes = TimeUnit.MINUTES.convert(Date.from(Instant.now()).getTime() - Date.from(fileTime).getTime(), TimeUnit.MILLISECONDS);

                    if (fileAgeInMinutes > 5 && !Files.isDirectory(file)) { //if (fileAgeInDays > daysOld && !Files.isDirectory(file))
                        // Move the file to the archive directory
                        Path archivedFilePath = archiveDirectoryPath.resolve(file.getFileName());
                        Files.move(file, archivedFilePath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Archived file: " + file.getFileName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
