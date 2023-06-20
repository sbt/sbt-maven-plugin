/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven;

import static org.codehaus.plexus.util.ReflectionUtils.setVariableValueInObject;

import org.apache.maven.tools.plugin.extractor.annotations.JavaAnnotationsMojoDescriptorExtractor;
import org.apache.maven.tools.plugin.extractor.annotations.scanner.DefaultMojoAnnotationsScanner;

public class AnnotationsMojoDescriptorExtractor extends JavaAnnotationsMojoDescriptorExtractor {

  public AnnotationsMojoDescriptorExtractor(SbtLogger logger) {
    DefaultMojoAnnotationsScanner scanner = new DefaultMojoAnnotationsScanner();
    scanner.enableLogging(logger);
    try {
      setVariableValueInObject(this, "mojoAnnotationsScanner", scanner);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
