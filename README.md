# Project Description

This project is implemented for **N26** as an assignment project. It's a Spring Boot project developed by using Kotlin as language.

## Problem

The main use case for the API is to calculate realtime statistics for the last 60 seconds of transactions.
The API needs the following endpoints:

* POST /transactions​ – called every time a transaction is made.
* GET /statistics​ – returns the statistic based of the transactions of the last 60
seconds.
* DELETE /transactions​ – deletes all transactions.

## Solution

State of the transactions has been stored in memory since any database solution was forbidden. This requires to make sure 
that the application is safe for multi-threads. In order to achieve that Spring Boot's @Synchronized annotation is used. 
It guarantees the same object can have only one thread will be executing at the same time.


## Build & Run 

This application uses Maven build tool as it's required. You can use either java or maven commands to run locally.

**Please make sure you give the server port property before you run.**
```bash
java -jar target/zehraerguven-0.0.1-SNAPSHOT.jar --server.port=8080
```
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080"
```

## Deployment

This application is deployed to Heroku. You can reach each API by the following endpoint:
```bash
https://n26-challenge.herokuapp.com/
```

## REST APIs

You can find the postman collection file in the root directory to try out the following API calls.

```bash
N26.postman_collection.json
```

### Create Transaction

POST /transactions

```json
{
"amount": "12.3343",
"timestamp": "2018-07-17T09:59:51.312Z"
}
```

### Get Statistics

GET /statistics

```json
{
  "sum": "1000.00",
  "avg": "100.53", 
  "max": "200000.49", 
  "min": "50.23", 
  "count": 10
}
```

### Delete Transactions

DELETE /transactions


