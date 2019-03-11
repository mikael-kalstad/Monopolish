package com.teamfour.monopolish.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void TestReadFile() {
        try {
            assertEquals(3, readFile("game.properties").length);
            assertTrue(readFile("game.properties")[0].equals("db_url=jdbc:mysql://mysql.stud.iie.ntnu.no:3306/eirikhem"));
            assertTrue(readFile("game.properties")[1].equals("db_user="));
            assertTrue(readFile("game.properties")[2].equals("db_pass="));
        } catch (IOException e) {
            System.out.println("Oh no");
        }
    }
}