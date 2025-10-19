package com.first.devswipe

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DevSwipeBackendApplication

fun main(args: Array<String>) {
    runApplication<DevSwipeBackendApplication>(*args)
}