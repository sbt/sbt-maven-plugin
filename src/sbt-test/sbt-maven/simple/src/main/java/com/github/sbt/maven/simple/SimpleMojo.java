/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven.simple;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "simple", defaultPhase = GENERATE_SOURCES, threadSafe = true)
public class SimpleMojo extends AbstractMojo {

  @Parameter(name = "string", property = "simple.string", required = true, defaultValue = "empty")
  private String _string;

  @Parameter(property = "simple.set")
  private Set<File> set = new LinkedHashSet<>();

  @Parameter(property = "simple.directory", required = true, defaultValue = "${project.build.directory}/generated-sources/simple")
  private File directory;

  @Parameter(property = "simple.map")
  private Map<String, String> map = new LinkedHashMap<>();

  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Override
  public void execute() {
    File file = new File(directory, "simple.txt");
    try {
      directory.mkdirs();
      Files.writeString(file.toPath(), "Hi, I'm Simple Mojo!");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getString() {
    return _string;
  }

  public void setString(String string) {
    this._string = string;
  }
}
