package org.nuyanzin.grep;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 */
public class Grep extends SimpleFileVisitor<Path> {
  private final GrepContext grepContext;
  private int counter;

  public Grep(GrepContext grepContext) {
    this.grepContext = grepContext;
  }

  @Override
  public FileVisitResult visitFile(
      Path file, BasicFileAttributes attrs) throws IOException {
    final PathMatcher pathMatcher = grepContext.getPathMatcher();
    if (pathMatcher != null && !pathMatcher.matches(file)) {
      return FileVisitResult.CONTINUE;
    }
    final OutputStream out = grepContext.getOut();
    if (!Files.isReadable(file)) {
      out.write(("No grants to read " + file + "\n")
          .getBytes(StandardCharsets.UTF_8));
      return FileVisitResult.CONTINUE;
    }
    final int firstNFiles = grepContext.getFirstNFiles();
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
      while (foundLineCounter < grepContext.getMaxFoundLines()
          && (line = br.readLine()) != null) {
        lineNumber++;
        if (!grepContext.getPattern().matcher(line).find()) {
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

}
