plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.dagger.hilt)
    id("kotlin-parcelize")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlitycs)
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ead.project.dreamer"
        minSdk = 26
        targetSdk = 34
        versionCode = 25
        versionName = "1.925"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    sourceSets {
        named("main") {
            assets.srcDirs("src/main/assets")
        }
    }

    packaging {
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/io.netty.versions.properties")
    }

    namespace = "com.ead.project.dreamer"
}

dependencies {
    
    //own libs
    implementation(libs.moongetter)
    implementation(libs.somoskudasai)
    implementation(libs.nomoreadsonmywebviewplayer)
    implementation(libs.monoschinosapi)
    implementation(libs.lifecycle.commons.ktx)
    implementation(libs.views.commons.ktx)
    implementation(libs.resource.commons.ktx)
    implementation(libs.metrics.commons.ktx)

    //basics
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.preference.ktx)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    implementation(libs.preference.ktx)
    implementation(libs.media)

    //lifecycles
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    //navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    //tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //desugar
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    //room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //image loader coil
    implementation(libs.coil)

    //shimmer
    implementation(libs.shimmer)

    //html parser
    implementation(libs.jsoup)

    //data store
    implementation(libs.datastore.preferences)

    //media3
    implementation(libs.media3.ui)
    // For media playback using ExoPlayer
    implementation(libs.media3.exoplayer)
    // For DASH playback support with ExoPlayer
    implementation(libs.media3.exoplayer.dash)
    // For HLS playback support with ExoPlayer
    implementation(libs.media3.exoplayer.hls)
    // For RTSP playback support with ExoPlayer
    implementation(libs.media3.exoplayer.rtsp)
    // For SS playback support with ExoPlayer
    implementation(libs.media3.exoplayer.smoothstreaming)
    // For ad insertion using the Interactive Media Ads SDK with ExoPlayer
    implementation(libs.media3.exoplayer.ima)
    // For exposing and controlling media sessions
    implementation(libs.media3.session)
    // For extracting data from media containers
    implementation(libs.media3.extractor)
    // For integrating with Cast
    implementation(libs.media3.cast)

    //workManager
    implementation(libs.work.runtime.ktx)
    implementation(libs.hilt.common)
    implementation(libs.hilt.work)

    //gson
    implementation(libs.gson)

    //dagger hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)

    //lottie
    implementation(libs.lottie)

    //okhttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    //retrofit
    implementation(libs.retrofit) { exclude (module = "okhttp") }

    //gson converter
    implementation(libs.converter.gson)

    //admob
    implementation(libs.play.services.ads)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.inappmessaging.display.ktx)

    //cast
    implementation(libs.mediarouter)
    implementation(libs.play.services.cast.framework)
    implementation(libs.volley)

    //embedded Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.partial.content)

    //play In-App Update:
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
}