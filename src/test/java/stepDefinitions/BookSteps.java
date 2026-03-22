package stepDefinitions;

import apiEngine.ErrorResponse;
import apiEngine.IRestResponse;
import apiEngine.Model.Book;
import apiEngine.Model.ISBN;
import apiEngine.Model.Requests.AddBookRequest;
import apiEngine.Model.Requests.DeleteBookRequest;
import apiEngine.Model.Responses.AddBookResponse;
import apiEngine.Model.Responses.BooksResponse;
import cucumber.TestContext;
import enums.Context;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static utils.ReadDataFromPropertiesFile.userId;

public class BookSteps extends BaseStep {

    public BookSteps(TestContext testContext) {
        super(testContext);
    }

    @Given("A list of book is available")
    public void a_list_of_book_is_available() {

        IRestResponse<BooksResponse, ErrorResponse> res = getApiService().getBooks();
        Assert.assertTrue(res.isSuccessful(), "Response unsuccessful");
        Assert.assertEquals(res.getStatusCode(), HttpStatus.SC_OK, "Expected 200 OK");

        List<Book> books = res.getBody().getBooks();
        Assert.assertFalse(books.isEmpty());
        int idx = (int) (Math.random() * books.size());
        Book book = books.get(idx);
        getScenarioContext().setContext(Context.BOOK, book);
    }

    @When("User adds a book to his reading list")
    public void user_adds_a_book_to_his_reading_list() {

        String token = getScenarioContext().getContext(Context.ACCESS_TOKEN).toString();

        List<ISBN> collectionOfIsbns = new ArrayList<>();
        Book book = (Book) getScenarioContext().getContext(Context.BOOK);
        collectionOfIsbns.add(new ISBN(book.getIsbn()));

        AddBookRequest payload = new AddBookRequest(userId, collectionOfIsbns);

        IRestResponse<AddBookResponse, ErrorResponse> iRestResponse = getApiService().addBook(payload, token);
        getScenarioContext().setContext(Context.BOOK_ADD_RESPONSE, iRestResponse);
    }

    @When("User removes a book from his reading list")
    public void user_removes_a_book_from_his_reading_list() {

        String token = getScenarioContext().getContext(Context.ACCESS_TOKEN).toString();
        Book book = (Book) getScenarioContext().getContext(Context.BOOK);
        DeleteBookRequest payload = new DeleteBookRequest(book.getIsbn(), userId);

        Response res = getApiService().removeBook(payload, token);

        Assert.assertEquals(res.getStatusCode(), HttpStatus.SC_NO_CONTENT, "Expected 204 No Content");
    }
}
