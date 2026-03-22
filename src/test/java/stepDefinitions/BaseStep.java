package stepDefinitions;

import cucumber.ScenarioContext;
import cucumber.TestContext;
import lombok.Getter;

@Getter
public class BaseStep {

    private final ScenarioContext scenarioContext;

    public BaseStep(TestContext testContext) {
        this.scenarioContext = testContext.getScenarioContext();
    }

}
