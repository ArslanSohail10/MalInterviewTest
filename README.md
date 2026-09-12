# Account Ledger Core

An in-memory account ledger implementation in Kotlin.

## How to Run

1.  Ensure you have a JDK (11+) and Kotlin installed.
2.  Use the provided Gradle wrapper:
    ```bash
    ./gradlew test
    ```
3.  The test suite will execute the event stream and print the daily reports to the console.

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
