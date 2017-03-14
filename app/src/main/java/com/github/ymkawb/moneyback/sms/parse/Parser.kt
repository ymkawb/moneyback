package com.github.ymkawb.moneyback.sms.parse

import com.github.ymkawb.moneyback.model.Operation
import java.math.BigInteger
import java.util.*
import java.util.regex.Pattern

/**
 * Created by nivanov on 03/03/17.
 */
abstract class BankSmsParser {
    abstract fun parse(input: String): Operation?
}

interface CardOpBuilder {

    val builders: List<(CardOperation, String) -> CardOperation>

    fun floatToBigInteger(input: String): BigInteger {
        var f = input.trim();
        val indexOfSeparator = input.indexOf('.')
        if (indexOfSeparator != -1) {
            f = f.removeRange(indexOfSeparator, indexOfSeparator + 1)
        }
        return BigInteger(f)
    }

    fun cardNumber(op: CardOperation, token: String): CardOperation {
        val split: List<String> = token.split(' ')
        if (split.size == 2) {
            return op.copy(cardNumber = split[1])
        }
        return op
    }

    fun operation(op: CardOperation, token: String): CardOperation {
        return op.copy(operation = token.trim())
    }

    fun amount(op: CardOperation, token: String): CardOperation {
        return op.copy(amount = floatToBigInteger(token.trim().split(' ')[0]))
    }

    fun date(op: CardOperation, token: String): CardOperation {
        return op.copy(date = token.trim().split(' ')[1])
    }


    fun accountBalance(op: CardOperation, token: String): CardOperation {
        val split = token.trim().split(' ')
        return op.copy(accountBalance = floatToBigInteger(split[2]))
    }

    fun buildFromTokens(token: List<String>, builder: List<(CardOperation, String) -> CardOperation> = builders): Operation {
        return token.zip(builder).fold(CardOperation(), { t, f -> f.second(t, f.first) })
    }
}


object RaiffaisenSmsParser : BankSmsParser() {

    private object IncomeSmsParser : CardOpBuilder {
        private val incomeRE = Pattern.compile(
                """Balans vashey karty (\*\d+) popolnilsya (\d\d/\d\d/\d\d\d\d) na (\d+\.\d\d \w+). (Dosstupny ostatok: \d+\.\d+ \w+). Raiffesinbank"""
        ).toRegex()
        //Match groups to build elems
        override val builders: List<(CardOperation, String) -> CardOperation> = listOf(
                { x, y -> x.copy(cardNumber = y) },
                { x, y -> x.copy(date = y) },
                { x, y -> amount(x, y) },
                { x, y -> accountBalance(x, y) }
        )

        fun parse(input: String): Operation? {
            val matchEntire = incomeRE.matchEntire(input)
            if (matchEntire != null) {
                return buildFromTokens(matchEntire.groupValues.drop(1))
            }
            return null
        }
    }


    private object ExpenseParser : CardOpBuilder {
        override val builders: List<(CardOperation, String) -> CardOperation> = listOf(
                { x, y -> cardNumber(x, y) },
                { x, y -> operation(x, y) },
                { x, y -> amount(x, y) },
                { x, y -> date(x, y) },
                { x, y -> accountBalance(x, y) }
        )

        fun parse(input: String): Operation? = buildFromTokens(StringTokenizer(input, ";").toList().map(Any::toString))

    }

    val prefixMap: Map<String, (String) -> Operation?> = mapOf(
            "Karta" to { x -> ExpenseParser.parse(x) },
            "Zachislen" to { x -> null },
            "Balans vashey karty" to { x -> IncomeSmsParser.parse(x) }
    )

    /**
     * Sample: Balans vashey karty *0002 popolnilsya 01/01/1999 na 1000.00 RUR. Dosstupny ostatok: 1000.00 RUR. Raiffesinbank
     */


    override fun parse(input: String): Operation? {
        val find = prefixMap.keys.find({ input.startsWith(it, false) })
        if (find != null) {
            return prefixMap.get(find)?.invoke(input)
        }
        return null
    }

}