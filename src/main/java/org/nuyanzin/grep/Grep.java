package org.nuyanzin.grep;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Pattern;

/**
 */
public class Grep extends SimpleFileVisitor<Path> {
  private static final int DEFAULT_MAX_FOUND_LINES = 10;
  private Pattern pattern;
  private PathMatcher pathMatcher;
  private boolean countMatchedFiles;
  private int firstNFiles = -1;
  private int counter = 0;
  private int maxFoundLines = DEFAULT_MAX_FOUND_LINES;
  private OutputStream out = System.out;

  @Override
  public FileVisitResult visitFile(
      Path file, BasicFileAttributes attrs) throws IOException {
    if (pathMatcher != null && !pathMatcher.matches(file)) {
      return FileVisitResult.CONTINUE;
    }
    if (!Files.isReadable(file)) {
      out.write(("No grants to read " + file + "\n")
          .getBytes(StandardCharsets.UTF_8));
      return FileVisitResult.CONTINUE;
    }
    if (firstNFiles >= 0 && firstNFiles <= counter) {
      return FileVisitResult.TERMINATE;
    }
    try (BufferedReader br =
        new BufferedReader(
            new InputStreamReader(
                new FileInputStream(file.toFile()), StandardCharsets.UTF_8))) {
      String line;
      int lineNumber = 0;
      int foundLineCounter = 0;
      while (foundLineCounter < maxFoundLines
          && (line = br.readLine()) != null) {
        lineNumber++;
        if (!pattern.matcher(line).find()) {
          continue;
        }
        if (foundLineCounter == 0) {
          counter++;
          out.write(("File: " + file + "\n").getBytes(StandardCharsets.UTF_8));
        }
        foundLineCounter++;

        out.write(("\tLine [" + lineNumber + "] : " + line + "\n")
            .getBytes(StandardCharsets.UTF_8));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return FileVisitResult.CONTINUE;
  }

  public int getCounter() {
    return counter;
  }

  public void setPattern(String pattern, boolean caseSensitive) {
    this.pattern = caseSensitive
        ? Pattern.compile(Pattern.quote(pattern))
        : Pattern.compile(Pattern.quote(pattern), Pattern.CASE_INSENSITIVE);
  }

  public void setCounter(int counter) {
    this.counter = counter;
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
}
