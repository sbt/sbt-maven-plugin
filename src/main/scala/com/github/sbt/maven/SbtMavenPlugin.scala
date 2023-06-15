package com.github.sbt.maven

import sbt.AutoPlugin

object SbtMavenPlugin extends AutoPlugin {

  override def requires = sbt.plugins.JvmPlugin
}
