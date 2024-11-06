# Group7-Concurrency

# LogSearcher - Log Search and Battery Management System

This project implements a log search and battery management system for an energy management application. It allows users to search log files based on various criteria (such as date or equipment name) and also provides functionality for battery charging and energy usage. The system demonstrates concurrency by using multi-threaded operations for log searching and battery management.

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
  - [Search Log Files by Date](#search-log-files-by-date)
  - [Search Log Files by Equipment Name](#search-log-files-by-equipment-name)
  - [Charge the Battery](#charge-the-battery)
  - [Use Energy from the Battery](#use-energy-from-the-battery)
- [Concurrency](#concurrency)
- [File Structure](#file-structure)
- [Error Handling](#error-handling)
- [License](#license)

## Features
- **Search log files** by date or equipment name.
- **View log file contents** for specific logs.
- **Battery management system** to simulate charging and energy usage.
- **Concurrency**: All search operations and battery management tasks are handled concurrently to optimize performance.

## Installation
1. **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/log-searcher.git
    cd log-searcher
    ```

2. **Set up the project**:
    If you're using an IDE (such as IntelliJ IDEA or Eclipse), you can import the project directly. Otherwise, compile and run the Java files manually using the `javac` and `java` commands.

    ```bash
    javac *.java
    ```

3. **Make sure you have Java 8 or higher installed** to run the program.

## Usage
### Search Log Files by Date
To search log files by date, the program accepts a date in the format `YYYY-MM-DD`. It will search for log files that contain the specified date in their filename.

**Example**:
- Input: `2024-11-01`
- Output:
    ```
    Search results for 2024-11-01:
    /home/user/Documents/logs/log_2024-11-01.log
    /home/user/Documents/logs/log_2024-11-01_2.log
    ```

### Search Log Files by Equipment Name
The program also allows you to search for log files by equipment name (e.g., `SolarPanel`, `WindTurbine`).

**Example**:
- Input: `SolarPanel`
- Output:
    ```
    Search results for SolarPanel:
    /home/user/Documents/logs/log_solarpanel_log1.log
    /home/user/Documents/logs/log_solarpanel_log2.log
    ```

### Charge the Battery
The system can simulate charging the battery by accepting a charge amount and a source of energy (e.g., Solar, Wind, etc.).

**Example**:
- Input:
    ```
    Enter amount to charge the battery: 100.5
    Enter source of energy (e.g., Solar, Wind): Solar
    ```

### Use Energy from the Battery
The system also allows you to simulate using energy from the battery. The amount of energy used will be deducted from the battery's current charge.

**Example**:
- Input:
    ```
    Enter amount of energy to use from the battery: 50.0
    ```

## Concurrency
The application makes use of concurrency in several places:
- **Log Searching**: The `LogSearcher` class extends `Thread` and performs the search operation in a separate thread for both date and equipment-based searches. This allows the program to search logs without blocking the main thread.
- **Battery Management**: Battery charging and energy usage are handled in their respective threads, allowing the system to simulate real-time energy management.

### Concurrency Example:
When you search by date or equipment name, each search operation is run on a separate thread to improve performance and ensure that multiple searches can be performed concurrently.

```java
// Example usage
LogSearcher dateSearcher = new LogSearcher(logDirectory, metadata, searchDate);
dateSearcher.start();  // Start the thread for searching by date

LogSearcher equipmentSearcher = new LogSearcher(logDirectory, metadata, searchEquipment);
equipmentSearcher.start();  // Start the thread for searching by equipment
