 Mt Buller Winter Resort

Student: Angelo Christou
Student Number: S3969084
Course: Further Programming (RMIT)
Project Type: Java GUI Application (Swing) with MVC Architecture and SQLite Database

 Overview

The Mt Buller Winter Resort Management System is a Java-based desktop application designed to manage resort operations, including customer registration, accommodation bookings, and staff administration.

It was built following the Model-View-Controller (MVC) design pattern, as taught in RMIT’s Further Programming course. The project demonstrates database connectivity, GUI event handling, and role-based access control.

 System Features

Login System with user authentication and role distinction

Master / Admin roles controlling access levels

Customer Management (add, delete, view customers)

Accommodation Management (create, view, deactivate)

Booking Management (assign customers, select dates, add lift passes and lessons)

Database Auto-Bootstrap (creates and migrates schema on launch)

Data Persistence using SQLite through JDBC

Roles and Credentials

Two built-in accounts are automatically seeded by the system for demonstration and testing.

Role	Username	Password	Description
MASTER	master	Master123!	Full system privileges: can manage users, admins, and perform database maintenance.
ADMIN	admin	Admin123!	Can manage customers, accommodations, and bookings. Cannot alter master user or system configuration.

Passwords are stored as hashed values in the database for integrity and security demonstration purposes.

Database Information

Database: SQLite
Package: com.rmit.mtbuller.db
Main Class: Bootstrap.java

On first launch, Bootstrap.ensureSchemaAndSeed() automatically:

Creates tables for users, customers, accommodations, and bookings.

Migrates legacy customer schema if necessary.

Adds new fields for lift passes and lessons.

Inserts initial seed data for demonstration (users, accommodations, customers).

Schema validation and migrations are handled dynamically, ensuring smooth upgrades between versions.

Application Structure

Key Packages:

com.rmit.mtbuller.view – GUI classes (JFrame, JPanel interfaces, layout, and event handling)

com.rmit.mtbuller.controller – Controls logic flow and user interactions

com.rmit.mtbuller.model – Domain objects (Customer, Accommodation, Booking, User)

com.rmit.mtbuller.db – Database connectivity and schema bootstrap

This modular structure allows clear separation of concerns:

Model: Represents data and business rules

View: User interface components

Controller: Mediates input and updates between View and Model

Running the Application

Open the project in VS Code or IntelliJ IDEA.

Ensure sqlite-jdbc dependency is available in your classpath or Maven project.

Run the main class (typically MainMenu or ResortGUI).

Log in using one of the seeded accounts (see credentials above).

The system automatically creates or updates the database (resort.db) on startup in the working directory.

Example Use Case

Login as admin / Admin123!

Navigate to Customer Management → Add a new customer.

Go to Accommodation Management → View or add available rooms.

Proceed to Booking → Assign customer, select dates, and optionally add lift passes or lessons using popup sliders.

System saves and validates all transactions in SQLite.

Reference

This project follows the object-oriented principles and MVC architecture demonstrated in the RMIT Further Programming course.
It applies:

Abstraction and Encapsulation

Inheritance and Polymorphism

Event Handling and GUI design (Swing)

JDBC Database Access and Exception Handling

Resources

SQLite JDBC Driver: https://github.com/xerial/sqlite-jdbc

Java SE API Documentation: https://docs.oracle.com/javase/8/docs/api/

RMIT Further Programming Course Material (COSC2391) – MVC, Event Handling, File and Database I/O
