package stepDefinitions;

import apiEngine.Model.Book;
import apiEngine.Model.Requests.AddBookRequest;
import apiEngine.Model.Requests.AuthorizationRequest;
import apiEngine.Model.Requests.DeleteBookRequest;
import apiEngine.Model.Responses.TokenResponse;
import apiEngine.Model.Responses.UserAccount;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import utils.ReadDataFromPropertiesFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static utils.ReadDataFromPropertiesFile.*;

public class Steps{

    private static TokenResponse tokenResponse;
    String bookId;
    private static Response res;

    @Before
    public void setup() throws IOException {
        ReadDataFromPropertiesFile.fetchData();
    }
    private RequestSpecification requestSpec() {
        return given()
                .contentType("application/json")
                .baseUri(baseUrl)
                .relaxedHTTPSValidation();
    }

    @Given("The user is authorized")
    public void the_user_is_authorized() {

        AuthorizationRequest user = new AuthorizationRequest(userName, password);
        res = requestSpec()
                .body(user)
                .when()
                .post("/Account/v1/GenerateToken");

        res.then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("result", equalTo("User authorized successfully."))
                .log().all();

        tokenResponse = res.getBody().as(TokenResponse.class);
    }

    @Given("A list of book is available")
    public void a_list_of_book_is_available() {

        res = requestSpec()
                .when()
                .get("/BookStore/v1/Books");

        res.then()
                .statusCode(200)
                .log().all();

        List<Book> books = res.jsonPath().getList("books", Book.class);
        Assert.assertFalse(books.isEmpty());
        int idx = (int) (Math.random() * books.size());
        bookId = books.get(idx).getIsbn();
    }

    @When("User adds a book to his reading list")
    public void user_adds_a_book_to_his_reading_list() {
        List<Map<String, String>> collectionOfIsbns = new ArrayList<>();
        collectionOfIsbns.add(Map.of("isbn", bookId));
        AddBookRequest payload = new AddBookRequest(userId, collectionOfIsbns);

        res = requestSpec()
                .auth().oauth2(tokenResponse.getToken())
                .body(payload)
                .when()
                .post("/BookStore/v1/Books");

        res.then()
                .statusCode(anyOf(is(400), is(201)))
                .log().all();
    }

    @Then("The book is added")
    public void the_book_is_added() {
        int statusCode = res.getStatusCode();
        if(statusCode == 201)
        {
            Assert.assertEquals(res.jsonPath().getString("books[0].isbn"), bookId);
        }
        else if(statusCode == 400)
        {
            res.then().body("message", equalTo("ISBN already present in the User's Collection!"));
        }
    }

    @When("User removes a book from his reading list")
    public void user_removes_a_book_from_his_reading_list() {
        DeleteBookRequest payload = new DeleteBookRequest(bookId, userId);
        requestSpec()
                .auth().oauth2(tokenResponse.getToken())
                .body(payload)
                .when()
                .delete("/BookStore/v1/Book")
                .then()
                .statusCode(204)
                .log().all();
    }

    @Then("The book is removed")
    public void the_book_is_removed() {
        res = requestSpec()
                .auth().oauth2(tokenResponse.getToken())
                .pathParam("UUID", userId)
                .when()
                .get("/Account/v1/User/{UUID}");

        res.then()
                .statusCode(200)
                .log().all();

        UserAccount userAccount = res.getBody().as(UserAccount.class);
        List<Book> books = userAccount.getBooks();
        Assert.assertFalse(books.stream().anyMatch(book -> book.getIsbn().equals(bookId))); //Book should not be contained in the list post deletion

        List<String> isbnList = res.jsonPath().getList("books.isbn");
        Assert.assertFalse(isbnList.contains(bookId)); //Book should not be contained in the list post deletion
    }

}
