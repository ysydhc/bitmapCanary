plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.ysydhc.bitmapcanary"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ysydhc.bitmapcanary"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            merges.add("META-INF/androidx.core_core.version")
            merges.add("META-INF/androidx.versionedparcelable_versionedparcelable.version")
            merges.add("META-INF/androidx.interpolator_interpolator.version")
            merges.add("META-INF/androidx.cursoradapter_cursoradapter.version")
            merges.add("META-INF/androidx.drawerlayout_drawerlayout.version")
            merges.add("META-INF/androidx.fragment_fragment.version")
            merges.add("META-INF/androidx.customview_customview.version")
            merges.add("META-INF/androidx.loader_loader.version")
            merges.add("META-INF/androidx.viewpager_viewpager.version")
        }
    }
}
repositories {
    google()
    mavenCentral()
}
dependencies {

//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
    implementation(project(":bitmapCanary"))
    implementation(project(":glideExt"))
//     api("com.github.bumptech.glide:glide:5.0.3")
    api("com.github.bumptech.glide:glide:3.8.0")

    implementation("com.android.support:support-fragment:28.0.0"){
        exclude(group = "androidx.versionedparcelable", module = "versionedparcelable")
        exclude(group = "com.android.support" , module = "support-compat")
    }
// 请根据需要选择版本
//    implementation("com.android.support:support-compat:28.0.0") {
//        exclude(group = "androidx.versionedparcelable", module = "versionedparcelable")
//    }
// FragmentActivity 通常在此库中
}

