package com.n26.zehraerguven.controller

import com.n26.zehraerguven.dto.StatisticsDto
import com.n26.zehraerguven.dto.TransactionRequest
import com.n26.zehraerguven.service.TransactionsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class TransactionsController(private val transactionsService: TransactionsService) {

    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveTransaction(@RequestBody transaction: TransactionRequest) {
        transactionsService.saveTransaction(transaction)
    }

    @GetMapping("/statistics")
    @ResponseStatus(HttpStatus.OK)
    fun getStatistics() : StatisticsDto {
        return transactionsService.getStatistics()
    }

    @DeleteMapping("/transactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAllTransations() {
        return transactionsService.deleteAllTransactions()
    }





}