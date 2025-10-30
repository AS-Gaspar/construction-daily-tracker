This is a Kotlin Multiplatform project targeting Android, Server.

* [/composeApp](../composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
    - [commonMain](../composeApp/src/commonMain/kotlin) is for code that's common for all targets.
    - [androidMain](../composeApp/src/androidMain/kotlin) is for Android-specific code.

* [/server](../server/src/main/kotlin) is for the Ktor server application.

* [/shared](../shared/src) is for the code that will be shared between all targets in the project.
  The most important subfolder is [commonMain](../shared/src/commonMain/kotlin). If preferred, you
  can add code to the platform-specific folders here too.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:

- on Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:

- on Linux
  ```shell
  ./start-server.sh
  ```
  
---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) and
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform).
