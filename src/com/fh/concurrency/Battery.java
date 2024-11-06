package com.fh.concurrency;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Battery {
    private final double capacity;
    private double currentCharge;
    private final Lock lock = new ReentrantLock();
    private final Semaphore usageSemaphore;

    public Battery(double capacity, int maxUsageSlots) {
        this.capacity = capacity;
        this.currentCharge = 0;
        this.usageSemaphore = new Semaphore(maxUsageSlots); // Controls maximum concurrent usage
    }

    // Thread to handle charging from different energy sources
    public class ChargingThread extends Thread {
        private final double amount;
        private final String source;

        public ChargingThread(double amount, String source) {
            this.amount = amount;
            this.source = source;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                currentCharge = Math.min(capacity, currentCharge + amount);
                System.out.println("Charging from " + source + ". Current charge: " + currentCharge);
                // Log the charging activity to a log file with the equipment name
                logChargingActivity(source, amount);
            } finally {
                lock.unlock();
            }
        }
    }

    // Thread to handle usage of energy
    public class UsageThread extends Thread {
        private final double amount;

        public UsageThread(double amount) {
            this.amount = amount;
        }

        @Override
        public void run() {
            if (usageSemaphore.tryAcquire()) { // Controls access to prevent overload
                lock.lock();
                try {
                    if (currentCharge >= amount) {
                        currentCharge -= amount;
                        System.out.println("Used " + amount + " energy. Remaining charge: " + currentCharge);
                    } else {
                        System.out.println("Insufficient charge!");
                    }
                } finally {
                    lock.unlock();
                    usageSemaphore.release();
                }
            } else {
                System.out.println("System overload: Usage limit reached");
            }
        }
    }

    // Public methods to create and start ChargingThread and UsageThread
    public void startCharging(double amount, String source) {
        new ChargingThread(amount, source).start();
    }

    public void startUsingEnergy(double amount) {
        new UsageThread(amount).start();
    }

    // Method to log the charging activity to a file, including the equipment name
    private void logChargingActivity(String source, double amount) {
        String homeDirectory = System.getProperty("user.home");
        String logDirectory = "energy_log.txt"; // Simple log file in the home directory
        Path logFilePath = Paths.get(homeDirectory, logDirectory);

        try {
            Files.write(logFilePath, 
                (source + " charged by " + amount + " units at " + System.currentTimeMillis() + "\n").getBytes(),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Charging activity logged to " + logFilePath.toString());
        } catch (IOException e) {
            System.err.println("Error logging charging activity: " + e.getMessage());
        }
    }
}
