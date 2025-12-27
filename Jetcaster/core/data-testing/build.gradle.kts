import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.jetcaster.core.data.testing"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            languageVersion.set(KotlinVersion.KOTLIN_2_2) // TODO: Remove this kludge eventually
            coreLibrariesVersion = "2.2.21" // TODO: Remove this kludge eventually
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(projects.core.data)
    coreLibraryDesugaring(libs.core.jdk.desugaring)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
