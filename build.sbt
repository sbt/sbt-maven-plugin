lazy val `sbt-maven-plugin` = (project in file(".")).enablePlugins(SbtWebBase)
  .settings(
    scriptedLaunchOpts ++= Seq(
      s"-Dplugin.version=${version.value}",
      "-Xmx128m"
    ),
    scriptedBufferLog := false
  )

sonatypeProfileName := "com.github.sbt.sbt-maven-plugin" // See https://issues.sonatype.org/browse/OSSRH-77819#comment-1203625

//description := "..." // TODO

developers += Developer(
  "playframework",
  "The Play Framework Team",
  "contact@playframework.com",
  url("https://github.com/playframework")
)

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.13.2" % "test"
)

// Customise sbt-dynver's behaviour to make it work with tags which aren't v-prefixed
ThisBuild / dynverVTagPrefix := false

// Sanity-check: assert that version comes from a tag (e.g. not a too-shallow clone)
// https://github.com/dwijnand/sbt-dynver/#sanity-checking-the-version
Global / onLoad := (Global / onLoad).value.andThen { s =>
  dynverAssertTagVersion.value
  s
}
