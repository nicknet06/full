# Car Rental Management App

Java desktop application (Swing) for basic car rental management, including employees, cars, customers, rentals, and returns.

## Project structure

- `/home/runner/work/full/full/full_project/src` — Java source code
  - `Main.java` — application entry point
  - `api/` — entities, services, and data repositories
  - `gui/` — Swing UI panels and main frame
- `/home/runner/work/full/full/full_project/data` — seed CSV files (`cars.csv`, `customers.csv`, `employees.csv`, `rentals.csv`)

## Features

- Employee login flow
- Manage cars, customers, and employees
- Create and return rentals
- Persist data between runs (loads saved state when available)

## Requirements

- Java JDK 8+ (recommended: JDK 11 or newer)

## Run locally

From `/home/runner/work/full/full/full_project`:

```bash
javac -d out $(find src -name "*.java")
java -cp out Main
```

## Notes

- The app uses the `data/` directory for initial CSV data.
- On first run, data is loaded and then persisted for subsequent runs.
