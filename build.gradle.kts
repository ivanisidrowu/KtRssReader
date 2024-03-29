// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath(libs.plugin.androidGradle)
        classpath(libs.plugin.kotlinGradle)
        classpath(libs.plugin.androidMavenGradle)
        classpath(libs.plugin.ktlint)
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        disabledRules.set(kotlin.collections.setOf("import-ordering", "no-wildcard-imports"))
    }
    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
        workerMaxHeapSize.set("2G")
    }

    if (name == "annotation" || name == "processor" || name == "kotlin") {
        apply(plugin = "java")
        apply(plugin = "maven-publish")

        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>(project.name) {
                    from(components["java"])

                    // If you configured them before
                    // val sourcesJar by tasks.getting(Jar::class)
                    // val javadocJar by tasks.getting(Jar::class)

                    val sourcesJar by tasks.creating(Jar::class) {
                        val sourceSets: SourceSetContainer by project
                        from(sourceSets["main"].allJava)
                        archiveClassifier.set("sources")
                    }
                    val javadocJar by tasks.creating(Jar::class) {
                        from(tasks["javadoc"])
                        archiveClassifier.set("javadoc")
                    }

                    artifact(sourcesJar)
                    artifact(javadocJar)
                }
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}