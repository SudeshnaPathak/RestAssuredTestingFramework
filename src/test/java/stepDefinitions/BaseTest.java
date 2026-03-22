package stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import utils.ReadDataFromPropertiesFile;

import java.io.IOException;

import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

public class BaseTest {
    @Before
    public void setup() throws IOException {
        RestAssured.config = RestAssured.config()
                .objectMapperConfig(
                        objectMapperConfig().jackson2ObjectMapperFactory(
                                (cls, charset) -> {
                                    ObjectMapper mapper = new ObjectMapper();
                                    mapper.registerModule(new JavaTimeModule());
                                    return mapper;
                                }
                        )
                );

        ReadDataFromPropertiesFile.fetchData();
    }
}
