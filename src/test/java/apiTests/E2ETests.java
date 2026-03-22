package apiTests;

import apiEngine.Model.Book;
import apiEngine.Model.ISBN;
import apiEngine.Model.Requests.AddBookRequest;
import apiEngine.Model.Requests.DeleteBookRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

//E2E Flow : get → add → delete → verify
public class E2ETests extends BaseClass{
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

//		List<Map<String, String>> books = res.jsonPath().getList("books");
		List<Book> books = res.jsonPath().getList("books", Book.class);
        Assert.assertFalse(books.isEmpty());
		int idx = (int) (Math.random() * books.size());
//		bookId = books.get(idx).get("isbn");
		bookId = books.get(idx).getIsbn();
	}
	
	@Test(dependsOnMethods = {"GetBooks"})
	public void AddBook()
	{
		List<ISBN> collectionOfIsbns = new ArrayList<>();
		collectionOfIsbns.add(new ISBN(bookId));
		AddBookRequest payload = new AddBookRequest(userId, collectionOfIsbns);
		
		requestSpec()
			.auth().oauth2(accessToken)
			.body(payload)
		.when()
			.post("/BookStore/v1/Books")
		.then()
			.statusCode(anyOf(is(400), is(201)))
			.log().all();
	}
	
	@Test(dependsOnMethods = {"AddBook"})
	public void DeleteBook()
	{
		DeleteBookRequest payload = new DeleteBookRequest(bookId, userId);
		requestSpec()
			.auth().oauth2(accessToken)
			.body(payload)
       .when()
			.delete("/BookStore/v1/Book")
	   .then()
			.statusCode(204)
			.log().all();
	}
	
	@Test(dependsOnMethods = {"DeleteBook"})
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
