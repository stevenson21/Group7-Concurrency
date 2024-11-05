package com.fh.concurrency;

import java.nio.file.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EnergyDataExchange {

    private String logDirectory;
    private Metadata metadata;

    public EnergyDataExchange(String logDirectory, Metadata metadata) {
        this.logDirectory = logDirectory;
        this.metadata = metadata;
    }

    // Log energy data for a specific equipment
    public void logEnergyData(String equipmentName, String energyData) {
        try {
            // Build the full path
            String homeDirectory = System.getProperty("user.home");
            Path logFilePath = Paths.get(homeDirectory, "Documents", logDirectory, equipmentName + ".log");

            // Ensure directories exist
            Files.createDirectories(logFilePath.getParent());

            // Write the energy data to the file
            Files.write(logFilePath, (energyData + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Energy data logged for " + equipmentName);

            // Optionally, update metadata about this operation
            metadata.logOperation(equipmentName, "Log energy data");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieve energy data
    public List<String> retrieveEnergyData(String equipmentName) {
        try {
            // Build the full path
            String homeDirectory = System.getProperty("user.home");
            Path logFilePath = Paths.get(homeDirectory, "Documents", logDirectory, equipmentName + ".log");

            // Read the file and return the contents
            if (Files.exists(logFilePath)) {
                return Files.readAllLines(logFilePath);
            } else {
                System.out.println("Log file does not exist for: " + equipmentName);
                return List.of();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }
}

