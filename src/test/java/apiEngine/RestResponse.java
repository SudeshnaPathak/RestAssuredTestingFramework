package apiEngine;

import Exceptions.DeserializationException;
import io.restassured.response.Response;

public class RestResponse<T, E> implements IRestResponse<T, E> {

    private final Class<T> responseType;
    private final Class<E> errorType;
    private final Response response;
    private Exception exception;

    public RestResponse(Class<T> responseType, Class<E> errorType, Response response) {
        this.responseType = responseType;
        this.errorType = errorType;
        this.response = response;
    }

    @Override
    public T getBody() {
        try {
            return response.getBody().as(responseType);
        } catch (Exception e) {
            this.exception = e;
            throw new DeserializationException("Failed to deserialize response body to " + responseType.getSimpleName(), e);
        }
    }

    @Override
    public E getErrorBody() {
        try {
            return response.getBody().as(errorType);
        } catch (Exception e) {
            this.exception = e;
            throw new DeserializationException("Failed to deserialize error body", e);
        }
    }

    @Override
    public String getContent() {
        return response.getBody().asString();
    }

    @Override
    public int getStatusCode() {
        return response.getStatusCode();
    }

    @Override
    public boolean isSuccessful() {
        int statusCode = response.getStatusCode();
        return statusCode >= 200 && statusCode < 400;
    }

    @Override
    public String getStatusDescription() {
        return response.getStatusLine();
    }

    @Override
    public Response getResponse() {
        return response;
    }

    @Override
    public Exception getException() {
        return exception;
    }
}
