package com.teamfour.monopolish.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class handles all general file reading.
 *
 * @author      Eirik Hemstad
 * @version     1.0
 */

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class FileReader {

    /**
     * Gets a string array read from a file
     * @param filename Filename of the file to be read
     * @return String array with all lines from file
     * @throws IOException
     */
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

    @Test
    public void TestSomething() {
        System.out.println("Yes");
        try {
            assertTrue(readFile("game.properties")[0].equals(""));
        } catch (IOException e) {
            System.out.println("Oh no");
        }
    }
}