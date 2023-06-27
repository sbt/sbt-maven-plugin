// Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtMavenPlugin)
  .settings(
    name := "Simple",
    mavenPluginGoalPrefix := "test-prefix",
    mavenGeneratePluginXml / logLevel := Level.Debug,
    libraryDependencies += "org.apache.maven.plugin-tools" % "maven-plugin-annotations" % "3.9.0" % Provided,
    libraryDependencies += "org.apache.maven" % "maven-core" % "3.9.0" % Provided,
  )
