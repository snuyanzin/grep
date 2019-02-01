package org.nuyanzin.grep;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 */
public class GrepRec2 implements Callable<Object> {
  private BlockingQueue<Path> queue;
  private GrepContext grepContext;
  private final Path dir;

  public GrepRec2(
      BlockingQueue<Path> queue, GrepContext grepContext, Path dir) {
    this.queue = queue;
    this.grepContext = grepContext;
    this.dir = dir;
  }

  public Object call() {
    try {
      SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult preVisitDirectory(
            Path dir, BasicFileAttributes attrs) throws IOException {
          if (Files.isReadable(dir)) {
            return FileVisitResult.CONTINUE;
          } else {
            return FileVisitResult.SKIP_SUBTREE;
          }
        }

        @Override
        public FileVisitResult visitFile(
            Path file, BasicFileAttributes attrs) throws IOException {
          if (grepContext.getPathMatcher() != null
              && !grepContext.getPathMatcher().matches(file)) {
            return FileVisitResult.CONTINUE;
          }
          if (!Files.isReadable(file)) {
            grepContext.getOut().write(("No grants to read " + file + "\n")
                .getBytes(StandardCharsets.UTF_8));
            return FileVisitResult.CONTINUE;
          }
          try {
            queue.put(file);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          return FileVisitResult.CONTINUE;
        }
      };
      Files.walkFileTree(dir, visitor);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      queue.put(MainGrep2.END);
      queue.put(MainGrep2.END);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return null;
  }

}
