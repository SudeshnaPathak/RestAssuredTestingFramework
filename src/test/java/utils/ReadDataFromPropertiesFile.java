package utils;

import org.testng.annotations.BeforeSuite;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

public class ReadDataFromPropertiesFile {
	
	public static String userName;
	public static String password;
	public static String baseUrl;
	public static UUID userId;
	
	@BeforeSuite
	public static void fetchData() throws IOException{
		
		FileInputStream fis = new FileInputStream("src/test/resources/configData/configData.properties");
		Properties p = new Properties();
		p.load(fis);
		
		userName = p.getProperty("userName");
		password = p.getProperty("password");
		baseUrl = p.getProperty("baseUrl");
		userId = UUID.fromString(p.getProperty("userId"));
	}
}
