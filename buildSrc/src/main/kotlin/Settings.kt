import org.gradle.api.Project

class ProjectSettings(project: Project) {
    val minecraftVersion = project.rootProject.property("minecraft_version") as String
    val loaderVersion = project.rootProject.property("loader_version") as String
    val modVersion = project.rootProject.property("mod_version") as String
    val archivesBaseName = project.rootProject.property("archives_base_name") as String
    val fabricVersion = project.rootProject.property("fabric_version") as String
    val loomVersion = project.rootProject.property("loom_version") as String
    val mavenGroup = project.rootProject.property("maven_group") as String
    val modID = project.rootProject.property("mod_id") as String
    val modDescription = project.rootProject.property("mod_description") as String
    val modName = project.rootProject.property("mod_name") as String
    val modLicense = project.rootProject.property("mod_license") as String
    val modAuthor = project.rootProject.property("mod_author") as String
}

val Project.settings
    get() = ProjectSettings(this)
