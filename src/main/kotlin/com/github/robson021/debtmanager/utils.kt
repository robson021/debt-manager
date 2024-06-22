package com.github.robson021.debtmanager


import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun <R : Any> R.logger(): Lazy<Logger> = lazy { LoggerFactory.getLogger(this::class.java.name) }
