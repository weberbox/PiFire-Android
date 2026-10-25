package com.weberbox.pifire.core.common.model

fun interface DualItemMapper<in A, in B, out R> {
    fun map(inA: A?, inB: B?): R
}
