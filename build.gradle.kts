// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

subprojects {
    if (name == "annotation" || name == "processor" || name == "kotlin") {
        apply(plugin = "java")
        apply(plugin = "maven")
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
                        classifier = "sources"
                    }
                    val javadocJar by tasks.creating(Jar::class) {
                        from(tasks.get("javadoc"))
                        classifier = "javadoc"
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