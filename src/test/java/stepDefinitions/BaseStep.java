package stepDefinitions;

import apiEngine.ApiService;
import cucumber.ScenarioContext;
import cucumber.TestContext;
import lombok.Getter;
import utils.ConfigReader;

@Getter
public class BaseStep {

    private final ScenarioContext scenarioContext;
    private final ApiService apiService;
    private final ConfigReader configReader = ConfigReader.getInstance();

    public BaseStep(TestContext testContext) {
        this.scenarioContext = testContext.getScenarioContext();
        this.apiService = new ApiService(configReader.getBaseUrl());
    }

}
