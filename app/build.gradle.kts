import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.room")
}

// Load environment variables from root .env file
val envFile = file("../../.env")
val envProps = Properties()
if (envFile.exists()) {
    envProps.load(FileInputStream(envFile))
}

android {
    namespace = "com.captainslog"
    compileSdk = 35

    signingConfigs {
        create("release") {
            val keystoreFile = file(envProps.getProperty("KEYSTORE_FILE", "../keystore/release.jks"))
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = envProps.getProperty("KEYSTORE_PASSWORD", "")
                keyAlias = envProps.getProperty("KEY_ALIAS", "captainslog")
                keyPassword = envProps.getProperty("KEY_PASSWORD", "")
            }
        }
    }

    defaultConfig {
        applicationId = "com.jware.captainslog"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.captainslog.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // No API key needed for OpenStreetMap
    }

    buildTypes {
        debug {
            // Allow HTTP connections and disable certificate pinning for development
            buildConfigField("Boolean", "ALLOW_HTTP", "true")
            buildConfigField("Boolean", "REQUIRE_CERT_PINNING", "false")
            
            // Default server URL from environment
            val defaultServerUrl = envProps.getProperty("DEFAULT_SERVER_URL") ?: "http://10.0.0.145:8585"
            buildConfigField("String", "DEFAULT_SERVER_URL", "\"$defaultServerUrl\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            // Require HTTPS and certificate pinning in production
            buildConfigField("Boolean", "ALLOW_HTTP", "false")
            buildConfigField("Boolean", "REQUIRE_CERT_PINNING", "true")
            
            // Default server URL from environment
            val defaultServerUrl = envProps.getProperty("DEFAULT_SERVER_URL") ?: "https://boat.jware.dev"
            buildConfigField("String", "DEFAULT_SERVER_URL", "\"$defaultServerUrl\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            all {
                it.useJUnitPlatform()
            }
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.5")
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-sse:4.12.0")

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // OpenStreetMap with osmdroid
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    
    // Location services (still needed for GPS)
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // QR Code Generation (ZXing)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.5.2")

    // QR Code Scanning (ML Kit)
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // CameraX
    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Hilt Dependency Injection
    val hiltVersion = "2.51.1"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    ksp("com.google.dagger:hilt-android-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    
    // Kotest Property Testing
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    
    // Hilt Testing
    testImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kspTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
