package stepDefinitions;

import apiEngine.ErrorResponse;
import apiEngine.IRestResponse;
import apiEngine.Model.Book;
import apiEngine.Model.Responses.AddBookResponse;
import apiEngine.Model.Responses.UserAccount;
import cucumber.TestContext;
import enums.Context;
import io.cucumber.java.en.Then;
import org.apache.http.HttpStatus;
import org.testng.Assert;

import java.util.List;

public class VerifySteps extends BaseStep {

    public VerifySteps(TestContext context) {
        super(context);
    }

    @Then("The book is added")
    public void the_book_is_added() {
        IRestResponse<AddBookResponse, ErrorResponse> iRestResponse = (IRestResponse<AddBookResponse, ErrorResponse>) getScenarioContext().getContext(Context.BOOK_ADD_RESPONSE);

        int statusCode = iRestResponse.getStatusCode();
        if (iRestResponse.isSuccessful()) {
            Assert.assertEquals(statusCode, HttpStatus.SC_CREATED, "Expected 201 Created");
            AddBookResponse addBookResponse = iRestResponse.getBody();

            Book book = (Book) getScenarioContext().getContext(Context.BOOK);
            Assert.assertEquals(addBookResponse.getBooks().get(0).getIsbn(), book.getIsbn());

        } else if (statusCode == 400) {
            ErrorResponse errorResponse = iRestResponse.getErrorBody();
            Assert.assertEquals(errorResponse.getCode(), "1210", "Unexpected error code");
            Assert.assertEquals(errorResponse.getMessage(), "ISBN already present in the User's Collection!", "Unexpected error message");
        }
    }

    @Then("The book is removed")
    public void the_book_is_removed() {

        String token = getScenarioContext().getContext(Context.ACCESS_TOKEN).toString();
        IRestResponse<UserAccount, ErrorResponse> res = getApiService().getUserAccount(token, getConfigReader().getUserID());

        Assert.assertTrue(res.isSuccessful(), "Response unsuccessful");
        Assert.assertEquals(res.getStatusCode(), HttpStatus.SC_OK, "Expected 200 OK");
        UserAccount userAccount = res.getBody();
        List<Book> books = userAccount.getBooks();

        Book book = (Book) getScenarioContext().getContext(Context.BOOK);
        Assert.assertFalse(books.stream().anyMatch(b -> b.getIsbn().equals(book.getIsbn())), "Book was not removed from the user's collection");
    }
}
