package com.github.ymkawb.moneyback

import com.github.ymkawb.moneyback.model.Operation
import com.github.ymkawb.moneyback.sms.parse.RaiffaisenSmsParser
import com.github.ymkawb.moneyback.sms.parse.RaiffaisenSmsParser.CardOperation
import org.junit.Test
import org.amshove.kluent.*;
import java.math.BigInteger
import kotlin.test.fail

/**
 * Created by nivanov on 03/03/17.
 */

object RaifSmsValues {
}

class SmsParseTest {
    val BUY_SMS = "Karta *0001; Pokupka FFFFF; 1000.00 RUR; Data: 01/01/1999; Dostupny Ostatok 100.01 RUR. Raiffesinbank"
    @Test
    fun parseBuySms() {
        val parser = RaiffaisenSmsParser()
        val parse: List<Operation> = parser.parse(BUY_SMS);
        parse shouldNotBe null
        parse.size shouldBe 1
        val result = parse[0]
        when (result) {
            is CardOperation -> {
                result.accountBalance shouldEqual  BigInteger("10001")
                result.amount shouldEqual BigInteger("100000")
                result.cardNumber shouldEqual "*0001"
                result.date shouldEqual "01/01/1999"
                result.operation shouldEqual "Pokupka FFFFF"
            }
            else -> {
                fail("Unexpected class")
            }
        }

    }
}