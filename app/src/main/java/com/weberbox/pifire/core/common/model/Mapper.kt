package com.weberbox.pifire.core.common.model

fun interface Mapper<in From, out To> {
    fun map(from: From): To
}
