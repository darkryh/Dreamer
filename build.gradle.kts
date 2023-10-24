import org.jetbrains.kotlin.gradle.tasks.KaptGenerateStubs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}

allprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.withType<KaptGenerateStubs>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}