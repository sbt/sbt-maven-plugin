// Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>

import scala.xml.NodeSeq
import scala.xml.XML

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtMavenPlugin)
  .settings(
    name := "Simple",
    mavenPluginGoalPrefix := "simple",
    TaskKey[Unit]("check") := {
      val xml = XML.loadFile((Compile / resourceManaged).value / "META-INF" / "maven" / "plugin.xml")
      val plugin = xml \\ "plugin"
      assertEquals("Goal prefix", (plugin \\ "goalPrefix").text, mavenPluginGoalPrefix.value)
      checkMojo(plugin \\ "mojos" \\ "mojo")
    }
  )

def checkMojo(mojo: NodeSeq): Unit = {
  assertEquals(
    "Mojo implementation",
    (mojo \\ "implementation").text,
    "com.github.sbt.maven.simple.SimpleMojo"
  )
}

def assertEquals(name: String, actual: Any, expected: Any): Unit = {
  if (actual != expected) sys.error(s"${name} is incorrect\n\tActual: ${actual}\n\tExpected: ${expected}")
}
