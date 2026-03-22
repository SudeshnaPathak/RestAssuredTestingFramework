package stepDefinitions;

import apiEngine.ApiService;
import apiEngine.ErrorResponse;
import apiEngine.IRestResponse;
import apiEngine.Model.Book;
import apiEngine.Model.Requests.AddBookRequest;
import apiEngine.Model.Requests.AuthorizationRequest;
import apiEngine.Model.Requests.DeleteBookRequest;
import apiEngine.Model.Responses.AddBookResponse;
import apiEngine.Model.Responses.BooksResponse;
import apiEngine.Model.Responses.TokenResponse;
import apiEngine.Model.Responses.UserAccount;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.ReadDataFromPropertiesFile.*;

public class Steps {

    private static TokenResponse tokenResponse;
    private static String bookId;
    private static IRestResponse<AddBookResponse, ErrorResponse> iRestResponse;

    @Given("The user is authorized")
    public void the_user_is_authorized() {

        AuthorizationRequest user = new AuthorizationRequest(userName, password);
        IRestResponse<TokenResponse, ErrorResponse> res = ApiService.authenticateUserResponse(user);

        Assert.assertTrue(res.isSuccessful(), "Response unsuccessful");
        Assert.assertEquals(res.getStatusCode(), HttpStatus.SC_OK, "Expected 200 OK");

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

        IRestResponse<BooksResponse, ErrorResponse> res = ApiService.getBooks();
        Assert.assertTrue(res.isSuccessful(), "Response unsuccessful");
        Assert.assertEquals(res.getStatusCode(), HttpStatus.SC_OK, "Expected 200 OK");

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
        iRestResponse = ApiService.addBook(payload, tokenResponse.getToken());
    }

    @Then("The book is added")
    public void the_book_is_added() {
        int statusCode = iRestResponse.getStatusCode();
        if (iRestResponse.isSuccessful()) {
            Assert.assertEquals(statusCode, HttpStatus.SC_CREATED, "Expected 201 Created");
            AddBookResponse addBookResponse = iRestResponse.getBody();
            Assert.assertEquals(addBookResponse.getBooks().get(0).get("isbn"), bookId);
        } else if (statusCode == 400) {
            ErrorResponse errorResponse = iRestResponse.getErrorBody();
            Assert.assertEquals(errorResponse.getCode(), "1210", "Unexpected error code");
            Assert.assertEquals(errorResponse.getMessage(), "ISBN already present in the User's Collection!", "Unexpected error message");
        }
    }

    @When("User removes a book from his reading list")
    public void user_removes_a_book_from_his_reading_list() {
        DeleteBookRequest payload = new DeleteBookRequest(bookId, userId);
        Response res = ApiService.removeBook(payload, tokenResponse.getToken());
        Assert.assertEquals(res.getStatusCode(), HttpStatus.SC_NO_CONTENT, "Expected 204 No Content");
    }

    @Then("The book is removed")
    public void the_book_is_removed() {

        IRestResponse<UserAccount, ErrorResponse> res = ApiService.getUserAccount(tokenResponse.getToken());
        Assert.assertTrue(res.isSuccessful(), "Response unsuccessful");
        Assert.assertEquals(res.getStatusCode(), HttpStatus.SC_OK, "Expected 200 OK");
        UserAccount userAccount = res.getBody();
        List<Book> books = userAccount.getBooks();
        Assert.assertFalse(books.stream().anyMatch(book -> book.getIsbn().equals(bookId)));
    }

}
