package org.nuyanzin.grep;

import java.io.OutputStream;

/**
 */
public final class GrepBuilder {
  private final Grep grep;
  private String pattern;
  private boolean caseSensitive;

  private GrepBuilder() {
    this.grep = new Grep();
  }

  public static GrepBuilder builder() {
    return new GrepBuilder();
  }

  public Grep build() {
    grep.setPattern(pattern, caseSensitive);
    return grep;
  }

  public GrepBuilder withPattern(final String pattern) {
    this.pattern = pattern;
    return this;
  }

  public GrepBuilder withCaseSensitive(final boolean value) {
    caseSensitive = value;
    return this;
  }

  public GrepBuilder withMaxFoundLines(final int maxFoundLines) {
    grep.setMaxFoundLines(maxFoundLines);
    return this;
  }

  public GrepBuilder withCountingMatchedFiles(final boolean value) {
    grep.countMatchedFiles(value);
    return this;
  }

  public GrepBuilder withFirstNFiles(final int firstNFiles) {
    grep.setFirstNFiles(firstNFiles);
    return this;
  }

  public GrepBuilder withPathMatcher(final String globPattern) {
    grep.setPathMatcher(globPattern);
    return this;
  }

  public GrepBuilder withOutputStream(final OutputStream out) {
    grep.setOutputStream(out);
    return this;
  }

}
