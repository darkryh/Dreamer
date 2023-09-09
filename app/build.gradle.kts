plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ead.project.dreamer"
        minSdk = 26
        targetSdk = 34
        versionCode = 20
        versionName = "1.91"
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
    
    val roomVersion = "2.5.2"
    val lottieVersion = "4.2.1"
    val media3Version = "1.1.1"
    val okhttpVersion = "4.10.0"
    val retrofitVersion = "2.9.0"
    val admobVersion = "22.3.0"
    val ktorVersion = "2.3.4"

    //own libs
    implementation("com.github.darkryh:lifecycle-commons-ktx:0.0.3")
    implementation("com.github.darkryh:views-commons-ktx:0.0.5")
    implementation("com.github.darkryh:resource-commons-ktx:0.0.1")
    implementation("com.github.darkryh:metrics-commons-ktx:0.0.1")

    //basics
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.media:media:1.6.0")

    //desugar
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

    //room
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    //image loader coil
    implementation("io.coil-kt:coil:2.2.2")

    //shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0")

    //html parser
    implementation("org.jsoup:jsoup:1.15.3")

    //data store
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //media3
    implementation("androidx.media3:media3-ui:$media3Version")
    // For media playback using ExoPlayer
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    // For DASH playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-dash:$media3Version")
    // For HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    // For RTSP playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-rtsp:$media3Version")
    // For SS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-smoothstreaming:$media3Version")
    // For ad insertion using the Interactive Media Ads SDK with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-ima:$media3Version")
    // For exposing and controlling media sessions
    implementation("androidx.media3:media3-session:$media3Version")
    // For extracting data from media containers
    implementation("androidx.media3:media3-extractor:$media3Version")
    // For integrating with Cast
    implementation("androidx.media3:media3-cast:$media3Version")

    //apacheCommons
    implementation("org.apache.commons:commons-text:1.9")

    //workManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.hilt:hilt-common:1.0.0")
    implementation("androidx.hilt:hilt-work:1.0.0")


    //gson
    implementation("com.google.code.gson:gson:2.10.1")

    //dagger hilt
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    //lottie
    implementation("com.airbnb.android:lottie:$lottieVersion")

    //okhttp
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion") {
        // exclude Retrofit’s OkHttp peer-dependency module and define your own module import
        exclude (module = "okhttp")
    }
    //gson converter
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")


    //calculator string
    implementation("net.objecthunter:exp4j:0.4.8")

    //admob
    implementation("com.google.android.gms:play-services-ads:$admobVersion")

    //firebase
    implementation(enforcedPlatform("com.google.firebase:firebase-bom:31.0.1"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-inappmessaging-display-ktx")

    //cast
    implementation("androidx.mediarouter:mediarouter:1.4.0")
    implementation("com.google.android.gms:play-services-cast-framework:21.3.0")
    implementation("com.android.volley:volley:1.2.1")

    //embedded Server
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-partial-content:$ktorVersion")

}