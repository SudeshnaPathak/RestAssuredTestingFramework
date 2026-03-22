package stepDefinitions;

import apiEngine.ApiService;
import apiEngine.ErrorResponse;
import apiEngine.IRestResponse;
import apiEngine.Model.Requests.AuthorizationRequest;
import apiEngine.Model.Responses.TokenResponse;
import cucumber.TestContext;
import enums.Context;
import io.cucumber.java.en.Given;
import org.apache.http.HttpStatus;
import org.testng.Assert;

import java.time.LocalDateTime;

import static utils.ReadDataFromPropertiesFile.password;
import static utils.ReadDataFromPropertiesFile.userName;

public class AuthStep extends BaseStep {

    public AuthStep(TestContext testContext) {
        super(testContext);
    }

    @Given("The user is authorized")
    public void the_user_is_authorized() {

        AuthorizationRequest user = new AuthorizationRequest(userName, password);
        IRestResponse<TokenResponse, ErrorResponse> res = ApiService.authenticateUserResponse(user);

        Assert.assertTrue(res.isSuccessful(), "Response unsuccessful");
        Assert.assertEquals(res.getStatusCode(), HttpStatus.SC_OK, "Expected 200 OK");

        TokenResponse tokenResponse = res.getBody();
        Assert.assertNotNull(tokenResponse.getToken(), "Token is null");
        Assert.assertEquals(tokenResponse.getStatus(), "Success", "Unexpected status");
        Assert.assertEquals(tokenResponse.getResult(), "User authorized successfully.", "Unexpected result message");

        LocalDateTime expiry = tokenResponse.getExpires();
        Assert.assertNotNull(expiry, "Expiry time should not be null");
        Assert.assertTrue(expiry.isAfter(LocalDateTime.now()), "Token is expired");

        getScenarioContext().setContext(Context.ACCESS_TOKEN, tokenResponse.getToken());
    }
}
