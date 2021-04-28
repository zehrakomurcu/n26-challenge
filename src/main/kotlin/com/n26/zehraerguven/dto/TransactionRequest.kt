package com.n26.zehraerguven.dto

import java.math.BigDecimal
import java.util.*

data class TransactionRequest(
    var timestamp: Date,
    var amount: BigDecimal
    )
