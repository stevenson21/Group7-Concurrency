package com.fh.concurrency;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Setting up the log directory and creating necessary objects
        String logDirectory = "logs";
        Metadata metadata = new Metadata();
        LogManager logManager = new LogManager(logDirectory);

        // Initialize Battery with 100 capacity and 3 max concurrent usage slots
        Battery battery = new Battery(100.0, 3); 

        EnergyDataExchange energyDataExchange = new EnergyDataExchange(logDirectory, metadata);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Energy Management System ---\n" +
                    "1. Create Log File\n2. Delete Log File\n3. Move Log File\n" +
                    "4. Archive Old Logs\n5. Log Energy Data\n6. Retrieve Energy Data\n" +
                    "7. Search Log Files by Date\n8. Search Log Files by Equipment Name\n" +
                    "9. Open Log File\n10. Charge Battery\n11. Use Energy from Battery\n0. Exit\nChoose an option: ");

            String input = scanner.nextLine();

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1: // Create log file
                        System.out.print("Enter log file name: ");
                        String createFileName = scanner.nextLine();
                        logManager.createLogFile(createFileName);
                        break;

                    case 2: // Delete log file
                        System.out.print("Enter log file name to delete: ");
                        String deleteFileName = scanner.nextLine();
                        logManager.deleteLogFile(deleteFileName);
                        break;

                    case 3: // Move log file
                        System.out.print("Enter log file name to move: ");
                        String moveFileName = scanner.nextLine();
                        System.out.print("Enter new directory to move the file: ");
                        String newDirectory = scanner.nextLine();
                        logManager.moveLogFile(moveFileName, newDirectory);
                        break;

                    case 4: // Archive old logs files
                        System.out.print("Enter the number of days to archive logs older than: ");
                        int days = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        logManager.archiveOldLogs(days);
                        break;

                    case 5: // Log energy data for equipment (e.g., solar or wind stations)
                        System.out.print("Enter equipment name to log energy data for: ");
                        String equipmentName = scanner.nextLine();
                        System.out.print("Enter energy data: ");
                        String energyData = scanner.nextLine();
                        energyDataExchange.logEnergyData(equipmentName, energyData);
                        break;

                    case 6: // Retrieve and display energy data
                        System.out.print("Enter equipment name to retrieve energy data: ");
                        String retrieveEquipment = scanner.nextLine();
                        List<String> data = energyDataExchange.retrieveEnergyData(retrieveEquipment);
                        System.out.println("Energy data for " + retrieveEquipment + ":");
                        data.forEach(System.out::println);
                        break;


                    case 7: // Search for log files by date
                        System.out.println("Starting multiple searches for logs by date...");
                        String[] searchDates = {"2024-11-01", "2024-10-25", "2024-09-15", "2024-08-10", "2024-07-05"};
                        for (String searchDate : searchDates) {
                            Thread dateSearchThread = new Thread(() -> {
                                LogSearcher dateSearcher = new LogSearcher(logDirectory, metadata, searchDate);
                                System.out.println("Searching for logs with date: " + searchDate);
                                dateSearcher.start();  // Start the thread for searching by date
                            });
                            dateSearchThread.start();
                            try {
                                Thread.sleep(1000); // Sleep for 1 second to stagger the searches and show concurrency
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        break;

                    case 8: // Search for log files by equipment name
                        System.out.println("Starting multiple searches for logs by equipment...");
                        String[] searchEquipmentNames = {"SolarPanel", "WindTurbine", "HydroGenerator", "BatteryStorage", "SolarInverter"};
                        for (String equipmentNam : searchEquipmentNames) {
                            Thread equipmentSearchThread = new Thread(() -> {
                                LogSearcher equipmentSearcher = new LogSearcher(logDirectory, metadata, equipmentNam);
                                System.out.println("Searching for logs related to equipment: " + equipmentNam);
                                equipmentSearcher.start();  // Start the thread for searching by equipment name
                            });
                            equipmentSearchThread.start();
                            try {
                                Thread.sleep(1500); // Sleep for 1.5 seconds to stagger the searches and show concurrency
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        break;

                    case 9: // Open and display a specific log file
                        System.out.print("Enter the log file name to open (e.g., log_2024-10-09.log): ");
                        String openFileName = scanner.nextLine();
                        LogSearcher fileOpener = new LogSearcher(logDirectory, metadata, openFileName);
                        fileOpener.openLogFile(openFileName);  // Open and display log file contents
                        break;

                    case 10: // Charge the battery
                        System.out.println("Starting multiple battery charging operations...");
                        double[] chargeAmounts = {100, 200, 150, 50, 300}; // Different charge amounts
                        String[] energySources = {"Solar", "Wind", "Hydro", "Geothermal", "Nuclear"}; // Different energy sources
                        for (int i = 0; i < chargeAmounts.length; i++) {
                            final double chargeAmount = chargeAmounts[i];
                            final String source = energySources[i];
                            Thread chargeThread = new Thread(() -> {
                                System.out.println("Charging with " + source + " energy. Amount: " + chargeAmount);
                                battery.startCharging(chargeAmount, source);
                            });
                            chargeThread.start();
                            try {
                                Thread.sleep(2000); // Sleep for 2 seconds to stagger the charging operations
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        break;

                    case 11: // Use energy from the battery
                        System.out.println("Starting multiple battery usage operations...");
                        double[] useAmounts = {50, 75, 100, 25, 30}; // Different energy usage amounts
                        for (double useAmount : useAmounts) {
                            Thread usageThread = new Thread(() -> {
                                System.out.println("Attempting to use " + useAmount + " energy.");
                                battery.startUsingEnergy(useAmount);
                            });
                            usageThread.start();
                            try {
                                Thread.sleep(2500); // Sleep for 2.5 seconds to stagger the usage operations
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        break;
                    case 0: // Exit
                        running = false;
                        System.out.println("Exiting the system. Goodbye!");
                        break;

                    default: // Invalid input
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (NumberFormatException | EMSException e) {
                System.out.println("Invalid input. Please enter a number or '0' to exit.");
            }
        }

        scanner.close();
    }
}
