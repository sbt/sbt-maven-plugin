/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven

import java.io.File

import sbt.*
import sbt.librarymanagement.Configurations.Compile
import sbt.Keys.*

import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.model.Build
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.project.MavenProject
import org.apache.maven.tools.plugin.extractor.annotations.scanner.DefaultMojoAnnotationsScanner
import org.apache.maven.tools.plugin.extractor.annotations.JavaAnnotationsMojoDescriptorExtractor
import org.apache.maven.tools.plugin.extractor.MojoDescriptorExtractor
import org.apache.maven.tools.plugin.generator.GeneratorUtils.toComponentDependencies
import org.apache.maven.tools.plugin.generator.PluginDescriptorFilesGenerator
import org.apache.maven.tools.plugin.DefaultPluginToolsRequest
import org.codehaus.plexus.logging
import org.codehaus.plexus.util.ReflectionUtils.setVariableValueInObject

object SbtMavenPlugin extends AutoPlugin {

  override def requires = sbt.plugins.JvmPlugin

  object autoImport {
    val mavenPluginGoalPrefix = settingKey[String]("Maven plugin goal prefix")
  }

  import autoImport.*
  override lazy val projectSettings: Seq[Setting[?]] = inConfig(Compile)(mavenPluginSettings)

  private def mavenPluginSettings: Seq[Setting[?]] = Seq(
    Compile / resourceGenerators += generateMavenPluginXml.taskValue,
  )

  private def createPluginDescriptor(
      goalPrefix: String,
      id: ModuleID,
      info: ModuleInfo,
      artifacts: Seq[Artifact]
  ): PluginDescriptor = {
    val pluginDescriptor = new PluginDescriptor()
    pluginDescriptor.setName(info.nameFormal)
    pluginDescriptor.setDescription(info.description)
    pluginDescriptor.setGroupId(id.organization)
    pluginDescriptor.setArtifactId(id.name)
    pluginDescriptor.setVersion(id.revision)
    pluginDescriptor.setGoalPrefix(goalPrefix)
    pluginDescriptor.setDependencies(toComponentDependencies(scala.collection.JavaConverters.seqAsJavaList(artifacts)))
    pluginDescriptor
  }

  private def findRuntimeDependencies(
      report: UpdateReport,
      dependencies: Seq[ModuleID],
      crossVersion: ModuleID => ModuleID
  ): Seq[Artifact] = {
    val artifacts = report.configurations
      .filter { c => isAnalyzedDependency(c.configuration) }
      .flatMap { c => c.modules }
      .flatMap { m => m.artifacts }
      .map(_._1)

    dependencies
      .filter { p => isAnalyzedDependency(p.configurations.map(ConfigRef(_)).getOrElse(Compile)) }
      .map { dependency =>
        val moduleId = crossVersion(dependency)
        val artifact = artifacts.find { a => a.name == dependency.name }
        new DefaultArtifact(
          moduleId.organization,
          moduleId.name,
          moduleId.revision,
          moduleId.configurations.orNull,
          artifact.map { i => i.`type` }.getOrElse("jar"),
          artifact.flatMap { i => i.classifier }.getOrElse(""),
          null
        )
      }
  }

  private def createMavenProject(projectId: ModuleID, pluginXMLDirectory: File, classDirectory: File): MavenProject = {
    val build = new Build()
    build.setDirectory(pluginXMLDirectory.getAbsolutePath)
    build.setOutputDirectory(classDirectory.getAbsolutePath)

    val project = new MavenProject()
    project.setGroupId(projectId.organization)
    project.setArtifactId(projectId.name)
    project.setBuild(build)
    project.setArtifact(
      new DefaultArtifact(projectId.organization, projectId.name, projectId.revision, null, "jar", "", null)
    )
    project
  }

  private def createMojoExtractor(logger: logging.Logger): MojoDescriptorExtractor = {
    val extractor = new JavaAnnotationsMojoDescriptorExtractor()
    val scanner   = new DefaultMojoAnnotationsScanner
    scanner.enableLogging(logger)
    setVariableValueInObject(extractor, "mojoAnnotationsScanner", scanner)
    extractor
  }

  private val generateMavenPluginXml = Def.task {
    compile.value // generation must be started after compilation
    val crossVersion       = CrossVersion(scalaVersion.value, scalaBinaryVersion.value)
    val projectId          = if (crossPaths.value) crossVersion(projectID.value) else projectID.value
    val pluginXMLDirectory = (Compile / resourceManaged).value / "META-INF" / "maven"
    val project            = createMavenProject(projectId, pluginXMLDirectory, (Compile / classDirectory).value)
    val artifacts          = findRuntimeDependencies(update.value, allDependencies.value, crossVersion)
    val pluginDescriptor   = createPluginDescriptor(mavenPluginGoalPrefix.value, projectId, projectInfo.value, artifacts)
    val request            = new DefaultPluginToolsRequest(project, pluginDescriptor)
    val extractor          = createMojoExtractor(new SbtLogger(streams.value.log))
    val generator          = new PluginDescriptorFilesGenerator()

    extractor.execute(request).forEach { mojo => pluginDescriptor.addMojo(mojo) }
    generator.execute(pluginXMLDirectory, request)

    Seq(pluginXMLDirectory / "plugin.xml")
  }

  private def isAnalyzedDependency(configuration: ConfigRef) = {
    Seq(Compile, Runtime).contains(configuration)
  }

}
