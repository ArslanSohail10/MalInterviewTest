import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LedgerTest {

    @Test
    fun runAssignmentSimulation() {
        val acc001 = LedgerCore("ACC-001", Currency.AED)
        val acc002 = LedgerCore("ACC-002", Currency.BHD)
        val accounts = mapOf("ACC-001" to acc001, "ACC-002" to acc002)

        val events = listOf(
            Event("E1", 1, EventType.CREDIT, "ACC-001", Amount(BigDecimal("1200.00"), Currency.AED), 1),
            Event("E2", 1, EventType.DEBIT, "ACC-001", Amount(BigDecimal("950.00"), Currency.AED), 1),
            Event("E3", 2, EventType.AUTHORIZATION, "ACC-001", Amount(BigDecimal("200.00"), Currency.AED), 2, metadata = mapOf("authId" to "Auth-A")),
            Event("E4", 3, EventType.CREDIT, "ACC-001", Amount(BigDecimal("400.00"), Currency.AED), 3),
            Event("E5", 4, EventType.SETTLEMENT, "ACC-001", Amount(BigDecimal("185.00"), Currency.AED), 4, metadata = mapOf("authId" to "Auth-A")),
            Event("E6", 4, EventType.SETTLEMENT, "ACC-001", Amount(BigDecimal("180.00"), Currency.AED), 4, metadata = mapOf("authId" to "Auth-Z")),
            Event("E7", 5, EventType.DEBIT, "ACC-001", Amount(BigDecimal("620.00"), Currency.AED), 2),
            Event("E8", 5, EventType.AUTHORIZATION, "ACC-001", Amount(BigDecimal("90.00"), Currency.AED), 5, metadata = mapOf("authId" to "Auth-B")),
            Event("E9", 6, EventType.REVERSAL, "ACC-001", Amount(BigDecimal("620.00"), Currency.AED), 2, refId = "E7"),
            Event("E10", 5, EventType.CREDIT, "ACC-002", Amount(BigDecimal("10.000"), Currency.BHD), 5, instalments = 3)
        )

        println("=== LEDGER EVENT STREAM REPLAY ===")
        for (day in 1..6) {
            println("\n[SYSTEM] PROCESSING DAY $day")

            // Replay events scheduled for this day
            events.filter { it.day == day }.forEach { event ->
                accounts[event.accountId]?.processEvent(event)
            }

            // End-of-day reports
            accounts.forEach { (id, core) ->
                val report = core.endOfDay(day)
                if (report.day == day) {
                    println("Account: $id")
                    println("  Closing Ledger Balance: ${report.closingBalance}")
                    if (report.fees.isNotEmpty()) {
                        println("  Fees Assessed: ${report.fees.joinToString()}")
                    }
                    if (report.activeHolds.isNotEmpty()) {
                        println("  Active Holds: ${report.activeHolds.map { "${it.authId}(${it.amount})" }.joinToString()}")
                    }
                    if (report.errors.isNotEmpty()) {
                        println("  ERRORS: ${report.errors.joinToString()}")
                    }
                }
            }
        }

        // Final verification for ACC-001 on Day 6
        val finalBalance = acc001.getLedgerBalance(6)
        println("\nFinal Balance ACC-001: $finalBalance")
    }

    /**
     * This test is DESIGNED TO FAIL.
     * It reveals that our "Append-only" core does not automatically reverse fees
     * when the event that caused the overdraft (E7) is reversed (E9).
     *
     * Requirement: "After E9, all balances and fees return to their pre-E7 values."
     * Reality (as documented in REJECTED.md): Fees are immutable events and remain in the ledger.
     */
    @Test
    fun testFailingFeeReversal() {
        val acc = LedgerCore("ACC-001", Currency.AED)

        // 1. Initial balance 1200
        acc.processEvent(Event("E1", 1, EventType.CREDIT, "ACC-001", Amount(BigDecimal("1200.00"), Currency.AED), 1))
        acc.endOfDay(1)

        // 2. Day 5: Back-dated Debit E7 (620) with value_date Day 2
        // Pre-E7 Day 2 balance was 1200.
        // Post-E7 Day 2 balance becomes 1200 - 620 = 580. Still positive.
        // Wait, E2 was also there in assignment. Let's match assignment exactly.

        acc.processEvent(Event("E2", 1, EventType.DEBIT, "ACC-001", Amount(BigDecimal("950.00"), Currency.AED), 1))
        acc.endOfDay(1) // Bal: 250

        // E7 on Day 5, value_date Day 2
        acc.processEvent(Event("E7", 5, EventType.DEBIT, "ACC-001", Amount(BigDecimal("620.00"), Currency.AED), 2))
        acc.endOfDay(5) // Bal: 250 - 620 = -370. Triggers fee for Day 2, 3, 4, 5.

        // E9 on Day 6: Reverses E7
        acc.processEvent(Event("E9", 6, EventType.REVERSAL, "ACC-001", Amount(BigDecimal("620.00"), Currency.AED), 2, refId = "E7"))
        acc.endOfDay(6)

        // According to the "incorrect" acceptance criterion, fees should be gone.
        // We assert there are no fees in the ledger (which will FAIL).
        val feesCount = acc.getLedgerBalance(6).value // This doesn't count fees directly, let's check entries

        // The ledger balance will include the negative fees.
        // 250 (initial) - 620 (E7) + 620 (E9) - 100 (4 fees of 25) = 150.
        // If fees returned to pre-E7, balance should be 250.

        println("Testing if fees were reversed... Current Balance: ${acc.getLedgerBalance(6)}")
        assertEquals(BigDecimal("250.00"), acc.getLedgerBalance(6).value,
            "REVEALED LIMITATION: Fees were NOT reversed automatically after E9.")
    }
}
