package com.ilakkanavial.core.parser

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.writeString

typealias Parser<A> = (Source) -> ParseResult<A>
typealias PairParser<A, B> = (Source) -> ParseResult<Pair<A, B>>
typealias CharParser = Parser<Char>
typealias CharPairParser = PairParser<Char, Char>
typealias StringParser = Parser<String>
typealias Predicate<A> = (a: A) -> Boolean
typealias CharPredicate = Predicate<Char>

// Primitive parsers --------------------

fun <A> ok(def: List<A>): Parser<A> = { src -> ParseResult.Ok(def, src) }
fun <A> ok(def: A): Parser<A> = { src -> ParseResult.Ok(def, src) }
fun <A> er(msg: String): Parser<A> = { src -> ParseResult.Er(msg, null, src) }
fun char(): CharParser = { src ->
    if (src.request(2)) {
        ParseResult.Ok(src.readShort().toInt().toChar(), src)
    } else {
        ParseResult.Er("Char is 2 bytes, but source ended before 2 bytes", null, src)
    }
}

fun <A> eos(): Parser<A> = { src ->
    if (src.exhausted()) {
        ParseResult.Ok(emptyList(), src)
    } else {
        ParseResult.Er("End of source expected", null, src)
    }
}

fun <A> Parser<A>.parse(src: Source): ParseResult<A> = invoke(src)
fun <A> Parser<A>.parse(src: String): ParseResult<A> = invoke(Buffer().apply { writeString(src) })

// Primitive parsers end ----------------

// Parser combinators -------------------

infix fun <A, B> Parser<A>.bind(f: (a: A) -> Parser<B>): Parser<B> = { src ->
    invoke(src).fold(
        onOk = { a -> f(a[0])(src) },
        onEr = { it }
    )
}

fun <A, B> seq(pa: Parser<A>, pb: Parser<B>): PairParser<A, B> =
    pa bind { a -> pb bind { b -> ok(a to b) } }

fun <A> or(pa: Parser<A>, pb: Parser<A>): Parser<A> =
    pa bind { a -> pb bind { b -> ok(listOf(a, b)) } }

fun <A> any(pa: Parser<A>, pb: Parser<A>): Parser<A> =
    { src -> pa(src).onEr { pb(src) } }

fun <A, B> Parser<A>.map(transform: (a: A) -> B): Parser<B> =
    bind { ok(transform(it)) }

fun <A> many1(p: Parser<A>): Parser<List<A>> =
    p bind { a -> many(p) bind { ok(listOf(listOf(a) + it)) } }

fun <A> many(p: Parser<A>): Parser<List<A>> =
    any(many1(p), ok(emptyList()))

fun <A> Parser<A>.optional(): Parser<A> =
    { src -> this(src).onEr { ParseResult.Ok(emptyList(), src) } }

fun <A> oneOf(vararg ps: Parser<A>): Parser<A> =
    ps[0] bind { a -> oneOf(*ps.sliceArray(1..ps.size)) bind { ok(listOf(a) + it) } }

fun string(s: String): Parser<String> =
    charIs(s[0]) bind { a -> string(s.substring(1)) bind { ok("$a$it") } }

fun space(): Parser<String> =
    many(charSat { it.isWhitespace() }).map { it.joinToString() }

fun <A> Parser<A>.token(): Parser<A> =
    bind { a -> space() bind { ok(a) } }

fun token(s: String): Parser<String> =
    string(s).token()

fun charPair(): CharPairParser =
    seq(char(), char())

fun charNotSat(p: CharPredicate): CharParser =
    charSat { !p(it) }

fun charSat(p: CharPredicate): CharParser =
    char() bind { c -> if (p(c)) ok(c) else er("Satisfies parser failed for char $c") }

fun charIs(c: Char): CharParser =
    charSat { it == c }

fun charIn(c: Collection<Char>): CharParser =
    charSat { it in c }

fun charPairSat(p1: CharPredicate, p2: CharPredicate): CharPairParser =
    seq(charSat(p1), charSat(p2))

fun charPairIs(c1: Char, c2: Char): CharPairParser =
    seq(charIs(c1), charIs(c2))

fun charPairIn(c1: Collection<Char>, c2: Collection<Char>): CharPairParser =
    seq(charIn(c1), charIn(c2))

fun <A> Parser<A>.asStringParser(): StringParser =
    map { "$it" }

fun <A, B> PairParser<A, B>.asStringParser(): StringParser =
    map { "${it.first}${it.second}" }

fun CharPairParser.asStringParser(): StringParser =
    map { "${it.first}${it.second}" }

// Parser combinators end ---------------

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val s = "அடிப்படை தமிழ் சொற்றொடர்கள் \uD807\uDFC0"
    s.forEach {
        print(it)
        println(it.code.toHexString())
    }
}


