package com.ilakkanavial.core

object Extensions {
    fun <T> T.allAre(vararg p: (T) -> Boolean) = p.all { it(this) }
    fun <T> T.anyIs(vararg p: (T) -> Boolean) = p.any { it(this) }
}