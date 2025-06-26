package com.weberbox.pifire.core.annotations

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION])
@Retention(AnnotationRetention.RUNTIME)
annotation class Compatibility(
    val versionBelow: String,
    val build: String,
    val message: String = ""
)