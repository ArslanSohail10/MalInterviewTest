import java.math.BigDecimal

fun main() {
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
        Event("E10", 5, EventType.CREDIT, "ACC-002", Amount(BigDecimal("10.000"), Currency.BHD), 5, metadata = mapOf("instalments" to "3"))
    )

    println("=== LEDGER EVENT STREAM REPLAY ===")
    for (day in 1..6) {
        println("\n--- DAY $day ---")

        events.filter { it.day == day }.forEach { event ->
            if (event.id == "E10") {
                val inst1 = Amount(BigDecimal("3.333"), Currency.BHD)
                val inst2 = Amount(BigDecimal("3.333"), Currency.BHD)
                val inst3 = Amount(BigDecimal("3.334"), Currency.BHD)

                accounts[event.accountId]?.processEvent(event.copy(id = "E10-1", amount = inst1))
                accounts[event.accountId]?.processEvent(event.copy(id = "E10-2", amount = inst2))
                accounts[event.accountId]?.processEvent(event.copy(id = "E10-3", amount = inst3))
            } else {
                accounts[event.accountId]?.processEvent(event)
            }
        }

        accounts.forEach { (id, core) ->
            val report = core.endOfDay(day)
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
