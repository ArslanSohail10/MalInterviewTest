import java.math.BigDecimal
import java.math.RoundingMode

sealed class Currency(val precision: Int, val code: String) {
    data object AED : Currency(2, "AED")
    data object BHD : Currency(3, "BHD")

    companion object {
        fun fromString(code: String): Currency {
            return when (code.uppercase()) {
                "AED" -> AED
                "BHD" -> BHD
                else -> throw IllegalArgumentException("Unknown currency: $code")
            }
        }
    }
}

data class Amount(val value: BigDecimal, val currency: Currency) {
    fun rounded(): Amount {
        return Amount(value.setScale(currency.precision, RoundingMode.HALF_EVEN), currency)
    }

    operator fun plus(other: Amount): Amount {
        require(currency == other.currency)
        return Amount(value + other.value, currency)
    }

    operator fun minus(other: Amount): Amount {
        require(currency == other.currency)
        return Amount(value - other.value, currency)
    }

    operator fun times(factor: BigDecimal): Amount {
        return Amount(value * factor, currency)
    }

    fun negate(): Amount = Amount(value.negate(), currency)

    fun isNegative(): Boolean = value < BigDecimal.ZERO
    fun isZero(): Boolean = value.compareTo(BigDecimal.ZERO) == 0

    override fun toString(): String = "${currency.code} ${value.setScale(currency.precision, RoundingMode.HALF_EVEN)}"
}

sealed class EventType {
    data object CREDIT : EventType()
    data object DEBIT : EventType()
    data object AUTHORIZATION : EventType()
    data object SETTLEMENT : EventType()
    data object REVERSAL : EventType()
    data object FEE : EventType()
    data object INTEREST : EventType()
}

data class Event(
    val id: String,
    val day: Int,
    val type: EventType,
    val accountId: String,
    val amount: Amount,
    val valueDate: Int,
    val refId: String? = null,
    val instalments: Int = 1,
    val metadata: Map<String, String> = emptyMap()
)

data class Hold(
    val authId: String,
    val amount: Amount,
    val day: Int
)

data class LedgerEntry(
    val eventId: String,
    val amount: Amount,
    val valueDate: Int,
    val type: EventType
)

data class DailyReport(
    val day: Int,
    val accountId: String,
    val closingBalance: Amount,
    val fees: List<Amount>,
    val activeHolds: List<Hold>,
    val errors: List<String>
)
