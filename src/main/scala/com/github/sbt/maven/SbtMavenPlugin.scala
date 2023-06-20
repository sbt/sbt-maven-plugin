/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven

import java.io.File

import sbt.*
import sbt.librarymanagement.Configurations.Compile
import sbt.Keys.*

import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.model.Build
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.project.MavenProject
import org.apache.maven.tools.plugin.generator.GeneratorUtils.toComponentDependencies
import org.apache.maven.tools.plugin.generator.PluginDescriptorFilesGenerator
import org.apache.maven.tools.plugin.DefaultPluginToolsRequest

object SbtMavenPlugin extends AutoPlugin {

  override def requires = sbt.plugins.JvmPlugin

  object autoImport {
    val mavenGeneratePluginXml = taskKey[File]("Generate the maven plugin xml")
    val mavenPluginGoalPrefix  = settingKey[String]("Maven plugin goalPrefix")
  }

  import autoImport.*
  override lazy val projectSettings: Seq[Setting[?]] = inConfig(Compile)(mavenGeneratePluginXmlSettings)

  private def mavenGeneratePluginXmlSettings: Seq[Setting[?]] = Seq(
    mavenGeneratePluginXml := {
      compile.value // make sure runs after compilation
      val extractor = new AnnotationsMojoDescriptorExtractor(new SbtLogger(streams.value.log))
      val dependenciesArtifacts = update.value.configurations
        .filter { c => isRuntimeDep(Option(c.configuration.name)) }
        .flatMap { c => c.modules }
        .flatMap { m => m.artifacts }
      val cv = CrossVersion(scalaVersion.value, scalaBinaryVersion.value)
      val artifacts = allDependencies.value
        .filter { p => isRuntimeDep(p.configurations) }
        .map { d =>
          val versioned = cv(d)
          val artifact  = dependenciesArtifacts.find { a => a._1.name.equals(d.name) }
          new DefaultArtifact(
            versioned.organization,
            versioned.name,
            versioned.revision,
            null,
            artifact.map { i => i._1.`type` }.getOrElse("jar"),
            artifact.flatMap { i => i._1.classifier }.getOrElse(""), // if null - NPE
            null
          )
        }
      val components = toComponentDependencies(scala.collection.JavaConverters.seqAsJavaList(artifacts))

      val pid                  = if (crossPaths.value) cv(projectID.value) else projectID.value
      val pi                   = projectInfo.value
      val destinationDirectory = (Compile / resourceManaged).value / "META-INF" / "maven"

      val pluginDescriptor = new PluginDescriptor()
      pluginDescriptor.setName(pi.nameFormal)
      pluginDescriptor.setDescription(pi.description)
      pluginDescriptor.setGroupId(pid.organization)
      pluginDescriptor.setArtifactId(pid.name)
      pluginDescriptor.setVersion(pid.revision)
      pluginDescriptor.setGoalPrefix(mavenPluginGoalPrefix.value)
      pluginDescriptor.setDependencies(components)

      val build = new Build()
      build.setDirectory(destinationDirectory.getAbsolutePath)
      build.setOutputDirectory((Compile / classDirectory).value.getAbsolutePath)

      val project = new MavenProject()
      project.setGroupId(pid.organization)
      project.setArtifactId(pid.name)
      project.setBuild(build)
      project.setArtifact(new DefaultArtifact(pid.organization, pid.name, pid.revision, null, "jar", "", null))

      val request = new DefaultPluginToolsRequest(project, pluginDescriptor)

      extractor.execute(request).forEach { mojo => pluginDescriptor.addMojo(mojo) }

      val generator = new PluginDescriptorFilesGenerator()
      generator.execute(destinationDirectory, request)

      destinationDirectory / "plugin.xml"
    },
    Compile / resourceGenerators += mavenGeneratePluginXml.map { Seq(_) }.taskValue,
  )

  private def isRuntimeDep(configuration: Option[String]) = {
    configuration.fold(true) {
      case "compile" => true
      case "runtime" => true
      case _         => false
    }
  }
}
