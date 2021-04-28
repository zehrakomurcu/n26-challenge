package com.n26.zehraerguven.dto

import java.math.BigDecimal

class StatisticsDto(
    var sum: BigDecimal? = BigDecimal.ZERO,
    var avg: BigDecimal? = BigDecimal.ZERO,
    var max: BigDecimal? = BigDecimal.ZERO,
    var min: BigDecimal? = BigDecimal.ZERO,
    var count: Long? = 0
)

