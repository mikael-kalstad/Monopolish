package com.teamfour.monopolish.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class handles all general file reading.
 *
 * @author      Eirik Hemstad
 * @version     1.0
 */

public class FileReader {
    public static String[] readFile(String filename) throws IOException {
        String[] result = new String[getLineCount(filename)];

        File file = new File(filename);
        BufferedReader bis = new BufferedReader(new java.io.FileReader(file));

        String line;
        int c = 0;
        while ((line = bis.readLine()) != null) {
            result[c] = line;
            c++;
        }

        bis.close();

        return result;
    }

    public static int getLineCount(String file) throws IOException {
        Path path = Paths.get(file);
        return (int)Files.lines(path).count();
    }
}