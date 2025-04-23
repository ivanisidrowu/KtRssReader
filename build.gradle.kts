import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.ksp) apply false
    kotlin("jvm") version libs.versions.kotlin apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
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
