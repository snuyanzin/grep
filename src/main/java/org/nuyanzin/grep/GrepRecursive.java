package org.nuyanzin.grep;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

/**
 */
public class GrepRecursive extends RecursiveAction {
  private static final long serialVersionUID = 6134224156285776915L;
  private static final Grep GREP = GrepBuilder.builder()
      .withCaseSensitive(true)
      .withPattern("java")
      //.withPathMatcher("**/*.java")
      //.withOutputStream(new FileOutputStream(new File("myfile")))
     // .withMaxFoundLines(10)
      //.withFirstNFiles(10)
      .build();
  private final Path dir;

  public GrepRecursive(Path dir) {
    this.dir = dir;
  }

  @Override
  protected void compute() {
    final List<GrepRecursive> walks = new ArrayList<>();
    try {
      SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(
            Path dir, BasicFileAttributes attrs) throws IOException {
          if (!dir.equals(GrepRecursive.this.dir)) {
            GrepRecursive w = new GrepRecursive(dir);
            w.fork();
            walks.add(w);

            return FileVisitResult.SKIP_SUBTREE;
          } else {
            return FileVisitResult.CONTINUE;
          }
        }

        @Override
        public FileVisitResult visitFile(
            Path file, BasicFileAttributes attrs) throws IOException {
          if (GREP.getPathMatcher() != null
              && !GREP.getPathMatcher().matches(file)) {
            return FileVisitResult.CONTINUE;
          }
          if (!Files.isReadable(file)) {
            GREP.getOut().write(("No grants to read " + file + "\n")
                .getBytes(StandardCharsets.UTF_8));
            return FileVisitResult.CONTINUE;
          }
          try (BufferedReader br =
              new BufferedReader(
                  new InputStreamReader(
                      new FileInputStream(file.toFile()),
                          StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            int foundLineCounter = 0;
            while (foundLineCounter < GREP.getMaxFoundLines()
                && (line = br.readLine()) != null) {
              lineNumber++;
              if (!GREP.getPattern().matcher(line).find()) {
                continue;
              }
              if (foundLineCounter == 0) {
                //  counter++;
                GREP.getOut().write(("File: " + file + "\n")
                    .getBytes(StandardCharsets.UTF_8));
              }
              foundLineCounter++;

              GREP.getOut().write(
                  ("\tLine [" + lineNumber + "] : " + line + "\n")
                      .getBytes(StandardCharsets.UTF_8));
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
          return FileVisitResult.CONTINUE;
        }
      };
      Files.walkFileTree(dir, visitor);
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (GrepRecursive w : walks) {
      w.join();
    }
  }
}
