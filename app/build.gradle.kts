
val pkg: String = providers.gradleProperty("clevervpnPackageName").get()
val ns: String = providers.gradleProperty("clevervpnSpacename").get()
val androidReleaseStoreFile: String? = providers.gradleProperty("androidReleaseStoreFile").orNull
val androidReleaseStorePassword: String? = providers.gradleProperty("androidReleaseStorePassword").orNull
val androidReleaseKeyAlias: String? = providers.gradleProperty("androidReleaseKeyAlias").orNull
val androidReleaseKeyPassword: String? = providers.gradleProperty("androidReleaseKeyPassword").orNull
val hasAndroidReleaseSigning: Boolean = listOf(
    androidReleaseStoreFile,
    androidReleaseStorePassword,
    androidReleaseKeyAlias,
    androidReleaseKeyPassword,
).all { !it.isNullOrBlank() }

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = ns
    compileSdk = 35

    signingConfigs {
        if (hasAndroidReleaseSigning) {
            create("release") {
                storeFile = file(androidReleaseStoreFile!!)
                storePassword = androidReleaseStorePassword
                keyAlias = androidReleaseKeyAlias
                keyPassword = androidReleaseKeyPassword
            }
        }
    }

    defaultConfig {
        applicationId = pkg
        minSdk = 24
        targetSdk = 35
        versionCode = providers.gradleProperty("clevervpnVersionCode").get().toInt()
        versionName = providers.gradleProperty("clevervpnVersionName").get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            val releaseSigning = signingConfigs.findByName("release")
            if (releaseSigning != null) {
                signingConfig = releaseSigning
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_11
//        targetCompatibility = JavaVersion.VERSION_11
//    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.accompanist.adaptive)
    implementation(libs.androidx.material3.window.size.class1.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.core)
    implementation(libs.zxing.android.embedded)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//    implementation(project(":kit"))
    implementation(libs.clever.vpn.android.kit)
    coreLibraryDesugaring(libs.desugarJdkLibs)
    implementation(libs.camera.core)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.camera.camera2)

}