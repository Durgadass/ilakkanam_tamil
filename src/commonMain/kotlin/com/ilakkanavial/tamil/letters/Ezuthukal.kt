package com.ilakkanavial.tamil.letters

import com.ilakkanavial.core.parser.*

val ayudham        = 'ஃ'
val pulli          = '்'
val uyirKurils     = setOf('அ', 'இ', 'உ', 'எ', 'ஒ')
val uyirNedils     = setOf('ஆ', 'ஈ', 'ஊ', 'ஏ', 'ஐ', 'ஓ', 'ஔ')
val uyirs          = uyirKurils + uyirNedils + ayudham
val valiMeis       = setOf('க', 'ச', 'ட', 'த', 'ப', 'ற')
val meliMeis       = setOf('ங', 'ஞ', 'ண', 'ந', 'ம', 'ன')
val idaiMeis       = setOf('ய', 'ர', 'ல', 'வ', 'ழ', 'ள')
val vadaMeis       = setOf('ஶ', 'ஜ', 'ஷ', 'ஸ', 'ஹ')
val tamilMeis      = valiMeis + meliMeis + idaiMeis
val meis           = valiMeis + meliMeis + idaiMeis + vadaMeis
val kurilThunais   = setOf('ி', 'ு', 'ெ', 'ொ')
val nedilThunais   = setOf('ா', 'ீ', 'ூ', 'ே', 'ை', 'ோ', 'ௌ')
val thunais        = kurilThunais + nedilThunais
val uyirInams      = mapOf('ஆ' to 'அ', 'ஈ' to 'இ', 'ஊ' to 'உ', 'ஏ' to 'எ', 'ஓ' to 'ஒ', 'ஐ' to 'இ', 'ஔ' to 'உ')
val meiInams       = mapOf('க' to 'ங', 'ச' to 'ஞ', 'ட' to 'ண', 'த' to 'ந', 'ப' to 'ம', 'ற' to 'ன')

// Predicates for Uyir
fun isAyudham(s: String)   = s[0] == ayudham
fun isUyirKuril(s: String) = s[0] in uyirKurils
fun isUyirNedil(s: String) = s[0] in uyirNedils
fun isUyir(s: String)      = s[0] in uyirs

// Parsers for Uyir
fun uyirKuril(): Parser<Char> = charIn(uyirKurils)
fun uyirNedil(): Parser<Char> = charIn(uyirNedils)
fun uyir(): Parser<Char>      = charIn(uyirs)

// Predicates for Mei
fun isValiMei(s: String)  = s[0] in valiMeis && s[1] == pulli
fun isMeliMei(s: String)  = s[0] in meliMeis && s[1] == pulli
fun isIdaiMei(s: String)  = s[0] in idaiMeis && s[1] == pulli
fun isVadaMei(s: String)  = s[0] in vadaMeis && s[1] == pulli
fun isTamilMei(s: String) = s[0] in tamilMeis && s[1] == pulli
fun isMei(s: String)      = s[0] in meis && s[1] == pulli

// Parsers for Mei
fun valiMei(): Parser<String>  = meiThenPulli(valiMeis)
fun meliMei(): Parser<String>  = meiThenPulli(meliMeis)
fun idaiMei(): Parser<String>  = meiThenPulli(idaiMeis)
fun vadaMei(): Parser<String>  = meiThenPulli(vadaMeis)
fun tamilMei(): Parser<String> = meiThenPulli(tamilMeis)
fun mei(): Parser<String>      = meiThenPulli(meis)
private inline fun meiThenPulli(cs: Collection<Char>) = charPairSat({ it in cs }, { it == pulli }).asStringParser()

// Predicates for Uyirmei
fun isValiUyirmei(s: String)  = s[0] in valiMeis
fun isMeliUyirmei(s: String)  = s[0] in meliMeis
fun isIdaiUyirmei(s: String)  = s[0] in idaiMeis
fun isVadaUyirmei(s: String)  = s[0] in vadaMeis
fun isTamilUyirmei(s: String) = s[0] in tamilMeis
fun isUyirmei(s: String)      = s[0] in meis

// Parsers for Uyirmei
fun valiUyirmei(): Parser<String>  = meiThenOtionalThunai(valiMeis)
fun meliUyirmei(): Parser<String>  = meiThenOtionalThunai(meliMeis)
fun idaiUyirmei(): Parser<String>  = meiThenOtionalThunai(idaiMeis)
fun vadaUyirmei(): Parser<String>  = meiThenOtionalThunai(vadaMeis)
fun tamilUyirmei(): Parser<String> = meiThenOtionalThunai(tamilMeis)
fun uyirmei(): Parser<String>      = meiThenOtionalThunai(meis)
private inline fun meiThenOtionalThunai(cs: Collection<Char>) = seq(charIn(cs), charIn(thunais).optional()).asStringParser()

fun isOttruThunai(s: String) = s[0] == pulli
fun isKurilThunai(s: String) = s[0] in kurilThunais
fun isNedilThunai(s: String) = s[0] in nedilThunais
fun isThunai(s: String)      = s[0] in thunais

// Predicates for Kuril and Nedil
fun isMeiKuril(s: String) = s.thunaiIs(::isKurilThunai, default = true)
fun isMeiNedil(s: String) = s.thunaiIs(::isNedilThunai, default = false)
fun isKuril(s: String)    = isUyirKuril(s.substring(0, 1)) || isMeiKuril(s)
fun isNedil(s: String)    = isUyirNedil(s.substring(0, 1)) || isMeiNedil(s)

// Parsers for Kuril and Nedil
fun tamilMeiKuril(): Parser<String> = meiThenKurilThunai(tamilMeis)
fun vadaMeiKuril(): Parser<String>  = meiThenKurilThunai(vadaMeis)
fun meiKuril(): Parser<String>      = meiThenKurilThunai(meis)
fun kuril(): Parser<String>         = oneOf(uyirKuril().asStringParser(), meiKuril())
fun tamilMeiNedil(): Parser<String> = meiThenNedilThunai(tamilMeis)
fun vadaMeiNedil(): Parser<String>  = meiThenNedilThunai(vadaMeis)
fun meiNedil(): Parser<String>      = meiThenNedilThunai(meis)
fun nedil(): Parser<String>         = oneOf(uyirNedil().asStringParser(), meiNedil())
private inline fun meiThenKurilThunai(cs: Collection<Char>) = seq(charIn(cs), charIn(kurilThunais).optional()).asStringParser()
private inline fun meiThenNedilThunai(cs: Collection<Char>) = seq(charIn(cs), charIn(nedilThunais)).asStringParser()

// Predicate for Tamil letter
fun isTamilLetter(s: String) = isUyir(s) || isTamilMei(s)

// Parser for Tamil letter
fun tamilLetter(): Parser<String> = oneOf(uyir().asStringParser(), tamilMei())


private fun String.thunai() = getOrNull(1)?.toString()
private fun String.thunaiIs(p: (String) -> Boolean, default: Boolean = false) = thunai()?.let(p) ?: default