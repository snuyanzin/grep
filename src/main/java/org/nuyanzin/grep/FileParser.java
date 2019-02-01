package org.nuyanzin.grep;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

/**
 */
public class FileParser implements Callable<Object> {
  private BlockingQueue<Path> queue;
  private static final Grep GREP = GrepBuilder.builder()
      .withCaseSensitive(true)
      .withPattern("java")
      //.withPathMatcher("**/*.java")
      //.withOutputStream(new FileOutputStream(new File("myfile")))
      // .withMaxFoundLines(10)
      //.withFirstNFiles(10)
      .build();

  public FileParser(BlockingQueue<Path> queue) {
    this.queue = queue;
  }

  @Override
  public Object call() throws Exception {
    while (true) {
      final Path path = queue.take();
      if (path == MainGrep2.END) {
        return null;
      }
      try (BufferedReader br =
          new BufferedReader(
              new InputStreamReader(
                  new FileInputStream(path.toFile()),
                      StandardCharsets.UTF_8))) {
        String line;
        int lineNumber = 0;
        int foundLineCounter = 0;
        StringBuilder output = null;
        while (foundLineCounter < GREP.getMaxFoundLines()
            && (line = br.readLine()) != null) {
          lineNumber++;
          if (!GREP.getPattern().matcher(line).find()) {
            continue;
          }
          if (foundLineCounter == 0) {
            //  counter++;
            output = new StringBuilder("File: " + path + "\n");
          }
          foundLineCounter++;
          output.append("\tLine [").append(lineNumber)
              .append("] : ").append(line).append("\n");
        }
        if (output != null) {
          GREP.getOut().write(output.toString()
              .getBytes(StandardCharsets.UTF_8));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}
