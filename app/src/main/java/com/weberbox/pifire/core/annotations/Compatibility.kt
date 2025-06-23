package com.weberbox.pifire.core.annotations

@Suppress("unused")
@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION])
@Retention(AnnotationRetention.RUNTIME)
annotation class Compatibility(val message: String)