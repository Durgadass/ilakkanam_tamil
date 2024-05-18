package com.ilakkanavial.core.parser

import kotlinx.io.Source

class ParseException(message: String, cause: Throwable?): RuntimeException(message, cause)

sealed class ParseResult<out T> {
    class Ok<T>(val result: List<T>, val rest: Source): ParseResult<T>() {
        constructor(result: T, rest: Source): this(listOf(result), rest)
    }
    class Er(val message: String, val cause: Throwable? = null, val rest: Source): ParseResult<Nothing>()
}

fun <T> ParseResult<T>.result() : List<T> = getOrThrow().first

fun <T> ParseResult<T>.resultOrDefault(default: T) : List<T> = getOrDefault(default).first

fun <T> ParseResult<T>.rest() : Source = getOrNull().second

fun <T> ParseResult<T>.getOrNull() : Pair<List<T>, Source> = when (this) {
    is ParseResult.Ok -> result to rest
    is ParseResult.Er -> emptyList<T>() to rest
}

fun <T> ParseResult<T>.getOrDefault(default: T) : Pair<List<T>, Source> = when (this) {
    is ParseResult.Ok -> result to rest
    is ParseResult.Er -> listOf(default) to rest
}

fun <T> ParseResult<T>.getOrThrow() : Pair<List<T>, Source> = when (this) {
    is ParseResult.Ok -> result to rest
    is ParseResult.Er -> throw ParseException(message, cause)
}

fun <T, U> ParseResult<T>.map(transform: (value: T) -> U) : ParseResult<U> = when(this) {
    is ParseResult.Ok -> ParseResult.Ok(result.map(transform), rest)
    is ParseResult.Er -> this
}

fun <T, U> ParseResult<T>.fold(onOk: (value: List<T>) -> U, onEr: (er: ParseResult.Er) -> U) : U = when(this) {
    is ParseResult.Ok -> onOk(result)
    is ParseResult.Er -> onEr(this)
}

fun <T> ParseResult<T>.onEr(onEr: (er: ParseResult.Er) -> ParseResult<T>) : ParseResult<T> = when(this) {
    is ParseResult.Ok -> this
    is ParseResult.Er -> onEr(this)
}

fun <T> ParseResult<T>.isOk() = this is ParseResult.Ok
fun <T> ParseResult<T>.isEr() = this is ParseResult.Er