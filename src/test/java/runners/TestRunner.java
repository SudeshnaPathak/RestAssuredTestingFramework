package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/functionalTests"},
        glue = {"stepDefinitions"},
        monochrome = true,
        plugin = {"json:target/jsonReports/RestAssuredTests.json"},
        dryRun = false)
public class TestRunner extends AbstractTestNGCucumberTests {

}