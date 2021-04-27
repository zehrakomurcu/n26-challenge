package com.n26.zehraerguven.service

import com.n26.zehraerguven.config.TimeIntervalConfig
import com.n26.zehraerguven.dto.StatisticsDto
import com.n26.zehraerguven.dto.TransactionRequest
import com.n26.zehraerguven.exception.FutureTransactionException
import com.n26.zehraerguven.exception.OldTransactionException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.properties.Delegates

@Service
class TransactionsService(private val config: TimeIntervalConfig) {

    private var transactions: MutableMap<Date, Double> = mutableMapOf()
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
            .filter { (k,v) -> isTransactionValid(k.toInstant()) }
            .values.toList()

        statistics.count = latestTransactionAmounts.count().toLong()
        statistics.sum = latestTransactionAmounts.sum()
        statistics.max = latestTransactionAmounts.maxOrNull()
        statistics.min = latestTransactionAmounts.minOrNull()
        statistics.avg = latestTransactionAmounts.average()
    }

    private fun isTransactionValid(timestamp : Instant) : Boolean {
        return timestamp.isBefore(Instant.now())
                && timestamp.isAfter(Instant.now() - Duration.ofSeconds(config.parameter.toLong()))
    }

    private fun isTransactionUnprocessable(timestamp: Instant) : Boolean {
        return timestamp.isAfter(Instant.now())
    }

}