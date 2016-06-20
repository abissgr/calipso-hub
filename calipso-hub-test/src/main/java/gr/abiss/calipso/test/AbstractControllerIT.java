package gr.abiss.calipso.test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.factory.Jackson2ObjectMapperFactory;

/**
 * Base class for rest-assured based controller integration testing
 */
@SuppressWarnings("unused")
public class AbstractControllerIT {

	protected static final String JSON_UTF8 = "application/json; charset=UTF-8";
	// configure the underlying Jackson object mapper as needed
	static {
		RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
				new ObjectMapperConfig().jackson2ObjectMapperFactory(new Jackson2ObjectMapperFactory() {
					@Override
					public ObjectMapper create(Class aClass, String s) {
						ObjectMapper objectMapper = new ObjectMapper();
						// support joda classes<->JSON
						objectMapper.registerModule(new JodaModule());
						// ignore unknown properties
						objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
						return objectMapper;
					}
				}));
	}
}
