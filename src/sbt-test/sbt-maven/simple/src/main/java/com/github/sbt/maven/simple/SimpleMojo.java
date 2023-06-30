/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven.simple;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "simple", defaultPhase = GENERATE_SOURCES, threadSafe = true)
public class SimpleMojo extends AbstractMojo {

  @Override
  public void execute() {
    System.out.println("Hi, I'm Simple Mojo!");
  }
}
