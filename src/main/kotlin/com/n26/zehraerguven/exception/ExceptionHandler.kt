package com.n26.zehraerguven.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(OldTransactionException::class)
    fun handleOldTransactions(exception: OldTransactionException?): ResponseEntity<Any?>? {
        return ResponseEntity("Invalid Transaction", HttpStatus.NO_CONTENT)
    }

    @ExceptionHandler(FutureTransactionException::class)
    fun handleFutureTransactions(exception: FutureTransactionException?): ResponseEntity<Any?>? {
        return ResponseEntity("Unprocessable Transaction", HttpStatus.UNPROCESSABLE_ENTITY)
    }
}