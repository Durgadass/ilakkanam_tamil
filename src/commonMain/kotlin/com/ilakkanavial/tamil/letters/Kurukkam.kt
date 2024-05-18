package com.ilakkanavial.tamil.letters

import com.ilakkanavial.core.parser.*

private val ukaraThunai    = 'ு'
private val aikaraThunai   = 'ை'
private val aukaraThunai   = 'ௌ'
private val au             = 'ஔ'
private val valiUkarams    = valiMeis.map(::addUkaram)

fun mudhal(): Parser<String> = seq(
    char(),
    eos<String>()
).asStringParser()

/**
 * ஐகாரம் மொழிக்கு முதல், இடை மற்றும் கடையில் ஒரு மாத்திரையாக குறைந்து ஒலிக்கும்.
 */
fun aikaraKurukkam(): Parser<String> = seq(
    seq(
        mei(),
        charIs(aikaraThunai),
    ),
    oneOf(
        eos(),
        charNotSat { it.isWhitespace() },
    ),
).asStringParser()

/**
 * ஔகாரம் மொழிக்கு முதலில் ஒரு மாத்திரையாக குறைந்து ஒலிக்கும்.
 */
fun aukaraKurukkam(): Parser<String> = oneOf(
    charIs(au).asStringParser(),
    seq(
        charIn(meis),
        charIs(aukaraThunai),
    ),
).asStringParser()

fun kuttrialukaram(): Parser<String> = TODO()
fun kuttrialikaram(): Parser<String> = TODO()
fun makaraKurukkam(): Parser<String> = TODO()
fun ayudhaKurukkam(): Parser<String> = TODO()

fun isAikaraKurukkam(s: String) = aikaraKurukkam().parse(s).isOk()
fun isAukaraKurukkam(s: String) = aukaraKurukkam().parse(s).isOk()
fun isKuttrialukaram(s: String) = kuttrialukaram().parse(s).isOk()
fun isKuttrialikaram(s: String) = kuttrialikaram().parse(s).isOk()
fun isMakaraKurukkam(s: String) = makaraKurukkam().parse(s).isOk()
fun isAyudhaKurukkam(s: String) = ayudhaKurukkam().parse(s).isOk()

fun nodi(s: String) = when {
    isKuttrialukaram(s)  -> Nodi.ARAI
    isKuttrialikaram(s)  -> Nodi.ARAI
    isMakaraKurukkam(s)  -> Nodi.ARAI
    isAyudhaKurukkam(s)  -> Nodi.ARAI
    isAikaraKurukkam(s)  -> Nodi.ONDRU
    isAukaraKurukkam(s)  -> Nodi.ONDRU
    isKuril(s)           -> Nodi.ONDRU
    isNedil(s)           -> Nodi.IRANDU
    else                 -> null
}


private fun addUkaram(c: Char) = "$c$ukaraThunai"