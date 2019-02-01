package org.nuyanzin.grep;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 */
public class MainGrep {
  private MainGrep() {
  }

  public static void main(String[] args) throws IOException {
    Path path = Paths.get("C:\\Users\\Sergey_Nuianzin");
    GrepContext grepContext = GrepBuilder.builder()
        .withCaseSensitive(true)
        .withPattern("java")
      //  .withPathMatcher("**/*.java")
        //.withOutputStream(new FileOutputStream(new File("myfile")))
  //      .withMaxFoundLines(10)
   //     .withFirstNFiles(10)
        .build();
    Grep visitor = new Grep(grepContext);
    long time = System.nanoTime();
    try {
      Files.walkFileTree(path, visitor);
    } catch (Exception ignored) {
    }

    System.out.println(System.nanoTime() - time);
    System.out.println("Total: " + visitor.getCounter());
  }
}
