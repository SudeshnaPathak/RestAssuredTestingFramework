package apiEngine;

import apiEngine.Model.Requests.AddBookRequest;
import apiEngine.Model.Requests.AuthorizationRequest;
import apiEngine.Model.Requests.DeleteBookRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static utils.ReadDataFromPropertiesFile.baseUrl;
import static utils.ReadDataFromPropertiesFile.userId;

//abstract the logic of communication with the server into a separate class
public class ApiService {

    private static RequestSpecification requestSpec() {
        return given()
                .contentType("application/json")
                .baseUri(baseUrl)
                .relaxedHTTPSValidation();
    }

    public static Response authenticateUserResponse(AuthorizationRequest authorizationRequest) {
        return requestSpec()
                .body(authorizationRequest)
                .when()
                .post(Routes.generateToken());

    }

    public static Response getBooks() {
        return requestSpec()
                .when()
                .get(Routes.books());
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

    public static Response getUserAccount(String token) {
        return requestSpec()
                .auth().oauth2(token)
                .pathParam("UUID", userId)
                .when()
                .get(Routes.userAccount());
    }

}
