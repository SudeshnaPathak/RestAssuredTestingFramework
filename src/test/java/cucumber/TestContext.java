package cucumber;

import enums.Context;
import lombok.Getter;

import static utils.ReadDataFromPropertiesFile.userId;

@Getter
public class TestContext {
    private final ScenarioContext scenarioContext;

    public TestContext() {
        this.scenarioContext = new ScenarioContext();
        scenarioContext.setContext(Context.USER_ID, userId);
    }
}
