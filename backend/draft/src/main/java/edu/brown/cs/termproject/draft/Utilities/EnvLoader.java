package edu.brown.cs.termproject.draft.Utilities;

import java.io.*;
import java.util.*;

public class EnvLoader {
  public static void loadEnv(String filename) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(filename));
    String line;
    while ((line = reader.readLine()) != null) {
      if (line.contains("=")) {
        String[] parts = line.split("=", 2);
        System.setProperty(parts[0], parts[1]);
      }
    }
    reader.close();
  }
}
