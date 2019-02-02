package org.nuyanzin.grep;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

/**
 */
public class ZipRead {

  private ZipRead() {
  }

  public static void main(String[] args) throws IOException {
    GrepContext grepContext = GrepBuilder.builder()
        .withCaseSensitive(true)
        .withPattern("java")
        //  .withPathMatcher("**/*.java")
        //.withOutputStream(new FileOutputStream(new File("myfile")))
        //      .withMaxFoundLines(10)
        //     .withFirstNFiles(10)
        .build();

    String fileName = "D:\\work\\apache-ant-1.10.5-bin.zip";
    try (ZipFile zipFile = new ZipFile(fileName)) {
      FileSystem fileSystem =
          FileSystems.newFileSystem(Paths.get(fileName), null);
      zipFile.stream().forEach(t -> {
        try {
          BufferedReader br =
              Files.newBufferedReader(fileSystem.getPath(t.getName()));
          String line;
          int lineNumber = 0;
          int foundLineCounter = 0;
          StringBuilder output = null;
          final String path =
              Paths.get(fileName)
                  + fileSystem.getSeparator()
                  + fileSystem.getPath(t.getName());
          while (foundLineCounter < grepContext.getMaxFoundLines()
              && (line = br.readLine()) != null) {
            lineNumber++;
            if (!grepContext.getPattern().matcher(line).find()) {
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
            grepContext.getOut().write(output.toString()
                .getBytes(StandardCharsets.UTF_8));
          }
        } catch (IOException e) {
          System.out.println("Failed for " + fileSystem.getPath(t.getName()));
          e.printStackTrace();
        }
      });

    } catch (IOException e) {
      System.out.println("Error while processing file " + fileName);
      e.printStackTrace();
      // error while opening a ZIP file
    }

  }
}
