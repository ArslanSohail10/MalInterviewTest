# WORKLOG

## 2026-09-12 15:10
- Initial analysis of the assignment and event stream (E1-E10).
- Identified incorrect acceptance criteria and initiated `REJECTED.md`.
- Documented project constants and ambiguities in `NUMBERS.md` and `AMBIGUITIES.md`.
- Setup project structure and documentation templates.

## 2026-09-12 15:35
- Implemented `Models.kt` with high-precision `BigDecimal` support for AED and BHD.
- Developed `LedgerCore.kt` featuring append-only event booking and back-dated fee assessment logic.
- Integrated daily interest accrual (0.04%) and capitalization rules.

## 2026-09-12 16:15
- Created `Main.kt` to replay the assignment's event stream.
- Verified overdraft fee triggers for Day 2, 3, and 4 during Day 5 processing.
- Implemented exact BHD instalment splitting (3.333, 3.333, 3.334) to satisfy precision rules.
- Added `LedgerTest.kt` with the requested failing test case to reveal append-only limitations.

## 2026-09-12 17:05
- Refactored `LedgerCore.kt` to handle instalment logic natively within the event processing flow.
- Updated `README.md` with comprehensive instructions for both Gradle and manual execution.
- Final validation of all "Non-negotiable rules".

## 2026-09-12 17:25
- Final verification of the ledger state at end of Day 6.
- Ensured commit history is intact and documented according to Deliverable 1 requirements.
- Readiness for submission confirmed.
