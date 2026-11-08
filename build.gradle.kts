import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}

val ktlintVersion = libs.versions.ktlint.get()
val detektConfigFile = rootProject.layout.projectDirectory.file("config/detekt/detekt.yml")

fun configureQualityPlugins(target: Project) {
    target.pluginManager.apply("org.jlleitschuh.gradle.ktlint")
    target.pluginManager.apply("io.gitlab.arturbosch.detekt")

    target.extensions.configure<KtlintExtension> {
        version.set(ktlintVersion)
        android.set(true)
        ignoreFailures.set(false)
    }

    target.extensions.configure<DetektExtension> {
        buildUponDefaultConfig = true
        parallel = true
        config.setFrom(detektConfigFile)
    }

    target.tasks.withType<Detekt>().configureEach {
        jvmTarget = "17"
        reports {
            html.required.set(true)
            xml.required.set(true)
            sarif.required.set(false)
            txt.required.set(false)
        }
    }
}

subprojects {
    val target = this
    pluginManager.withPlugin("org.jetbrains.kotlin.android") {
        configureQualityPlugins(target)
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        configureQualityPlugins(target)
    }
    tasks.withType<Test>().configureEach {
        testLogging {
            events(TestLogEvent.FAILED)
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
}
