package com.fh.concurrency;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        // Setting up components
        String logDirectory = "logs";
        Metadata metadata = new Metadata();
        LogManager logManager = new LogManager(logDirectory);
        EnergyDataExchange energyDataExchange = new EnergyDataExchange(logDirectory, metadata);
        LogSearcher logSearcher = new LogSearcher(logDirectory, metadata);
        Battery battery = new Battery(100.0, 3); // Initialize battery with max capacity of 100 and 3 usage slots

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Energy Management System ---\n" +
                    "1. Create Log File\n2. Delete Log File\n3. Move Log File\n" +
                    "4. Archive Old Logs\n5. Log Energy Data\n6. Retrieve Energy Data\n" +
                    "7. Concurrent Search by Date\n8. Concurrent Search by Equipment\n" +
                    "9. Concurrent Battery Charging\n10. Concurrent Battery Usage\n0. Exit\nChoose an option: ");
            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1:
                        System.out.print("Enter log file name: ");
                        String createFileName = scanner.nextLine();
                        logManager.createLogFile(createFileName);
                        break;

                    case 2:
                        System.out.print("Enter log file name to delete: ");
                        String deleteFileName = scanner.nextLine();
                        logManager.deleteLogFile(deleteFileName);
                        break;

                    case 3:
                        System.out.print("Enter log file name to move: ");
                        String moveFileName = scanner.nextLine();
                        System.out.print("Enter new directory to move the file: ");
                        String newDirectory = scanner.nextLine();
                        logManager.moveLogFile(moveFileName, newDirectory);
                        break;

                    case 4:
                        System.out.print("Enter the number of days to archive logs older than: ");
                        int days = scanner.nextInt();
                        logManager.archiveOldLogs(days);
                        break;

                    case 5:
                        System.out.print("Enter equipment name to log energy data for: ");
                        String equipmentName = scanner.nextLine();
                        System.out.print("Enter energy data: ");
                        String energyData = scanner.nextLine();
                        energyDataExchange.logEnergyData(equipmentName, energyData);
                        break;

                    case 6:
                        System.out.print("Enter equipment name to retrieve energy data: ");
                        String retrieveEquipment = scanner.nextLine();
                        List<String> data = energyDataExchange.retrieveEnergyData(retrieveEquipment);
                        System.out.println("Energy data for " + retrieveEquipment + ":");
                        data.forEach(System.out::println);
                        break;

                    case 7: // Concurrent search by date
                        System.out.print("Enter date to search logs (format: YYYY-MM-DD): ");
                        String searchDate = scanner.nextLine();
                        Future<List<String>> dateFuture = logSearcher.searchByDateAsync(searchDate);
                        try {
                            List<String> dateResults = dateFuture.get();
                            System.out.println("Search results by date:");
                            dateResults.forEach(System.out::println);
                        } catch (ExecutionException | InterruptedException e) {
                            System.err.println("Error occurred during concurrent date search: " + e.getMessage());
                        }
                        break;

                    case 8: // Concurrent search by equipment
                        System.out.print("Enter equipment name to search logs: ");
                        String searchEquipment = scanner.nextLine();
                        Future<List<String>> equipmentFuture = logSearcher.searchByEquipmentAsync(searchEquipment);
                        try {
                            List<String> equipmentResults = equipmentFuture.get();
                            System.out.println("Search results by equipment:");
                            equipmentResults.forEach(System.out::println);
                        } catch (ExecutionException | InterruptedException e) {
                            System.err.println("Error occurred during concurrent equipment search: " + e.getMessage());
                        }
                        break;

                    case 9: // Concurrent battery charging simulation
                        System.out.print("Enter amount to charge: ");
                        double chargeAmount = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        System.out.print("Enter energy source: ");
                        String source = scanner.nextLine();
                        new Thread(() -> battery.charge(chargeAmount, source)).start(); // start charging thread
                        break;

                    case 10: // Concurrent battery usage with overload control
                        System.out.print("Enter amount to use: ");
                        double usageAmount = scanner.nextDouble();
                        scanner.nextLine(); // consume newline
                        new Thread(() -> {
                            boolean success = battery.useEnergy(usageAmount);
                            if (!success) {
                                System.out.println("Failed to use energy: System might be overloaded or insufficient charge.");
                            }
                        }).start(); // start usage thread
                        break;

                    case 0:
                        running = false;
                        logSearcher.shutdown(); // shutdown the log searcher thread pool
                        System.out.println("Exiting the system. Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number or '0' to exit.");
            }
        }
        scanner.close();
    }
}
