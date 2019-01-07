package utils.settings;

import javafx.application.Platform;

import java.io.*;
import java.util.LinkedList;
import java.util.Properties;

public class Settings {

    private static Properties props;

    public static void load() {
        load("sep.properties");
    }

    public static void load(String path) {

        props = new Properties();

        File f = new File(path);

        if(!f.exists() || f.isDirectory()) {
            createDefault(path);
            return;
        }

        try {

            props.load(new BufferedReader(new FileReader(f)));

        } catch(FileNotFoundException ex) {
            //this should never happen
        } catch(IOException ex) {
            System.out.println("Failed to load config File: " + ex.getMessage());
            Platform.exit();
        }
    }

    private static void createDefault(String path) {
        File f = new File(path);
        try {
            for(Setting s : Setting.values()) {
                props.setProperty(s.getKey(), s.getDefaultValue());
            }
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
