plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.maven.publish)
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates("io.github.pearwhite-mb", "aura", "0.1.0")

    pom {
        name.set("Aura")
        description.set("Animated AGSL aura for Jetpack Compose: soft glowing spheres, cards and full-screen backgrounds with weighted color palettes.")
        inceptionYear.set("2026")
        url.set("https://github.com/pearwhite-mb/aura/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("pearwhite")
                name.set("Anatoliy Lyashenko")
                url.set("https://github.com/pearwhite-mb/")
            }
        }
        scm {
            url.set("https://github.com/pearwhite-mb/aura/")
            connection.set("scm:git:git://github.com/pearwhite-mb/aura.git")
            developerConnection.set("scm:git:ssh://git@github.com/pearwhite-mb/Aura.git")
        }
    }
}

android {
    namespace = "io.github.pearwhitemb.aura"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

}

kotlin {
    explicitApi()
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.lifecycle.runtime.compose)

    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.animation.core)
    api(libs.androidx.compose.foundation)

    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.lifecycle.runtime.testing)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
