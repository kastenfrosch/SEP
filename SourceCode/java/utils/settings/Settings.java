package utils.settings;

import javafx.application.Platform;

import java.io.*;
import java.util.*;

public class Settings {

    private static Properties props;

    public static void load() {
        load("sep.properties");
    }

    public static void load(String path) {

        props = new Properties();

        File f = new File(path);

        try {
            props.load(new BufferedReader(new FileReader(f)));

            for(Setting s : Setting.values()) {
                if(!props.containsKey(s)) {
                    insertMissingFields(path);
                }
            }

        } catch(FileNotFoundException ex) {
            insertMissingFields(path);
        } catch(IOException ex) {
            System.out.println("Failed to load config File: " + ex.getMessage());
            Platform.exit();
        }
    }

    private static void insertMissingFields(String path) {
        try {
            for(Setting s : Setting.values()) {
                if(!props.containsKey(s.getKey())) {
                    props.setProperty(s.getKey(), s.getDefaultValue());
                }
            }
            File f = new File(path);
            props.store(new BufferedWriter(new FileWriter(f)), null);
        } catch(IOException ex) {
            System.out.println(ex.getMessage());
            Platform.exit();
        }
    }

    public static String get(Setting key) {
        return props.getProperty(key.getKey());
    }
}
