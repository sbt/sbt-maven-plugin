/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven

import sbt.AutoPlugin

object SbtMavenPlugin extends AutoPlugin {

  override def requires = sbt.plugins.JvmPlugin
}
