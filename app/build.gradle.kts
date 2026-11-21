import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val tdlibProperties = Properties().apply {
    val configured = rootProject.file("config/tdlib.properties")
    val fallback = rootProject.file("config/tdlib.properties.example")
    val source = if (configured.exists()) configured else fallback
    source.inputStream().use(::load)
    if (!configured.exists()) {
        logger.warn("OCSIGRAM: config/tdlib.properties не найден; используются ЗАГЛУШКИ из .example.")
    }
}

val signingProperties = Properties()
val signingFile = rootProject.file("config/signing.properties")
if (signingFile.exists()) {
    signingFile.inputStream().use(signingProperties::load)
}

fun signingValue(name: String): String? =
    signingProperties.getProperty(name)
        ?: providers.environmentVariable(name.envName()).orNull

fun String.envName(): String = when (this) {
    "storeFile" -> "OCSIGRAM_STORE_FILE"
    "storePassword" -> "OCSIGRAM_STORE_PASSWORD"
    "keyAlias" -> "OCSIGRAM_KEY_ALIAS"
    "keyPassword" -> "OCSIGRAM_KEY_PASSWORD"
    else -> this
}

android {
    namespace = "ocsigram.lite.org"
    compileSdk = 37

    defaultConfig {
        applicationId = "ocsigram.lite.org"
        minSdk = 34
        targetSdk = 35
        versionName = providers.gradleProperty("appVersionName").get()
        versionCode = providers.gradleProperty("appVersionCode").get().toInt()
        buildConfigField("int", "TDLIB_API_ID", tdlibProperties.getProperty("api_id"))
        buildConfigField("String", "TDLIB_API_HASH", "\"${tdlibProperties.getProperty("api_hash")}\"")

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    androidResources {
        localeFilters += listOf("ru", "en")
    }

    flavorDimensions += "distribution"
    productFlavors {
        create("prod") {
            dimension = "distribution"
            applicationId = "ocsigram.lite.org"
            manifestPlaceholders["appName"] = "OcsiGram"
            buildConfigField("boolean", "IS_BETA", "false")
            buildConfigField("boolean", "ENABLE_LOGS", "false")
        }
        create("beta") {
            dimension = "distribution"
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta"
            manifestPlaceholders["appName"] = "OcsiGram Beta"
            buildConfigField("boolean", "IS_BETA", "true")
            buildConfigField("boolean", "ENABLE_LOGS", "true")
        }
    }

    val releaseStoreFile = signingValue("storeFile")
    val releaseStorePassword = signingValue("storePassword")
    val releaseKeyAlias = signingValue("keyAlias")
    val releaseKeyPassword = signingValue("keyPassword")

    signingConfigs {
        create("release") {
            if (releaseStoreFile != null && releaseStorePassword != null &&
                releaseKeyAlias != null && releaseKeyPassword != null
            ) {
                storeFile = rootProject.file(releaseStoreFile)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
            } else {
                logger.warn("OCSIGRAM: РЕЛИЗНАЯ ПОДПИСЬ НЕ НАСТРОЕНА; будет использован debug-ключ.")
                // Без полного набора release-секции AGP использует существующий debug signingConfig.
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (releaseStoreFile != null && releaseStorePassword != null &&
                releaseKeyAlias != null && releaseKeyPassword != null
            ) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }

    lint {
        abortOnError = true
        warningsAsErrors = false
    }

}

dependencies {
    implementation(project(":tdlib"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    implementation(libs.datastore.preferences)
    implementation(libs.coroutines.core)
    implementation(libs.immutable)
    implementation(libs.serialization.core)
    implementation(libs.timber)
    implementation(libs.coil.compose)
    implementation(libs.profileinstaller)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    debugImplementation(libs.leakcanary)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}

val prodGoogleServices = file("src/prod/google-services.json")
val betaGoogleServices = file("src/beta/google-services.json")
if (prodGoogleServices.exists() && betaGoogleServices.exists()) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
} else {
    logger.warn("OCSIGRAM: Firebase отключён — отсутствуют google-services.json для prod и/или beta.")
}
