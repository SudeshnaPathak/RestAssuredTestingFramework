package apiTests;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pojos.AddBookPojo;
import pojos.DeleteBookPojo;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//E2E Flow : get → add → delete → verify
public class E2ETests extends BaseClass{
	String baseUrl = "https://bookstore.toolsqa.com";
	String userId = "3c9a82fe-5d8a-44e2-80c6-4239d83556fa";
	String bookId;
	
	private RequestSpecification requestSpec()
	{
		return given()
			   .contentType("application/json")
			   .baseUri(baseUrl)
			   .relaxedHTTPSValidation();
	}
	
	@Test
	public void GetBooks()
	{
		Response res = requestSpec()
						.when()
						.get("/BookStore/v1/Books");
		
		res.then()
			.statusCode(200)
			.log().all();
		
		List<Map<String, String>> books = res.jsonPath().getList("books");
		Assert.assertTrue(books.size() > 0);
		int idx = (int) (Math.random() * books.size());
		bookId = books.get(idx).get("isbn");
	}
	
	@Test(dependsOnMethods = "GetBooks")
	public void AddBook()
	{
		List<Map<String, String>> collectionOfIsbns = new ArrayList<>();
		collectionOfIsbns.add(Map.of("isbn", bookId));
		AddBookPojo payload = new AddBookPojo(userId, collectionOfIsbns);
		
		requestSpec()
			.auth().oauth2(accessToken)
			.body(payload)
		.when()
			.post("/BookStore/v1/Books")
		.then()
			.statusCode(anyOf(is(400), is(201)));
	}
	
	@Test(dependsOnMethods = "AddBook")
	public void DeleteBook()
	{
		DeleteBookPojo payload = new DeleteBookPojo(bookId, userId);
		requestSpec()
			.auth().oauth2(accessToken)
			.body(payload)
       .when()
			.delete("/BookStore/v1/Book")
	   .then()
			.statusCode(204)
			.log().all();
	}
	
	@Test(dependsOnMethods = "DeleteBook")
	public void getUser()
	{
		Response res = requestSpec()
				.auth().oauth2(accessToken)
				.pathParam("UUID", userId)
		.when()
			.get("/Account/v1/User/{UUID}");
		
		res.then()
			.statusCode(200)
			.log().all();
		
		List<String> isbnList = res.jsonPath().getList("books.isbn");
		Assert.assertFalse(isbnList.contains(bookId)); //Book should not be contained in the list post deletion
	}

}
