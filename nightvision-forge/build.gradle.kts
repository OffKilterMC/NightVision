import java.text.SimpleDateFormat
import java.util.Date

buildscript {
    repositories {
//        // These repositories are only for Gradle plugins, put any other repositories in the repository block further below
        maven { url = uri("https://repo.spongepowered.org/repository/maven-public/") }
//        mavenCentral()
        gradlePluginPortal()
        maven {
            name = "MinecraftForge"
            url = uri("https://maven.minecraftforge.net/")
        }
    }
    dependencies {
        classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
    }
}

plugins {
    id("idea")
    id("net.minecraftforge.gradle") version "[6.0,6.2)"
}

apply(plugin = "org.spongepowered.mixin")

group = settings.mavenGroup
version = settings.modVersion

// global settings
val mod_id = settings.modID
val mod_name = settings.modName
val mod_license = settings.modLicense
val mod_version = settings.modVersion
val mod_authors = settings.modAuthor
val mod_description = settings.modDescription

// forge-specific settings
val minecraft_version = property("minecraft_version") as String
val forge_version = property("forge_version") as String
val forge_version_range = property("forge_version_range") as String
val minecraft_version_range = property("minecraft_version_range") as String
val loader_version_range = property("loader_version_range") as String
val mappingsChannel = property("mappings_channel") as String
val mappingsVersion = property("mappings_version") as String

