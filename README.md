# Account Ledger Core

An in-memory account ledger implementation in Kotlin.

## How to Run

### Option 1: Using the provided Runner (Main.kt)
If you have `kotlinc` installed, you can run the simulation directly:
```bash
kotlinc src/main/kotlin/*.kt -include-runtime -d ledger.jar && java -jar ledger.jar
```

### Option 2: Using Gradle
If you have Gradle installed:
```bash
./gradlew run
```
(Note: You may need to add a `java { mainClass = ... }` to `build.gradle.kts` for this to work, or just use `test`).

### Option 3: Running Tests
```bash
./gradlew test
```
The test suite contains:
1. `runAssignmentSimulation`: Replays the event stream and prints reports.
2. `testFailingFeeReversal`: A test designed to fail, revealing the append-only nature of the ledger regarding fees.

## Reading the Output

The output is grouped by day. For each day, it prints:
- **Closing Ledger Balance:** The sum of all booked entries with `value_date <= current_day`.
- **Fee Assessments:** Any overdraft fees booked on that day.
- **Authorization States:** Active holds currently on the account.
- **Errors:** Any rejected events (e.g., failed authorizations).

## Project Structure
- `src/main/kotlin/Models.kt`: Data structures.
- `src/main/kotlin/LedgerCore.kt`: Business logic.
- `src/test/kotlin/LedgerTest.kt`: Event stream replay and verification.
