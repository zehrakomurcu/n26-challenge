package com.n26.zehraerguven.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("time-interval-in-seconds")
class TimeIntervalConfig {
     lateinit var parameter : String
}