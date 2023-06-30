// Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>

import scala.xml.XML

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtMavenPlugin)
  .settings(
    name := "Simple",
    mavenPluginGoalPrefix := "simple",
    TaskKey[Unit]("check") := {
      val plugin = XML.loadFile((Compile / resourceManaged).value / "META-INF" / "maven" / "plugin.xml")
      assertEquals("Goal prefix", (plugin \\ "plugin" \\ "goalPrefix").text, mavenPluginGoalPrefix.value)
    }
  )

def assertEquals(name: String, actual: Any, expected: Any): Unit = {
  if (actual != expected) sys.error(s"${name} is incorrect\n\tActual: ${actual}\n\tExpected: ${expected}")
}
