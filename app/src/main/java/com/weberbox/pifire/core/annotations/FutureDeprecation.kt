package com.weberbox.pifire.core.annotations

@Target(
    allowedTargets = [
        AnnotationTarget.CLASS,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.LOCAL_VARIABLE
    ]
)
@Retention(AnnotationRetention.RUNTIME)
annotation class FutureDeprecation(
    val comments: String,
    val action: String
)