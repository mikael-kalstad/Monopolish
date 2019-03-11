package com.teamfour.monopolish.util;

import java.io.IOException;

/**
 * This class is used for retrieving settings from the game.properties file
 *
 * @author      Eirik Hemstad
 * @version     1.0
 */

public class Properties {
    /**
     * Get the database url from game.properties
     * @return String with database url
     * @throws IOException
     */
    public static String getDatabaseURL() throws IOException {
        String[] fileLines = FileReader.readFile("game.properties");

        for (int i = 0; i < fileLines.length; i++) {
            if(fileLines[i].contains("db_url")) {
                return fileLines[i].split("=")[1];
            }
        }

        return null;
    }

    /**
     * Get the database username from game.properties
     * @return String with database username
     * @throws IOException
     */
    public static String getDatabaseUser() throws IOException {
        String[] fileLines = FileReader.readFile("game.properties");

        for (int i = 0; i < fileLines.length; i++) {
            if(fileLines[i].contains("db_user")) {
                return fileLines[i].split("=")[1];
            }
        }

        return null;
    }

    /**
     * Get the database password from game.properties
     * @return String with database password
     * @throws IOException
     */
    public static String getDatabasePassword() throws IOException {
        String[] fileLines = FileReader.readFile("game.properties");

        for (int i = 0; i < fileLines.length; i++) {
            if(fileLines[i].contains("db_pass")) {
                return fileLines[i].split("=")[1];
            }
        }

        return null;
    }
}
