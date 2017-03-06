package com.github.ymkawb.moneyback.sms.parse

import com.github.ymkawb.moneyback.model.Operation
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

/**
 * Created by nivanov on 03/03/17.
 */
abstract class BankSmsParser {
    abstract fun parse(input: String): List<Operation>

    protected fun floatToBigInteger(input:String) : BigInteger {
        var f = input.trim();
        val indexOfSeparator = input.indexOf('.')
        if(indexOfSeparator != -1){
            f = f.removeRange(indexOfSeparator,indexOfSeparator + 1)
        }
        return BigInteger(f)
    }
}

class RaiffaisenSmsParser : BankSmsParser() {

    data class CardOperation(val cardNumber : String? = null,
                             val operation : String? = null,
                             val amount: BigInteger? = null,
                             val date : String? = null,
                             val accountBalance : BigInteger? = null) : Operation()

    private val tokenParseList : List<(CardOperation,String) -> CardOperation> = listOf(
            {x,y -> cardNumber(x,y)},
            {x,y -> operation(x,y)},
            {x,y -> amount(x,y)},
            {x,y -> date(x,y)},
            {x,y -> accountBalance(x,y)}
    );

    private fun cardNumber(op:CardOperation,token : String) : CardOperation {
        val split: List<String> = token.split(' ')
        if(split.size == 2){
            return op.copy(cardNumber = split[1])
        }
        return op
    }

    private fun operation(op :CardOperation, token : String ) : CardOperation {
       return op.copy(operation = token.trim())
    }

    private fun amount(op : CardOperation, token : String) : CardOperation {
        return op.copy(amount = floatToBigInteger(token.trim().split(' ')[0]))
    }

    private fun date(op: CardOperation, token:String) : CardOperation {
        return op.copy(date = token.trim().split(' ')[1])
    }



    private fun accountBalance(op:CardOperation, token:String) : CardOperation {
        val split = token.trim().split(' ')
        return op.copy(accountBalance = floatToBigInteger(split[2]))
    }

    fun parseCardOperation(input: String): List<Operation> {
        return listOf(
                StringTokenizer(input,";").toList()
                        .map(Any::toString)
                        .zip(tokenParseList)
                        .fold(CardOperation(), { t, f -> f.second(t, f.first) })
        )
    }

    fun parseTransaction(input: String): List<Operation> {
        return emptyList()
    }

    val prefixMap: Map<String, (String) -> List<Operation>> = mapOf(
            "Karta" to { x -> parseCardOperation(x) },
            "Zachislen" to { x -> parseTransaction(x) }
    )

    override fun parse(input: String): List<Operation> {
        val find = prefixMap.keys.find({ input.startsWith(it, false) })
        if (find != null) {
            return prefixMap.get(find)?.invoke(input) ?: emptyList()
        }
        return emptyList()
    }

}