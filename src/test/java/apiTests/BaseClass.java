package apiTests;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.testng.annotations.BeforeClass;

import io.restassured.response.Response;
import pojos.UserPojo;
import utils.ReadDataFromPropertiesFile;

public class BaseClass extends ReadDataFromPropertiesFile{
	UserPojo user;
	String accessToken;
	
	@BeforeClass
	public void setup() {
	    RegisterUser();
	    generateToken();
	}
	
	public void RegisterUser()
	{	
		user = new UserPojo(userName, password);
		
		Response res = given()
			.contentType("application/json")
			.baseUri(baseUrl)
			.relaxedHTTPSValidation()
			.body(user)
		.when()
			.post("/Account/v1/User");
		
		int statusCode = res.getStatusCode();

		if (statusCode == 201) {
		    System.out.println("User created successfully");
		} else if (statusCode == 409) {
		    System.out.println("User already exists");
		}
		
		res.then().log().all();
	}
	
	
	public void generateToken()
	{
		Response res = given()
				.contentType("application/json")
				.baseUri(baseUrl)
				.relaxedHTTPSValidation()
				.body(user)
			.when()
				.post("/Account/v1/GenerateToken");
		
		res.then()
		.statusCode(200)
		.body("token", notNullValue())
		.body("result", equalTo("User authorized successfully."))
		.log().all();
		
		accessToken = res.path("token");
	}
	

}
