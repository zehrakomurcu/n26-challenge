package com.n26.zehraerguven

import com.n26.zehraerguven.config.TimeIntervalConfig
import com.n26.zehraerguven.dto.TransactionRequest
import com.n26.zehraerguven.exception.FutureTransactionException
import com.n26.zehraerguven.exception.OldTransactionException
import com.n26.zehraerguven.service.TransactionsService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.Instant
import java.util.*

@ExtendWith(MockitoExtension::class)
class TransactionsServiceUnitTest {

    private lateinit var transactionsService: TransactionsService
    private val config: TimeIntervalConfig = TimeIntervalConfig()

    @BeforeEach
    fun setup() {
        config.parameter = "60"
        transactionsService = TransactionsService(config)
    }

    @Test
    fun `save transaction should success when data is valid`() {
        //given
        val transactionRequest = TransactionRequest(Date.from(Instant.now() - Duration.ofSeconds(5)), 21.45.toBigDecimal())
        transactionsService.saveTransaction(transactionRequest)
    }

    @Test
    fun `save transaction should fail when data is old`() {
        //given
        val transactionsRequest = TransactionRequest(Date.from(Instant.now() - Duration.ofMinutes(5)), 21.2355.toBigDecimal())

        //when
        val result = assertThrows<OldTransactionException> { transactionsService.saveTransaction(transactionsRequest) }

        //then
        org.assertj.core.api.Assertions.assertThat(result.javaClass).isEqualTo(OldTransactionException::class.java)
    }

    @Test
    fun `save transaction should fail when data is unprocessable`() {
        //given
        val transactionRequest = TransactionRequest(Date.from(Instant.now() + Duration.ofMinutes(5)), 21.45.toBigDecimal())

        //when
        val result = assertThrows<FutureTransactionException> { transactionsService.saveTransaction(transactionRequest) }

        //then
        org.assertj.core.api.Assertions.assertThat(result.javaClass).isEqualTo(FutureTransactionException::class.java)
    }




}