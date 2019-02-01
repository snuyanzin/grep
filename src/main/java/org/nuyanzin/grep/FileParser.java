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
  private final BlockingQueue<Path> queue;
  private final GrepContext grepContext;

  public FileParser(BlockingQueue<Path> queue, GrepContext grepContext) {
    this.queue = queue;
    this.grepContext = grepContext;
  }

  @Override
  public Object call() throws Exception {
    int counter = 0;
    while (true) {
      final Path path = queue.take();
      if (path == MainGrep2.END) {
        return null;
      }
      if (grepContext.getFirstNFiles() >= 0
          && grepContext.getFirstNFiles() <= counter) {
        continue;
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
        while (foundLineCounter < grepContext.getMaxFoundLines()
            && (grepContext.getFirstNFiles() >= 0
                && counter < grepContext.getFirstNFiles())
            && (line = br.readLine()) != null) {
          lineNumber++;
          if (!grepContext.getPattern().matcher(line).find()) {
            continue;
          }
          if (foundLineCounter == 0) {
            counter = grepContext.counterIncrement();
            output = new StringBuilder("File: " + path + "\n");
          }
          foundLineCounter++;
          output.append("\tLine [").append(lineNumber)
              .append("] : ").append(line).append("\n");
        }
        if (output != null) {
          grepContext.getOut().write(output.toString()
              .getBytes(StandardCharsets.UTF_8));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}