base {
    archivesName = mod_id
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

minecraft {
    // The mappings can be changed at any time and must be in the following format.
    // Channel:   Version:
    // official   MCVersion             Official field/method names from Mojang mapping files
    // parchment  YYYY.MM.DD-MCVersion  Open community-sourced parameter names and javadocs layered on top of official
    //
    // You must be aware of the Mojang license when using the 'official' or 'parchment' mappings.
    // See more information here: https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md
    //
    // Parchment is an unofficial project maintained by ParchmentMC, separate from MinecraftForge
    // Additional setup is needed to use their mappings: https://parchmentmc.org/docs/getting-started
    //
    // Use non-default mappings at your own risk. They may not always work.
    // Simply re-run your setup task after changing the mappings to update your workspace.
    mappings(mappingsChannel, mappingsVersion)

    // When true, this property will have all Eclipse/IntelliJ IDEA run configurations run the "prepareX" task for the given run configuration before launching the game.
    // enableEclipsePrepareRuns = true
    // enableIdeaPrepareRuns = true

    // This property allows configuring Gradle's ProcessResources task(s) to run on IDE output locations before launching the game.
    // It is REQUIRED to be set to true for this template to function.
    // See https://docs.gradle.org/current/dsl/org.gradle.language.jvm.tasks.ProcessResources.html
    copyIdeResources = true

    // When true, this property will add the folder name of all declared run configurations to generated IDE run configurations.
    // The folder name can be set on a run configuration using the "folderName" property.
    // By default, the folder name of a run configuration is the name of the Gradle project containing it.
    // generateRunFolders = true

    // This property enables access transformers for use in development.
    // They will be applied to the Minecraft artifact.
    // The access transformer file can be anywhere in the project.
    // However, it must be at "META-INF/accesstransformer.cfg" in the final mod jar to be loaded by Forge.
    // This default location is a best practice to automatically put the file in the right place in the final jar.
    // See https://docs.minecraftforge.net/en/latest/advanced/accesstransformers/ for more information.
    // accessTransformer = file('src/main/resources/META-INF/accesstransformer.cfg')

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        // applies to all the run configs below
        configureEach {
            workingDirectory(project.file("run"))

            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            property("forge.logging.markers", "REGISTRIES")


            // Recommended logging level for the console
            // You can set various levels here.
            // Please read: https://stackoverflow.com/questions/2031163/when-to-use-the-different-log-levels
            property("forge.logging.console.level", "debug")

//            mods {
//                "${mod_id}" {
//                    source(sourceSets.main.get())
//                }
//            }
        }

        create("client") {
            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            property("forge.enabledGameTestNamespaces", mod_id)
        }

        create("server") {
            property("forge.enabledGameTestNamespaces", mod_id)
            args("--nogui")
        }

        // This run config launches GameTestServer and runs all registered gametests, then exits.
        // By default, the server will crash when no gametests are provided.
        // The gametest system is also enabled by default for other run configs under the /test command.
        create("gameTestServer") {
            property("forge.enabledGameTestNamespaces", mod_id)
        }

        create("data") {
            // example of overriding the workingDirectory set in configureEach above
            workingDirectory(project.file("run-data"))

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            args("--mod", mod_id, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
        }
    }
}

configure<org.spongepowered.asm.gradle.plugins.MixinExtension> {
    add(sourceSets["main"], "${mod_id}-forge.mixins.refmap.json")
    config("${mod_id}-forge.mixins.json")
}

// Include resources generated by data generators.
//sourceSets.main.resources { srcDir("src/generated/resources") }

repositories {
    // Put repositories for dependencies here
    // ForgeGradle automatically adds the Forge maven and Maven Central for you

    // If you have mod jar dependencies in ./libs, you can declare them as a repository like so.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:flat_dir_resolver
    // flatDir {
    //     dir 'libs'
    // }
}

dependencies {
    // Specify the version of Minecraft to use.
    // Any artifact can be supplied so long as it has a "userdev" classifier artifact and is a compatible patcher artifact.
    // The "userdev" classifier will be requested and setup by ForgeGradle.
    // If the group id is "net.minecraft" and the artifact id is one of ["client", "server", "joined"],
    // then special handling is done to allow a setup of a vanilla dependency without the use of an external repository.
    minecraft("net.minecraftforge:forge:${minecraft_version}-${forge_version}")

    // Example mod dependency with JEI - using fg.deobf() ensures the dependency is remapped to your development mappings
    // The JEI API is declared for compile time use, while the full JEI artifact is used at runtime
    // compileOnly fg.deobf("mezz.jei:jei-${mc_version}-common-api:${jei_version}")
    // compileOnly fg.deobf("mezz.jei:jei-${mc_version}-forge-api:${jei_version}")
    // runtimeOnly fg.deobf("mezz.jei:jei-${mc_version}-forge:${jei_version}")

    // Example mod dependency using a mod jar from ./libs with a flat dir repository
    // This maps to ./libs/coolmod-${mc_version}-${coolmod_version}.jar
    // The group id is ignored when searching -- in this case, it is "blank"
    // implementation fg.deobf("blank:coolmod-${mc_version}:${coolmod_version}")

    // For more info:
    // http://www.gradle.org/docs/current/userguide/artifact_dependencies_tutorial.html
    // http://www.gradle.org/docs/current/userguide/dependency_management.html

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

tasks.named<Copy>("processResources") {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraft_version,
        "minecraft_version_range" to minecraft_version_range,
        "forge_version" to forge_version,
        "forge_version_range" to forge_version_range,
        "loader_version_range" to loader_version_range,
        "mod_id" to mod_id,
        "mod_name" to mod_name,
        "mod_license" to mod_license,
        "mod_version" to mod_version,
        "mod_authors" to mod_authors,
        "mod_description" to mod_description,
    )

    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
       expand(replaceProperties + mapOf("project" to project))
    }
}

// Example for how to get properties into the manifest for reading at runtime.
tasks.named<Jar>("jar") {
    val props = mapOf(
        "Specification-Title" to mod_id,
        "Specification-Vendor" to mod_authors,
        "Specification-Version" to "1", // We are version 1 of ourselves
        "Implementation-Title" to project.name,
        "Implementation-Version"  to project.version,
        "Implementation-Vendor" to mod_authors,
    )

    inputs.properties(props)

    manifest {
        attributes(props + mapOf(
            "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        ))
    }

    // This is the preferred method to reobfuscate your jar file
    finalizedBy("reobfJar")
}

tasks.named<JavaCompile>("compileJava") {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}
