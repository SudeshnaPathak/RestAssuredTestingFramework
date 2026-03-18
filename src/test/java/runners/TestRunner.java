package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = {"src/test/resources/functionalTests"},
        glue = {"stepDefinitions"},
        monochrome = true,
        dryRun = false)
public class TestRunner extends AbstractTestNGCucumberTests {

}