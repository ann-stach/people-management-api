# People Management API

Built with **Spring Boot 3**, this system provides management for different types of people (e.g., employees, students, retirees). It allows flexible searching through the use of the **Criteria API**, ensures secure access with **Spring Security**, and supports versioned database migrations with **Liquibase**. The **Facade pattern** ensures flexibility, simplifying future extensions without modifying existing code. The system is designed for **scalability** and **concurrency safety**.

---

## System Overview

This **people management system** is a web application built on **Spring Boot 3**. It allows for the storage and search of various person types, such as employees, students, and retirees. The system provides a single, universal endpoint for fetching people based on different criteria, supporting pagination and filtering by **PESEL number**, name, surname, age, height, weight, and other specific parameters. This functionality is implemented using **Criteria API**. The system ensures that adding new person types doesn't require modifying existing classes, and it provides validation and error handling to ensure data integrity. Editing a person supports **optimistic locking** to protect against concurrency issues and "missing updates". It also supports managing **employee positions**, preventing overlaps in employment dates. The application handles **asynchronous data import** from large CSV files efficiently, with a transactional approach, enabling up to **50,000 inserts/s** for H2.

To ensure maximum flexibility and future extensibility, the **Facade pattern** is used, simplifying interaction with application modules and enabling easy expansion. Database migrations are managed using **Liquibase**, which allows controlled and versioned schema changes. The application is secured using **Spring Security**, controlling access to system functions. It is designed to operate in scalable, multi-instance environments, ensuring concurrency safety.

---

## Example JSON Requests

### 1. **Search Request Example** (`@PostMapping("/search")`)

```json
    {
      "criteria": [
        {"key": "numberOfPositions", "operation": "range", "value": [0, 1]},
        {"key": "weight", "operation": "range", "value": [50, 60]},
        {"key": "gender", "operation": "containsIgnoreCase", "value": "f"},
        {"key": "university", "operation": "containsIgnoreCase", "value": "state"},
        {"key": "name", "operation": "containsIgnoreCase", "value": "John"}
      ]
    }

```
### 2. **Create Person Command Example** (`@PostMapping("/createPerson")`)

```json
    {
      "classType": "Employee",
      "parameters": [
        { "name": "name", "value": "John" },
        { "name": "surname", "value": "Doe" },
        { "name": "pesel", "value": "74032813943" },
        { "name": "height", "value": "180" },
        { "name": "weight", "value": "75" },
        { "name": "email", "value": "john.doe@example.com" }
      ]
    }
```

```json
    {
      "classType": "Retiree",
      "parameters": [
        { "name": "name", "value": "Jane" },
        { "name": "surname", "value": "Smith" },
        { "name": "pesel", "value": "61022717475" },
        { "name": "height", "value": "165" },
        { "name": "weight", "value": "65" },
        { "name": "email", "value": "jane.smith@example.com" },
        { "name": "pension", "value": "3000" },
        { "name": "yearsWorked", "value": "40" }
      ]
    }
```

```json
    {
      "classType": "Student",
      "parameters": [
        { "name": "name", "value": "Emily" },
        { "name": "surname", "value": "Johnson" },
        { "name": "pesel", "value": "05212686866" },
        { "name": "height", "value": "170" },
        { "name": "weight", "value": "60" },
        { "name": "email", "value": "emily.johnson@example.com" },
        { "name": "university", "value": "Harvard University" },
        { "name": "academicYear", "value": "3" },
        { "name": "course", "value": "Computer Science" },
        { "name": "scholarship", "value": "2000" }
      ]
    }
```
### 3. ** Edit Person Command Example** (`@PutMapping("/editPerson/{id}")`)

```json
        {
          "id": 1,
          "classType": "Employee",
          "parameters": [
            { "name": "name", "value": "John" },
            { "name": "surname", "value": "Doe" },
            { "name": "pesel", "value": "74032813943" },
            { "name": "height", "value": "180" },
            { "name": "weight", "value": "75" },
            { "name": "email", "value": "john.doe@example.com" }
          ],
         "version": "0"
        }
```

### 4. **Create Position Command Example** (`@PostMapping("/createPosition")`)

```json
      {
        "name": "Chief Coffee Tester",
        "dateFrom": "2024-06-01",
        "dateTo": null,
        "salary": 1000000
        }
```
