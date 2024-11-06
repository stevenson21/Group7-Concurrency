package com.fh.concurrency;


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
}
