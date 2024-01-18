// Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtMavenPlugin)
  .settings(
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
