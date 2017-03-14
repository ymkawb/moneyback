package com.github.ymkawb.moneyback.sms.parse

import com.github.ymkawb.moneyback.model.Operation
import java.math.BigInteger

data class CardOperation(val cardNumber: String? = null,
                         val operation: String? = null,
                         val amount: BigInteger? = null,
                         val date: String? = null,
                         val accountBalance: BigInteger? = null) : Operation()