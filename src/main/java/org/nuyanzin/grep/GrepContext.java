package org.nuyanzin.grep;

import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 */
public class GrepContext {
  private static final int DEFAULT_MAX_FOUND_LINES = 10;
  private Pattern pattern;
  private PathMatcher pathMatcher;
  private boolean countMatchedFiles;
  private int firstNFiles = -1;
  private AtomicInteger counter = new AtomicInteger(0);
  private int maxFoundLines = DEFAULT_MAX_FOUND_LINES;
  private OutputStream out = System.out;

  public void setPattern(String pattern, boolean caseSensitive) {
    this.pattern = caseSensitive
        ? Pattern.compile(Pattern.quote(pattern))
        : Pattern.compile(Pattern.quote(pattern), Pattern.CASE_INSENSITIVE);
  }

  public void setMaxFoundLines(int maxFoundLines) {
    this.maxFoundLines = maxFoundLines;
  }

  public void countMatchedFiles(boolean countMatchedFiles) {
    this.countMatchedFiles = countMatchedFiles;
  }

  public boolean countMatchedFiles() {
    return countMatchedFiles;
  }

  public void setFirstNFiles(int firstNFiles) {
    this.firstNFiles = firstNFiles;
  }

  public void setPathMatcher(String glob) {
    pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
  }

  public void setOutputStream(OutputStream out) {
    this.out = out;
  }

  public Pattern getPattern() {
    return pattern;
  }

  public PathMatcher getPathMatcher() {
    return pathMatcher;
  }

  public boolean isCountMatchedFiles() {
    return countMatchedFiles;
  }

  public int getFirstNFiles() {
    return firstNFiles;
  }

  public int getMaxFoundLines() {
    return maxFoundLines;
  }

  public OutputStream getOut() {
    return out;
  }

  public int counterIncrement() {
    return counter.incrementAndGet();
  }
}
