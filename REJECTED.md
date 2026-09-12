# REJECTED.md

## Rejected Acceptance Criteria

### 1. "E7 causes exactly one overdraft fee to be assessed, on Day 2."
- **Reasoning:** E7 is a back-dated DEBIT (Day 5 event with `value_date` Day 2). In an append-only ledger, the state of the ledger on Day 2 was positive. The overdraft is only "visible" once E7 is recorded on Day 5. Furthermore, since E7's `value_date` is Day 2, it makes the closing balances of Day 2, Day 3, and Day 4 all negative. According to the rule "assessed once per day... when that day's closing ledger balance is negative", this should trigger fees for each day the balance remained negative (Days 2, 3, and 4), not just Day 2.
- **Approach:** Fees will be assessed for every day that has a negative closing balance at the time of evaluation.

### 2. "After E9, all balances and fees return to their pre-E7 values."
- **Reasoning:** E9 is a REVERSAL of E7. While the ledger balance will eventually return to the same value, the overdraft fees triggered by E7 are separate events. Since the ledger is append-only and "no event record is ever mutated or deleted", those fee events remain in the ledger. Unless specifically reversed by new events, the fees still exist.
- **Approach:** Balance will be restored by a credit event (E9), but previously assessed fees will remain unless the requirements explicitly state they should be automatically reversed (which contradicts the append-only/no-mutation rule).

### 3. "The three BHD instalments in E10 must each be BHD 3.334."
- **Reasoning:** Three instalments of 3.334 sum to 10.002. The total credit is exactly 10.000.
- **Approach:** Instalments will be split as 3.333, 3.333, and 3.334 to ensure the sum is exactly 10.000.

### 4. "If the rounded daily interest accruals do not sum to the capitalized total, the remainder is discarded."
- **Reasoning:** The non-negotiable rules state: "The rounded daily accruals must sum exactly to the capitalized total." Discarding the remainder would violate this rule.
- **Approach:** The final capitalized credit will be the exact sum of the daily rounded accruals.

### 5. "Any settlement referencing an authorization ID not present in the ledger must be rejected..."
- **Reasoning:** In real-world ledger systems, "forced settlements" (settlements without a matching authorization) are common (e.g., offline transactions, late presentments). E6 specifically introduces a settlement for `Auth-Z` which has no prior authorization. If we reject this, we ignore a valid financial instruction in the stream.
- **Approach:** Settlements without preceding authorizations will be accepted and processed as direct debits, as they represent a final movement of funds.

## Abandoned Approaches
- **Retroactive Fee Deletion:** Initially considered deleting fees when E7 was reversed, but abandoned this as it violates the "append-only" and "no mutation" rules.
