/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.uber.artist

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.BaseVariant
import com.uber.artist.internal.util.resolveVariantOutputDir
import org.gradle.api.DomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project

class ArtistPlugin : Plugin<Project> {

    companion object {
        private val ARTIST = "artist"
    }

    private val artistExtension = ArtistExtension()

    override fun apply(project: Project) {
        project.extensions.add(ARTIST, artistExtension)
        project.afterEvaluate {
            project.plugins.all {
                when (it) {
                    is AppPlugin -> with(project.extensions.getByType(AppExtension::class.java)) {
                        configureAndroid(project, applicationVariants)
                    }
                    is LibraryPlugin -> with(project.extensions.getByType(LibraryExtension::class.java)) {
                        configureAndroid(project, libraryVariants)
                    }
                }
            }
        }
    }

    private fun <T : BaseVariant> configureAndroid(
            project: Project,
            variants: DomainObjectSet<T>) {
        val generateViews = project.task("generateViews")
        variants.all { variant ->
            val artistTask = project.tasks.create(
                    "generate${variant.name.capitalize()}Views", ArtistTask::class.java)
                    .apply {
                        group = ARTIST
                        outputDirectory = resolveVariantOutputDir(project, variant, ARTIST)
                        description = "Generate ${variant.name} base views."
                        packageName = artistExtension.packageName ?: variant.applicationId
                        viewPackageName = artistExtension.viewPackageName ?: packageName
                        superinterfaceClassName = artistExtension.interfaceClassName
                        viewNamePrefix = artistExtension.viewNamePrefix
                        formatSource = artistExtension.formatSource
                    }
            generateViews.dependsOn(artistTask)

            variant.registerJavaGeneratingTask(artistTask, artistTask.outputDirectory)
        }
    }
}
