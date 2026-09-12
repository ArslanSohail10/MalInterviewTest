# NUMBERS.md

### Overdraft Fee: AED 25.00
- **Why this value?** Explicitly defined in the requirements.
- **Why not half?** AED 12.50 would not match the specification. The fee is a fixed penalty intended to discourage negative balances and cover administrative costs of managing an overdrawn account.

### Daily Interest Rate: 0.04% (0.0004)
- **Why this value?** Explicitly defined in the requirements.
- **Why not half?** 0.02% would underestimate the yield. 0.04% per day approximates to ~15.7% APY (compounded), which is a common (though high) rate for certain types of accounts or penalty interest.

### Precision: AED (2), BHD (3)
- **Why?** These follow ISO 4217 standards for the United Arab Emirates Dirham and Bahraini Dinar. Storing and rounding to these precisions is a non-negotiable rule of the assignment.
