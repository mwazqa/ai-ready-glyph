plugins { id("com.android.application") }

android {
    namespace = "pl.glyphtoy.aiready"
    compileSdk = 35
    defaultConfig {
        applicationId = "pl.glyphtoy.aiready"
        minSdk = 35
        targetSdk = 35
        versionCode = 17
        versionName = "0.7.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint { abortOnError = true }
}
dependencies {
    implementation(files("libs/glyph-matrix-sdk-2.0.aar"))
    testImplementation("junit:junit:4.13.2")
}
