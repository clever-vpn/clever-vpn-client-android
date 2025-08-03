pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "Clever VPN"
include(":app")
//include(":kit")
//project(":kit").projectDir=file("../clever-vpn-android-kit/kit")
val libRoot = file("../clever-vpn-android-kit")
if (libRoot.exists()) {
    includeBuild(libRoot) {
        dependencySubstitution {
            // 匹配你在 build.gradle.kts 中声明的 groupId:artifactId
            substitute(module("net.clever-vpn:clever-vpn-android-kit"))
                // 指向 includeBuild 中真正产出 AAR 的子模块
                .using(project(":kit"))
        }
    }
} else {
    println("📦 Using clever-vpn-android-kit from Maven repository")
}
