package com.github.ymkawb.moneyback

import com.github.ymkawb.moneyback.model.Operation
import com.github.ymkawb.moneyback.sms.parse.RaiffaisenSmsParser
import com.github.ymkawb.moneyback.sms.parse.CardOperation
import org.junit.Test
import org.amshove.kluent.*;
import java.math.BigInteger
import kotlin.test.fail

/**
 * Created by nivanov on 03/03/17.
 */

class SmsParseTest {

    val BUY_SMS = "Karta *0001; Pokupka FFFFF; 1000.00 RUR; Data: 01/01/1999; Dostupny Ostatok 100.01 RUR. Raiffesinbank"

    @Test
    fun parseBuySms() {
        val parse: Operation? = RaiffaisenSmsParser.parse(BUY_SMS);
        parse shouldNotBe null
        when (parse) {
            is CardOperation -> {
                parse.accountBalance shouldEqual BigInteger("10001")
                parse.amount shouldEqual BigInteger("100000")
                parse.cardNumber shouldEqual "*0001"
                parse.date shouldEqual "01/01/1999"
                parse.operation shouldEqual "Pokupka FFFFF"
            }
            else -> {
                fail("Unexpected class")
            }
        }

    }

    val CASH_SMS = "Karta *0001; Snyatie nalichnih ADRESS ; 1000.00 RUR; Data 01/01/1999; Dostupny Ostatok 100.01 RUR. Raiffesinbank"

    @Test
    fun parseCacheSms(){
        val parse: Operation? = RaiffaisenSmsParser.parse(CASH_SMS);
        parse shouldNotBe null
        when (parse) {
            is CardOperation -> {
                parse.accountBalance shouldEqual BigInteger("10001")
                parse.amount shouldEqual BigInteger("100000")
                parse.cardNumber shouldEqual "*0001"
                parse.date shouldEqual "01/01/1999"
                parse.operation shouldEqual "Snyatie nalichnih ADRESS"
            }
            else -> {
                fail("Unexpected class")
            }
        }

    }
    val INCOME_SMS = "Balans vashey karty *0001 popolnilsya 01/01/1999 na 1000.00 RUR. Dosstupny ostatok: 1000.00 RUR. Raiffesinbank"

    @Test
    fun parseCashSms() {
        val parse: Operation? = RaiffaisenSmsParser.parse(INCOME_SMS)
        parse shouldNotBe null
        when (parse) {
            is CardOperation -> {
                parse.accountBalance shouldEqual BigInteger("100000")
                parse.amount shouldEqual BigInteger("100000")
                parse.cardNumber shouldEqual "*0001"
                parse.date shouldEqual "01/01/1999"
                parse.operation shouldEqual null
            }
            else -> {
                fail("Unexpected class")
            }
        }
    }
}