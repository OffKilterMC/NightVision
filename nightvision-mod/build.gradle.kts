import net.fabricmc.loom.task.RemapJarTask

plugins {
    base
}

tasks.named("clean") {
    dependsOn(":nightvision-fabric:clean")
    dependsOn(":nightvision-forge:clean")
}

tasks.register<Jar>("jar") {
    val remapFabric = project(":nightvision-fabric").tasks.named<RemapJarTask>("remapJar")
    dependsOn(
        remapFabric,
        project(":nightvision-forge").tasks.named("reobfJar")
    )
    from(zipTree({remapFabric.get().archiveFile}))
    from(zipTree({project(":nightvision-forge").tasks.getByName("jar").outputs.files.singleFile})) {
        exclude("assets/nightvision/")
    }
    manifest {
        from(
            zipTree({remapFabric.get().archiveFile}).find{ it.name=="MANIFEST.MF"}
        )
        from(
            zipTree({project(":nightvision-forge").tasks.getByName("jar").outputs.files.singleFile}).find{ it.name=="MANIFEST.MF"}
        )
    }
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveVersion.set(settings.modVersion)
}

tasks.named("assemble") {
    dependsOn("jar")
}
