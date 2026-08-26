# EV project template

This is a project template for a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/ev/EV.java` file, right-click it, and choose `Run EV.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
    _______     __
   |   ____|   /  \
   |  |__     |    |
   |   __|    |    |
   |  |____    \  /
   |_______|    \/
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Building and running with Gradle

From the project root (`gradlew.bat` on Windows, `./gradlew` elsewhere):

| Command | What it does |
| --- | --- |
| `gradlew run` | Runs the chatbot in the terminal |
| `gradlew test` | Runs the JUnit tests |
| `gradlew build` | Compiles, tests and packages the app |

The text-UI regression tests are separate from Gradle: run `test\run-ui-tests.ps1` (PowerShell)
to replay every case in [test/ui-test-plan.md](test/ui-test-plan.md) against a freshly compiled app.

## Creating and running the JAR file

```
gradlew shadowJar
```

This produces `build/libs/ev.jar`, a fat JAR that bundles everything the app needs. To use it:

1. Copy `ev.jar` into an empty folder.
2. Open a command window in that folder.
3. Run `java -jar "ev.jar"`.

The chatbot saves your tasks in `data/duke.txt` **relative to the folder you run it from**, and
creates that folder on the first save, so the JAR needs no other files to work.
