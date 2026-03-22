package cucumber;

import apiEngine.ApiService;
import enums.Context;
import lombok.Getter;
import utils.ConfigReader;

import static utils.ReadDataFromPropertiesFile.userId;

@Getter
public class TestContext {
    private final ScenarioContext scenarioContext;
    private final ConfigReader configReader = ConfigReader.getInstance();
    private final ApiService apiService;


    public TestContext() {
        this.scenarioContext = new ScenarioContext();
        scenarioContext.setContext(Context.USER_ID, userId);
        this.apiService = new ApiService(configReader.getBaseUrl());
    }
}
