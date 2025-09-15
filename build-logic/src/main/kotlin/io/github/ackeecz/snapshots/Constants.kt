package io.github.ackeecz.snapshots

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

object Constants {

    val COMPILE_JVM_TARGET = JvmTarget.JVM_11
    val COMPILE_JAVA_VERSION = JavaVersion.VERSION_11

    const val COMPILE_SDK = 36
    const val MIN_SDK = 23
    const val TARGET_SDK = 36
}
