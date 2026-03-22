package apiEngine;

import io.restassured.response.Response;

public interface IRestResponse<T, E> {

    T getBody();

    E getErrorBody();

    String getContent();

    int getStatusCode();

    boolean isSuccessful();

    String getStatusDescription();

    Response getResponse();

    Exception getException();
}
