package com.github.ymkawb.moneyback

import com.github.ymkawb.moneyback.model.Operation
import com.github.ymkawb.moneyback.sms.parse.RaiffaisenSmsParser
import org.junit.Test
import org.amshove.kluent.*;

/**
 * Created by nivanov on 03/03/17.
 */

object RaifSmsValues {
    val BUY_SMS = "Karta *0001; Pokupka FFFFF; 1000.00 RUR; Data: 01/01/1999; Dostupny Ostatok 100.01 RUR. Raiffesinbank"
}
class SmsParseTest {
    @Test
    fun parseBuySms() {
        val parser = RaiffaisenSmsParser()
        val parse : List<Operation> = parser.parse(RaifSmsValues.BUY_SMS);
        parse shouldNotBe null
        parse.size shouldBe  1
    }
}