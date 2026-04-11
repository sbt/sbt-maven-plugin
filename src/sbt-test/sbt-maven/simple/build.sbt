// Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>

lazy val checkPluginXml = taskKey[Unit]("Verify that the generated Maven plugin descriptor exists under target.")

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtMavenPlugin)
  .settings(
    checkPluginXml := {
      val pluginXml =
        if (sbtBinaryVersion.value == "1.0") {
          target.value / "classes" / "META-INF" / "maven" / "plugin.xml"
        } else {
          target.value / "resource_managed" / "main" / "META-INF" / "maven" / "plugin.xml"
        }
      require(pluginXml.isFile, s"Expected generated plugin.xml at $pluginXml")
    },
    crossPaths            := false,
    autoScalaLibrary      := false,
    organization          := "com.example",
    name                  := "Simple",
    mavenPluginGoalPrefix := "simple",
    mavenLaunchOpts += version.apply { v => s"-Dplugin.version=$v" }.value,
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest"          % "3.2.16" % Test,
      "org.scalatest" %% "scalatest-wordspec" % "3.2.16" % Test,
    )
  )
