package com.likethesalad.android.buddylib

import com.likethesalad.android.buddylib.di.LibraryInjector
import com.likethesalad.android.buddylib.tasks.CreateJarDescriptionProperties
import com.likethesalad.android.common.base.BuddyPlugin
import com.likethesalad.android.common.models.AndroidBuddyExtension
import com.likethesalad.android.common.utils.Constants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer


@Suppress("UnstableApiUsage")
open class AndroidBuddyLibraryPlugin : Plugin<Project>, BuddyPlugin {

    private lateinit var project: Project

    companion object {
        private const val PROCESS_JAVA_RESOURCES_TASK_NAME = "processResources"
        private const val CREATE_JAVA_LIBRARY_FILE_TASK_NAME = "jar"
        private const val CREATE_JAR_DESCRIPTION_PROPERTIES_TASK_NAME = "createJarDescriptionProperties"
        private const val COPY_DESCRIPTION_PROPERTIES_TASK_NAME = "copyDescriptionProperties"
        private const val EXTENSION_NAME = "androidBuddyLibrary"
        private const val BYTE_BUDDY_DEPENDENCY_FORMAT = "net.bytebuddy:byte-buddy:%s"
        private const val BYTE_BUDDY_DEPENDENCY_VERSION_PROPERTY_NAME = "android.buddy.byteBuddy.version"
    }

    override fun apply(project: Project) {
        LibraryInjector.init(this)
        this.project = project
        project.pluginManager.apply(JavaLibraryPlugin::class.java)
        project.dependencies.add(
            "implementation",
            BYTE_BUDDY_DEPENDENCY_FORMAT.format(getByteBuddyVersion(project))
        )
        val extension = project.extensions.create(EXTENSION_NAME, AndroidBuddyExtension::class.java)
        val sourceSets = project.extensions.getByType(SourceSetContainer::class.java)

        val createJarDescriptionProperties = project.tasks.register(
            CREATE_JAR_DESCRIPTION_PROPERTIES_TASK_NAME,
            CreateJarDescriptionProperties::class.java,
            LibraryInjector.getCreateJarDescriptionPropertiesArgs()
        )
        createJarDescriptionProperties.configure {
            it.inputClassNames = extension.pluginNames
            it.inputClassPaths = getClassesOutputDirs(sourceSets)
            it.outputDir.set(project.file("${project.buildDir}/${it.name}"))
        }

        val javaProcessResourcesTask = project.tasks.named(PROCESS_JAVA_RESOURCES_TASK_NAME, Copy::class.java)

        val copyPropertiesTask = project.tasks.register(COPY_DESCRIPTION_PROPERTIES_TASK_NAME, Copy::class.java) {
            it.from(createJarDescriptionProperties)
            it.into({ "${javaProcessResourcesTask.get().destinationDir}/${Constants.LIBRARY_PROPERTIES_DIR}" })

            it.dependsOn(javaProcessResourcesTask)
        }

        project.tasks.named(CREATE_JAVA_LIBRARY_FILE_TASK_NAME) {
            it.dependsOn(copyPropertiesTask)
        }
    }

    private fun getClassesOutputDirs(sourceSets: SourceSetContainer): FileCollection {
        return sourceSets.getByName("main").output.classesDirs
    }

    private fun getByteBuddyVersion(project: Project): String {
        return getPropertyByteBuddyVersion(project) ?: Constants.BYTE_BUDDY_DEPENDENCY_VERSION
    }

    private fun getPropertyByteBuddyVersion(project: Project): String? {
        val propertyVersion = project.properties[BYTE_BUDDY_DEPENDENCY_VERSION_PROPERTY_NAME]

        if (propertyVersion != null && propertyVersion is String) {
            return propertyVersion.trim()
        }

        return null
    }

    override fun getLogger(): Logger {
        return project.logger
    }
}