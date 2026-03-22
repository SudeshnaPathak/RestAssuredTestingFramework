package stepDefinitions;

import apiEngine.ApiService;
import apiEngine.IRestResponse;
import apiEngine.Model.Book;
import apiEngine.Model.Requests.AddBookRequest;
import apiEngine.Model.Requests.AuthorizationRequest;
import apiEngine.Model.Requests.DeleteBookRequest;
import apiEngine.Model.Responses.BooksResponse;
import apiEngine.Model.Responses.TokenResponse;
import apiEngine.Model.Responses.UserAccount;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static utils.ReadDataFromPropertiesFile.*;

public class Steps {

    private static TokenResponse tokenResponse;
    String bookId;
    private static Response res;

    @Given("The user is authorized")
    public void the_user_is_authorized() {

        AuthorizationRequest user = new AuthorizationRequest(userName, password);
        IRestResponse<TokenResponse> res = ApiService.authenticateUserResponse(user);

        Assert.assertTrue(res.isSuccessful(), "Response unsuccessful");
        Assert.assertEquals(res.getStatusCode(), 200, "Expected 200 OK");

        tokenResponse = res.getBody();
        Assert.assertNotNull(tokenResponse.getToken(), "Token is null");
        Assert.assertEquals(tokenResponse.getStatus(), "Success", "Unexpected status");
        Assert.assertEquals(tokenResponse.getResult(), "User authorized successfully.", "Unexpected result message");

        LocalDateTime expiry = tokenResponse.getExpires();
        Assert.assertNotNull(expiry, "Expiry time should not be null");
        Assert.assertTrue(expiry.isAfter(LocalDateTime.now()), "Token is expired");
    }

    @Given("A list of book is available")
    public void a_list_of_book_is_available() {

        IRestResponse<BooksResponse> res = ApiService.getBooks();
        Assert.assertTrue(res.isSuccessful(), "Response unsuccessful");
        Assert.assertEquals(res.getStatusCode(), 200, "Expected 200 OK");

        List<Book> books = res.getBody().getBooks();
        Assert.assertFalse(books.isEmpty());
        int idx = (int) (Math.random() * books.size());
        bookId = books.get(idx).getIsbn();
    }

    @When("User adds a book to his reading list")
    public void user_adds_a_book_to_his_reading_list() {

        List<Map<String, String>> collectionOfIsbns = new ArrayList<>();
        collectionOfIsbns.add(Map.of("isbn", bookId));
        AddBookRequest payload = new AddBookRequest(userId, collectionOfIsbns);

        res = ApiService.addBook(payload, tokenResponse.getToken());
        res.then()
                .statusCode(anyOf(is(400), is(201)))
                .log().all();
    }

    @Then("The book is added")
    public void the_book_is_added() {
        int statusCode = res.getStatusCode();
        if (statusCode == 201) {
            Assert.assertEquals(res.jsonPath().getString("books[0].isbn"), bookId);
        } else if (statusCode == 400) {
            res.then().body("message", equalTo("ISBN already present in the User's Collection!"));
        }
    }

    @When("User removes a book from his reading list")
    public void user_removes_a_book_from_his_reading_list() {
        DeleteBookRequest payload = new DeleteBookRequest(bookId, userId);
        res = ApiService.removeBook(payload, tokenResponse.getToken());
        Assert.assertEquals(res.getStatusCode(), 204, "Expected 204 No Content");
    }

    @Then("The book is removed")
    public void the_book_is_removed() {

        IRestResponse<UserAccount> res = ApiService.getUserAccount(tokenResponse.getToken());
        Assert.assertTrue(res.isSuccessful(), "Response unsuccessful");
        Assert.assertEquals(res.getStatusCode(), 200, "Expected 200 OK");
        UserAccount userAccount = res.getBody();
        List<Book> books = userAccount.getBooks();
        Assert.assertFalse(books.stream().anyMatch(book -> book.getIsbn().equals(bookId)));
    }

}
