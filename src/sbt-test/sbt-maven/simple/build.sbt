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
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest"          % "3.2.16" % Test,
      "org.scalatest" %% "scalatest-wordspec" % "3.2.16" % Test,
    )
  )
