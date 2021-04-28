package com.n26.zehraerguven.service

import com.n26.zehraerguven.config.TimeIntervalConfig
import com.n26.zehraerguven.dto.StatisticsDto
import com.n26.zehraerguven.dto.TransactionRequest
import com.n26.zehraerguven.exception.FutureTransactionException
import com.n26.zehraerguven.exception.OldTransactionException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class TransactionsService(private val config: TimeIntervalConfig) {

    private var transactions: MutableMap<Date, BigDecimal> = mutableMapOf()
    private var statistics: StatisticsDto = StatisticsDto()

    @Synchronized()
    fun saveTransaction(transaction: TransactionRequest)  {
        if (isTransactionUnprocessable(transaction.timestamp.toInstant())) {
            throw FutureTransactionException()
        }

        if (!isTransactionValid(transaction.timestamp.toInstant())) {
            throw OldTransactionException()
        }

        //save transactions to memory if valid
        transactions.put(transaction.timestamp, transaction.amount)
    }

    fun getStatistics() : StatisticsDto {
        calculateStatistics()
        return statistics
    }

    fun deleteAllTransactions() {
        transactions = mutableMapOf()
        statistics = StatisticsDto()
    }

    @Synchronized()
    fun calculateStatistics() {
        val latestTransactionAmounts = transactions
            .filter { (key) -> isTransactionValid(key.toInstant()) }
            .values.toList()

        if (latestTransactionAmounts.isNotEmpty()) {
            statistics.sum = latestTransactionAmounts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP)
            statistics.avg = statistics.sum?.divide(BigDecimal(latestTransactionAmounts.size), RoundingMode.HALF_UP)
            statistics.max = latestTransactionAmounts.maxOrNull()?.setScale(2, RoundingMode.HALF_UP)
            statistics.min = latestTransactionAmounts.minOrNull()?.setScale(2, RoundingMode.HALF_UP)
            statistics.count = latestTransactionAmounts.count().toLong()
        }
    }

    private fun isTransactionValid(timestamp : Instant) : Boolean {
        return timestamp.isBefore(Instant.now())
                && timestamp.isAfter(Instant.now() - Duration.ofSeconds(config.parameter.toLong()))
    }

    private fun isTransactionUnprocessable(timestamp: Instant) : Boolean {
        return timestamp.isAfter(Instant.now())
    }
}