package com.github.ymkawb.moneyback.sms.parse

import com.github.ymkawb.moneyback.model.Operation

/**
 * Created by nivanov on 03/03/17.
 */
abstract class BankSmsParser {
    abstract fun parse(input : String) : List<Operation>
}

class RaiffaisenSmsParser : BankSmsParser() {
    override fun parse(input: String): List<Operation> {
        return emptyList();
    }

}