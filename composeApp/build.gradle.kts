import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)

    id("com.github.gmazzo.buildconfig") version "5.5.1"
}

val appName = "Quiz"
val appBundleId = "pt.demanda.quiz"
val appVersionName = "1.1"
val appVersionCode = 2

buildConfig {
    className("QuizBuildConfig")    // forces the class name. Defaults to 'BuildConfig'
    packageName(appBundleId)        // forces the package. Defaults to '${project.group}'

    buildConfigField("BUNDLE_ID", appBundleId)
    buildConfigField("VERSION", "$appVersionName.$appVersionCode")

    buildConfigField("QUIZ_QUESTION_COUNT", 30)
    buildConfigField("QUESTION_TIME", 60)
    buildConfigField("HIGHSCORE_COUNT", 10)
}

kotlin {

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true

            binaryOption("bundleId", appBundleId)
            binaryOption("bundleShortVersionString", "$appVersionName")
            binaryOption("bundleVersion", "$appVersionName.$appVersionCode")
        }
    }

    jvm("desktop")
    
    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)

            implementation(libs.koin.android)

            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonMain.dependencies {

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.components.resources)

            implementation(libs.navigation.compose)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.napier)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)

            implementation(libs.url.encoder)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)


            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.paging)
            implementation(libs.sqlite.bundled)

            implementation(libs.androidx.datastore.preferences)
        }

        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.kotlinx.coroutines.swing)

            implementation(libs.ktor.client.okhttp)
        }
    }
}

android {
    namespace = appBundleId
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = appBundleId

        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = appVersionCode
        versionName = appVersionName
    }

    signingConfigs {
        create("release") {
            storeFile = file("android_keystore.jks")
            storePassword = "123456"
            keyAlias = "release_signing"
            keyPassword = "123456"
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules-android.pro")

            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.material3.android)
    debugImplementation(compose.uiTooling)

    //ksp(libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}


compose.desktop {
    application {
        mainClass = "$appBundleId.MainKt"

        buildTypes.release.proguard {
            // Compillation fails on Desktop/JVM when building with Proguard and JDK21 #4216
            // https://github.com/JetBrains/compose-multiplatform/issues/4216
            version = "7.6.1"

            isEnabled = true
            obfuscate = true
            optimize = true

            // additional rule
            configurationFiles.from(file("proguard-rules-desktop.pro"))
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = appName
            packageVersion = "$appVersionName.$appVersionCode"

            description = appName
            copyright = "© 2024 $appName. All rights reserved."
            vendor = "demanda"

            macOS {
                iconFile.set(project.file("../source_assets/app.icns"))
            }

            windows {
                iconFile.set(project.file("../source_assets/app.ico"))
            }

            // jdk unsafe error appearing after the binary build run, not while direct running #2686
            // https://github.com/JetBrains/compose-multiplatform/issues/2686#issuecomment-1413429842
            modules("jdk.unsupported")
        }
    }
}

//
// Desktop try to load proguard rules from jars
//
tasks.withType(org.jetbrains.compose.desktop.application.tasks.AbstractProguardTask::class.java) {
    val proguardFile = File.createTempFile("tmp", ".pro", temporaryDir)
    proguardFile.deleteOnExit()

    compose.desktop.application.buildTypes.release.proguard {
        configurationFiles.from(proguardFile)
    }

    doFirst {
        proguardFile.bufferedWriter().use { proguardFileWriter ->

            inputFiles
                .filter { it.extension == "jar" }
                .forEach { jar ->
                    val zip = zipTree(jar)
                    zip.matching { include("META-INF/**/proguard/*.pro") }.forEach {
                        proguardFileWriter.appendLine("########   ${jar.name} ${it.name}")
                        proguardFileWriter.appendLine(it.readText())
                    }
                    zip.matching { include("META-INF/services/*") }.forEach {
                        it.readLines().forEach { cls ->
                            val rule = "-keep class $cls"
                            proguardFileWriter.appendLine(rule)
                        }
                    }
                }

        }
    }
}

