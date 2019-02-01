package org.nuyanzin.grep;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;

/**
 */
public class MainGrepRec {
  private MainGrepRec() {
  }

  public static void main(String[] args) throws IOException {
    GrepRecursive grepRecursive =
        new GrepRecursive(Paths.get("C:\\Users\\Sergey_Nuianzin")
            .toRealPath());
    ForkJoinPool p = new ForkJoinPool();
    long time = System.nanoTime();
    p.invoke(grepRecursive);
    System.out.println(System.nanoTime() - time);
  }
}
