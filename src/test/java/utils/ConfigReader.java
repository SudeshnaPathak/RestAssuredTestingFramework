package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public class ConfigReader {

    private static ConfigReader configReader;
    private final Properties properties;

    private ConfigReader() {
        FileInputStream fis;
        try {
            fis = new FileInputStream("src/test/resources/configData/configData.properties");
            properties = new Properties();
            try {
                properties.load(fis);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConfigReader getInstance() {
        if (configReader == null) {
            configReader = new ConfigReader();
        }
        return configReader;
    }

    public String getBaseUrl() {
        String baseUrl = properties.getProperty("baseUrl");
        if (baseUrl != null) return baseUrl;
        else throw new RuntimeException("base_Url not specified in the Configuration.properties file.");
    }

    public UUID getUserID() {
        try {
            return UUID.fromString(properties.getProperty("userId"));
        } catch (NullPointerException e) {
            throw new RuntimeException("user_id not specified in the Configuration.properties file.");
        }

    }

    public String getPassword() {
        String password = properties.getProperty("password");
        if (password != null) return password;
        else throw new RuntimeException("password not specified in the Configuration.properties file.");
    }

    public String getUserName() {
        String userName = properties.getProperty("userName");
        if (userName != null) return userName;
        else throw new RuntimeException("userName not specified in the Configuration.properties file.");
    }
}
