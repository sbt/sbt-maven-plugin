/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven

import java.io.File

import sbt.*
import sbt.librarymanagement.Configurations.Compile
import sbt.Keys.*

object SbtMavenPlugin extends AutoPlugin {

  override def requires = sbt.plugins.JvmPlugin

  override lazy val projectSettings: Seq[Setting[?]] = inConfig(Compile)(mavenPluginSettings)

  private def mavenPluginSettings: Seq[Setting[?]] = Seq(
    Compile / resourceGenerators += generateMavenPluginXml.taskValue
  )

  private val generateMavenPluginXml = Def.task {
    compile.value // generation must be started after compilation
    val pluginXML = (Compile / resourceManaged).value / "META-INF" / "maven" / "plugin.xml"
    // TODO: implement
    IO.write(pluginXML, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
    Seq(pluginXML)
  }

}
