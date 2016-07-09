package gr.abiss.calipso.test;


import static io.restassured.RestAssured.get;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import static org.asciidoctor.Asciidoctor.Factory.create;

import org.asciidoctor.AsciiDocDirectoryWalker;
import org.asciidoctor.Asciidoctor;

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

			// create markdown
			this.makeDocs(json, Paths.get("target/swagger2md"), MarkupLanguage.MARKDOWN); 
			// create confluence
			this.makeDocs(json, Paths.get("target/swagger2confluence"), MarkupLanguage.CONFLUENCE_MARKUP); 
			// create confluence
			this.makeDocs(json, Paths.get("target/swagger2asciidoc"), MarkupLanguage.ASCIIDOC); 
			
			// asciidoc to HTML
//			Asciidoctor asciidoctor = create();
//			String[] result = asciidoctor.convertDirectory(
//				    new AsciiDocDirectoryWalker("target/swagger2asciidoc"),
//				    new HashMap<String, Object>());
			
		}
		catch(Exception e){
			LOGGER.error("Failed generating static docs", e);
			throw e;
		}
	}

	/**
	 * Create documentation from the given swagger JSON input
	 * @param json the swagger JSON input
	 * @param outputDirectory the directory to create the docs into
	 * @param markupLanguage the markup language to use	
	 */ 
	private void makeDocs(String json, Path outputDirectory, MarkupLanguage markupLanguage) {
		// config
		Swagger2MarkupConfig configMarkdown = new Swagger2MarkupConfigBuilder()
		        .withMarkupLanguage(markupLanguage)
		        .withOutputLanguage(Language.EN) 
		        .withPathsGroupedBy(GroupBy.TAGS)
		        .build();
		
		// create docs
		Swagger2MarkupConverter.from(json)
		        .withConfig(configMarkdown)
		        .build()
		        .toFolder(outputDirectory);
	}

}