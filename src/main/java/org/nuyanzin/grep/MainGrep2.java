package org.nuyanzin.grep;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 */
public class MainGrep2 {
  public static final Path END = Paths.get(".");
  private MainGrep2() {
  }

  public static void main(String[] args) throws Exception {
    final BlockingQueue<Path> queue = new ArrayBlockingQueue<>(100);
    GrepContext grepContext = GrepBuilder.builder()
            .withCaseSensitive(true)
            .withPattern("sqlline")
            .withPathMatcher("**/*.java")
            //.withOutputStream(new FileOutputStream(new File("myfile")))
            .withMaxFoundLines(1)
            .withFirstNFiles(10)
            .build();
    final GrepRec2 grepRecursive =
        new GrepRec2(
            queue, grepContext, Paths.get("C:\\Users\\Sergey_Nuianzin"));
    Collection<Callable<Object>> tasks = new ArrayList<>();
    tasks.add(grepRecursive);
    tasks.add(new FileParser(queue, grepContext));
    tasks.add(new FileParser(queue, grepContext));
    ForkJoinPool p = new ForkJoinPool();
    long time = System.nanoTime();
    p.invokeAll(tasks);
    System.out.println(System.nanoTime() - time);
  }
}
