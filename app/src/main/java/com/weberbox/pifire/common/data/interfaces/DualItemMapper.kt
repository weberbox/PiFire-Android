package com.weberbox.pifire.common.data.interfaces

fun interface DualItemMapper<in A, in B, out R> {
    fun map(inA: A?, inB: B?): R
}