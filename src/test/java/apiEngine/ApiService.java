package apiEngine;

import apiEngine.Model.Requests.AddBookRequest;
import apiEngine.Model.Requests.AuthorizationRequest;
import apiEngine.Model.Requests.DeleteBookRequest;
import apiEngine.Model.Responses.AddBookResponse;
import apiEngine.Model.Responses.BooksResponse;
import apiEngine.Model.Responses.TokenResponse;
import apiEngine.Model.Responses.UserAccount;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;

import static io.restassured.RestAssured.given;

//abstract the logic of communication with the server into a separate class
public class ApiService {

    private final String baseUrl;

    public ApiService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private RequestSpecification requestSpec() {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl)
                .relaxedHTTPSValidation();
    }

    public IRestResponse<TokenResponse, ErrorResponse> authenticateUserResponse(AuthorizationRequest authorizationRequest) {
        Response res = requestSpec()
                .body(authorizationRequest)
                .when()
                .post(Routes.generateToken());
        return new RestResponse<>(TokenResponse.class, ErrorResponse.class, res);
    }

    public IRestResponse<BooksResponse, ErrorResponse> getBooks() {
        Response res = requestSpec()
                .when()
                .get(Routes.books());
        return new RestResponse<>(BooksResponse.class, ErrorResponse.class, res);
    }

    public IRestResponse<AddBookResponse, ErrorResponse> addBook(AddBookRequest addBookRequest, String token) {
        Response res = requestSpec()
                .auth().oauth2(token)
                .body(addBookRequest)
                .when()
                .post(Routes.books());
        return new RestResponse<>(AddBookResponse.class, ErrorResponse.class, res);
    }

    public Response removeBook(DeleteBookRequest deleteBookRequest, String token) {
        return requestSpec()
                .auth().oauth2(token)
                .body(deleteBookRequest)
                .when()
                .delete(Routes.book());
    }

    public IRestResponse<UserAccount, ErrorResponse> getUserAccount(String token, UUID userId) {
        Response res = requestSpec()
                .auth().oauth2(token)
                .pathParam("UUID", userId)
                .when()
                .get(Routes.userAccount());

        return new RestResponse<>(UserAccount.class, ErrorResponse.class, res);
    }
}
