package apiEngine;

import apiEngine.Model.Requests.AddBookRequest;
import apiEngine.Model.Requests.AuthorizationRequest;
import apiEngine.Model.Requests.DeleteBookRequest;
import apiEngine.Model.Responses.BooksResponse;
import apiEngine.Model.Responses.TokenResponse;
import apiEngine.Model.Responses.UserAccount;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static utils.ReadDataFromPropertiesFile.baseUrl;
import static utils.ReadDataFromPropertiesFile.userId;

//abstract the logic of communication with the server into a separate class
public class ApiService {

    private static RequestSpecification requestSpec() {
        return given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl)
                .relaxedHTTPSValidation();
    }

    public static IRestResponse<TokenResponse> authenticateUserResponse(AuthorizationRequest authorizationRequest) {
        Response res = requestSpec()
                .body(authorizationRequest)
                .when()
                .post(Routes.generateToken());
        return new RestResponse<>(TokenResponse.class, res);
    }

    public static IRestResponse<BooksResponse> getBooks() {
        Response res = requestSpec()
                .when()
                .get(Routes.books());
        return new RestResponse<>(BooksResponse.class, res);
    }

    public static Response addBook(AddBookRequest addBookRequest, String token) {
        return requestSpec()
                .auth().oauth2(token)
                .body(addBookRequest)
                .when()
                .post(Routes.books());

    }

    public static Response removeBook(DeleteBookRequest deleteBookRequest, String token) {
        return requestSpec()
                .auth().oauth2(token)
                .body(deleteBookRequest)
                .when()
                .delete(Routes.book());
    }

    public static IRestResponse<UserAccount> getUserAccount(String token) {
        Response res = requestSpec()
                .auth().oauth2(token)
                .pathParam("UUID", userId)
                .when()
                .get(Routes.userAccount());

        return new RestResponse<>(UserAccount.class, res);
    }

}
