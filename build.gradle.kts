// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    // alias(libs.plugins.android)
}
repositories {
    google()
    mavenCentral()
    uri(path = "https://api.xposed.info/")
    maven { setUrl("http://maven.applib.baidu-int.com:8681/repository/applib-public/") }
}