plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "ocsigram.lite.org.tdlib"
    compileSdk = 37

    defaultConfig {
        minSdk = 34
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
