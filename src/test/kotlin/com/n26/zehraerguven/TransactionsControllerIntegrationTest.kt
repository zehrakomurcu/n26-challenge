package com.n26.zehraerguven

import com.n26.zehraerguven.dto.StatisticsDto
import com.n26.zehraerguven.dto.TransactionRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionsControllerIntegrationTest {
    @LocalServerPort
    private val port = 8090
    private var baseUrl: String? = null

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @BeforeEach
    fun setUp() {
        baseUrl = String.format("http://localhost:%d/", port)
    }

    @Test
    fun  `post transaction should return CREATED when transaction is successful` () {
        //given
        val transaction = TransactionRequest(Date.from(Instant.now() - Duration.ofSeconds(5)), 21.45)
        val uri = URI("$baseUrl/transactions")

        //when
        var result = restTemplate.postForEntity(uri,
            HttpEntity<TransactionRequest>(transaction,
                HttpHeaders()), String::class.java
        )

        //then
        assertThat(result.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun `post transaction should return NO_CONTENT when timestamp is old` () {
        //given
        val transaction = TransactionRequest(Date.from(Instant.now() - Duration.ofMinutes(5)), 13.2355)
        val postURI = URI("$baseUrl/transactions")

        //when
        var result = restTemplate.postForEntity(postURI,
            HttpEntity<TransactionRequest>(transaction, HttpHeaders()),
            TransactionRequest::class.java
        )

        //then
        assertThat(result.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `post transaction should return UNPROCESSABLE_ENTITY when timestamp is for future` () {
        //given
        val transaction = TransactionRequest(Date.from(Instant.now() + Duration.ofMinutes(5)), 18.65)
        val postURI = URI("$baseUrl/transactions")

        //when
        var result = restTemplate.postForEntity(postURI,
            HttpEntity<TransactionRequest>(transaction, HttpHeaders()),
            String::class.java
        )

        //then
        assertThat(result.statusCode).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
    }

    @Test
    fun  `get statistics should success` () {
        //given
        val transaction1 = TransactionRequest(Date.from(Instant.now() - Duration.ofSeconds(5)), 18.65)
        val transaction2 = TransactionRequest(Date.from(Instant.now() - Duration.ofSeconds(10)), 12.35)
        val postURI = URI("$baseUrl/transactions")

        restTemplate.postForEntity(postURI,
            HttpEntity<TransactionRequest>(transaction1,
                HttpHeaders()), TransactionRequest::class.java
        )
        restTemplate.postForEntity(postURI,
            HttpEntity<TransactionRequest>(transaction2,
                HttpHeaders()), TransactionRequest::class.java
        )

        //when
        val getURI = URI("$baseUrl/statistics")
        var result = restTemplate.exchange(getURI, HttpMethod.GET, HttpEntity<StatisticsDto>(HttpHeaders()), StatisticsDto::class.java)

        //then
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body?.sum).isEqualTo(31.0)
        assertThat(result.body?.avg).isEqualTo(15.5)
        assertThat(result.body?.max).isEqualTo(18.65)
        assertThat(result.body?.min).isEqualTo(12.35)
        assertThat(result.body?.count).isEqualTo(2)
    }

    @Test
    fun  `delete transaction should success` () {
        //given
        val deleteURI = URI("$baseUrl/transactions")

        //when
        var result = restTemplate.exchange(deleteURI, HttpMethod.DELETE, HttpEntity<Any>(HttpHeaders()), Any::class.java)

        //then
        assertThat(result.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
    }

    @Test
    fun `get statistics should no value when all transactions deleted` () {
        //given
        val transaction1 = TransactionRequest(Date.from(Instant.now() - Duration.ofSeconds(5)), 18.65)
        val transaction2 = TransactionRequest(Date.from(Instant.now() - Duration.ofSeconds(10)), 12.35)
        val postURI = URI("$baseUrl/transactions")

        restTemplate.postForEntity(postURI,
            HttpEntity<TransactionRequest>(transaction1,
                HttpHeaders()), TransactionRequest::class.java
        )
        restTemplate.postForEntity(postURI,
            HttpEntity<TransactionRequest>(transaction2,
                HttpHeaders()), TransactionRequest::class.java
        )

        //when
        val deleteURI = URI("$baseUrl/transactions")
        restTemplate.exchange(deleteURI, HttpMethod.DELETE, HttpEntity<Any>(HttpHeaders()), Any::class.java)

        //then
        val getURI = URI("$baseUrl/statistics")
        var result = restTemplate.exchange(getURI, HttpMethod.GET, HttpEntity<StatisticsDto>(HttpHeaders()), StatisticsDto::class.java)

        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(result.body?.count).isEqualTo(0)
    }

}