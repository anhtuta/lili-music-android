import java.io.File
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val appVersionCode = envOrVersion("VERSION_CODE").toInt()
val appVersionName = envOrVersion("VERSION_NAME")
val releaseSigning = loadReleaseSigning()

android {
    namespace = "com.anhtuta.lilimusic"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.anhtuta.lilimusic"
        minSdk = 26
        targetSdk = 35
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"https://blog.nenmongvietnam.com/laravel/public\"",
        )
    }

    signingConfigs {
        if (releaseSigning != null) {
            create("release") {
                storeFile = releaseSigning.storeFile
                storePassword = releaseSigning.storePassword
                keyAlias = releaseSigning.keyAlias
                keyPassword = releaseSigning.keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (releaseSigning != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

tasks.matching { it.name == "testReleaseUnitTest" }.configureEach {
    enabled = false
}

tasks.configureEach {
    if (name == "testDebugUnitTest") {
        dependsOn("processDebugMainManifest")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:data"))
    implementation(project(":core:playback"))
    implementation(project(":core:lyrics"))
    implementation(project(":core:ui"))
    implementation(project(":feature:library"))
    implementation(project(":feature:player"))
    implementation(project(":feature:lyrics"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:downloads"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.work.runtime)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.work.testing)
    kspTest(libs.hilt.compiler)
}

fun envOrVersion(name: String): String {
    val fromEnv = System.getenv(name)?.trim().orEmpty()
    if (fromEnv.isNotEmpty()) {
        return fromEnv
    }
    val properties = Properties()
    rootProject.file("version.properties").inputStream().use(properties::load)
    val value = properties.getProperty(name)?.trim().orEmpty()
    require(value.isNotEmpty()) { "Missing $name in version.properties" }
    return value
}

data class ReleaseSigning(
    val storeFile: File,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String,
)

fun loadReleaseSigning(): ReleaseSigning? {
    val fileProps = Properties()
    val propertiesFile = rootProject.file("keystore.properties")
    if (propertiesFile.isFile) {
        propertiesFile.inputStream().use(fileProps::load)
    }

    fun read(
        propKey: String,
        envKey: String,
    ): String? {
        val fromEnv = System.getenv(envKey)?.trim().orEmpty()
        if (fromEnv.isNotEmpty()) {
            return fromEnv
        }
        return fileProps.getProperty(propKey)?.trim()?.takeIf { it.isNotEmpty() }
    }

    val storeFileValue = read("storeFile", "LILI_STORE_FILE")
    val storePassword = read("storePassword", "LILI_STORE_PASSWORD")
    val keyAlias = read("keyAlias", "LILI_KEY_ALIAS")
    val keyPassword = read("keyPassword", "LILI_KEY_PASSWORD")
    val present = listOf(storeFileValue, storePassword, keyAlias, keyPassword).count { it != null }
    if (present == 0) {
        return null
    }
    require(present == 4) {
        "Release signing needs storeFile, storePassword, keyAlias, and keyPassword " +
            "(keystore.properties or LILI_STORE_FILE / LILI_STORE_PASSWORD / " +
            "LILI_KEY_ALIAS / LILI_KEY_PASSWORD)."
    }
    val storeFile = resolveStoreFile(storeFileValue!!)
    return ReleaseSigning(storeFile, storePassword!!, keyAlias!!, keyPassword!!)
}

fun resolveStoreFile(path: String): File {
    val specified = File(path)
    val candidates =
        listOfNotNull(
            specified.takeIf { it.isAbsolute && it.isFile },
            rootProject.file(path).takeIf { it.isFile },
            file(path).takeIf { it.isFile },
        )
    return candidates.firstOrNull() ?: error("Release keystore not found: $path")
}
