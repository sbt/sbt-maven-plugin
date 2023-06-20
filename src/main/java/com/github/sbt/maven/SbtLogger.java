/*
 * Copyright (C) from 2023 The sbt contributors <https://github.com/sbt>
 */

package com.github.sbt.maven;

import static org.codehaus.plexus.util.ExceptionUtils.getStackTrace;

import java.util.function.Supplier;
import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.Logger;
import sbt.internal.util.ManagedLogger;
import sbt.util.Level;
import scala.Enumeration;

public class SbtLogger extends AbstractLogger {
  private final ManagedLogger logger;

  public SbtLogger(ManagedLogger logger) {
    // Set DEBUG level, because sbt logger will filter
    super(LEVEL_DEBUG, "sbt-logger");
    this.logger = logger;
  }

  @Override
  public void debug(String message, Throwable throwable) {
    log(Level.Debug(), message, throwable);
  }

  @Override
  public void info(String message, Throwable throwable) {
    log(Level.Info(), message, throwable);
  }

  @Override
  public void warn(String message, Throwable throwable) {
    log(Level.Warn(), message, throwable);
  }

  @Override
  public void error(String message, Throwable throwable) {
    log(Level.Error(), message, throwable);
  }

  @Override
  public void fatalError(String message, Throwable throwable) {
    log(Level.Error(), message, throwable);
  }

  @Override
  public Logger getChildLogger(String name) {
    return this;
  }

  private void log(final Enumeration.Value level, final String message, final Throwable throwable) {
    logger.log(
        level,
        (Supplier<String>)
            () ->
                throwable != null
                    ? message + System.lineSeparator() + getStackTrace(throwable)
                    : message);
  }
}
