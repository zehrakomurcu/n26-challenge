package com.n26.zehraerguven.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.*

data class TransactionRequest(
    var timestamp: Date,
    var amount: Double
    )
