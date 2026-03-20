package apiTests;

import apiEngine.Model.Requests.AuthorizationRequest;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import utils.ReadDataFromPropertiesFile;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BaseClass extends ReadDataFromPropertiesFile{
	AuthorizationRequest user;
	String accessToken;
	
	@BeforeClass
	public void setup() {
	    RegisterUser();
	    generateToken();
	}
	
	public void RegisterUser() {
		user = new AuthorizationRequest(userName, password);
		
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
