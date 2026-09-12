# AMBIGUITIES.md

### 1. Back-dated events and Fee Assessment
- **Ambiguity:** If an event is back-dated (like E7), when are the fees for the preceding days assessed?
- **Resolution:** Fees are assessed at the end of each day in the simulation. If a back-dated event is processed on Day 5 but affects Day 2, the "once per day" check for Day 2 will be re-run (or historically checked) during the end-of-day processing of Day 5. This ensures that a back-dated debit eventually triggers all applicable overdraft fees.

### 2. "Reversal" Implementation
- **Ambiguity:** How should E9 (Reversal of E7) be implemented in an append-only ledger?
- **Resolution:** E9 is implemented as a new `CREDIT` event with the same amount as E7. It does not delete E7. It also carries the same `value_date` as the original event to fix the historical balance trajectory.

### 3. Interest Calculation Precision
- **Ambiguity:** "Rounded daily accruals must sum exactly to the capitalized total." How to handle rounding?
- **Resolution:** Daily interest is calculated as `balance * 0.0004`. This value is rounded to the currency's precision (2 for AED, 3 for BHD) and stored as a "pending accrual". On Day 6, all these rounded values are summed to create a single capitalized credit event.

### 4. Authorization Reversal/Expiry
- **Ambiguity:** What happens to Auth-B which is "never settled"?
- **Resolution:** It remains as an active hold, reducing the available balance until the end of the simulation. It does not affect the ledger balance.

### 5. Settlement Amount Mismatch
- **Ambiguity:** What if a settlement is for a different amount than the hold (e.g., E5)?
- **Resolution:** The hold is fully released, and the settlement amount is debited from the ledger balance. This is standard banking practice.
