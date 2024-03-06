<<<<<<< HEAD
import org.gradle.api.initialization.resolve.RepositoriesMode.*

=======
>>>>>>> 7044ab24b655fd5348f453537fe799830ec8bda8
pluginManagement {
    repositories {
        google()
        mavenCentral()
<<<<<<< HEAD
        maven { url = uri("https://jitpack.io") } // for QR Code Generator library
        gradlePluginPortal()

=======
        gradlePluginPortal()
>>>>>>> 7044ab24b655fd5348f453537fe799830ec8bda8
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
<<<<<<< HEAD
        maven { url = uri("https://jitpack.io") }
=======
>>>>>>> 7044ab24b655fd5348f453537fe799830ec8bda8
    }
}

rootProject.name = "QRCheckin"
include(":app")
 