package gr.abiss.calipso.test;


import static io.restassured.RestAssured.get;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;

/**
 * Generates static swagger docs 
 */
public class SwaggerStaticExporterIT extends AbstractControllerIT {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerStaticExporterIT.class);

	@Test(priority = 10, description = "Test the swagger endpoint and create the static files documentation")
	public void testCreateStaticDocs() throws Exception {
		try{
			// get swagger document
			String json = get("/calipso/v2/api-docs").asString();
			
			// set output folder
			Path outputDirectory = Paths.get("target/swagger2Markup");
			
			Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
			        .withMarkupLanguage(MarkupLanguage.MARKDOWN)
			        .withOutputLanguage(Language.EN) 
			        .withPathsGroupedBy(GroupBy.TAGS)
			        .build();
			Swagger2MarkupConverter.from(json)
			        .withConfig(config)
			        .build()
			        .toFolder(outputDirectory); 
		}
		catch(Exception e){
			LOGGER.error("Failed generating static docs", e);
			throw e;
		}
	}

}