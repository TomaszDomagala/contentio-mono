package dev.thhs.contentiospring.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * code by https://amarszalek.net/blog/2018/05/13/logging-in-kotlin-right-approach/
 */
fun <R : Any> R.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(this.javaClass) }
}