# Ticket Machine AE2

This project is a desktop Ticket Machine System developed in Kotlin using
Compose Multiplatform (JVM) as part of an Object-Oriented Software Engineering assignment.

## Features
- Customer ticket purchase (single / return tickets)
- Card-based payment with balance validation
- Automatic application of special offers and discounts
- Admin panel with authentication
- Admin management of destinations, prices and special offers
- Persistent data storage using SQLite

## Technologies
- Kotlin (JVM)
- Compose Multiplatform (Desktop)
- SQLite
- Gradle

## Diagrams
All UML diagrams (use case, sequence, class diagrams) are included in the `/diagrams` folder.

## Testing
The project includes unit tests for selected domain and repository logic
(e.g. Destination pricing logic).

---





This is a Kotlin Multiplatform project targeting Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
      For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
      Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
      folder is the appropriate location.

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
