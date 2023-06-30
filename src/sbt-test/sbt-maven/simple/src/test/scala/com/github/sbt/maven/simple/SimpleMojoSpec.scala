/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven.simple

import scala.io.BufferedSource
import scala.io.Source
import scala.xml.Elem
import scala.xml.NodeSeq
import scala.xml.XML

import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec

class SimpleMojoSpec extends AnyWordSpec with should.Matchers {

  val pluginXML: BufferedSource = Source.fromResource("META-INF/maven/plugin.xml")

  "plugin.xml" should {
    "be generated" in {
      pluginXML should not be null
    }
  }

  val plugin: NodeSeq = XML.load(pluginXML.bufferedReader()) \\ "plugin"
  "Plugin" should {
    "be defined" in {
      plugin should not be empty
    }
    "have correct goal prefix" in {
      (plugin \\ "goalPrefix").text should be("simple")
    }
  }

  val mojo: NodeSeq = plugin \\ "mojos" \\ "mojo"
  "Mojo" should {
    "be defined" in {
      mojo should not be empty
    }
    "have correct implementation" in {
      (mojo \\ "implementation").text should be("com.github.sbt.maven.simple.SimpleMojo")
    }
  }
}
