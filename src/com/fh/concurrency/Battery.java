package com.fh.concurrency;


import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Battery {
    private double capacity;
    private double currentCharge;
    private final Lock lock = new ReentrantLock();
    private final Semaphore usageSemaphore;

    public Battery(double capacity, int maxUsageSlots) {
        this.capacity = capacity;
        this.currentCharge = 0;
        this.usageSemaphore = new Semaphore(maxUsageSlots); // Controls maximum concurrent usage
    }

    // Charge the battery from various energy sources concurrently
    public void charge(double amount, String source) {
        lock.lock();
        try {
            currentCharge = Math.min(capacity, currentCharge + amount);
            System.out.println("Charging from " + source + ". Current charge: " + currentCharge);
        } finally {
            lock.unlock();
        }
    }

    // Use energy from the battery with overload control
    public boolean useEnergy(double amount) {
        if (usageSemaphore.tryAcquire()) { // Controls access to prevent overload
            lock.lock();
            try {
                if (currentCharge >= amount) {
                    currentCharge -= amount;
                    System.out.println("Used " + amount + " energy. Remaining charge: " + currentCharge);
                    return true;
                } else {
                    System.out.println("Insufficient charge!");
                    return false;
                }
            } finally {
                lock.unlock();
                usageSemaphore.release();
            }
        } else {
            System.out.println("System overload: Usage limit reached");
            return false;
        }
    }
}
