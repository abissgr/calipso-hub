package gr.abiss.calipso.web.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import gr.abiss.calipso.utils.ConfigurationFactory;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableWebMvc
@Configuration
public class ApplicationSwaggerConfig {
	
//	@Bean
//	public Docket customImplementation() {
//		return new Docket(DocumentationType.SPRING_WEB).apiInfo(apiInfo());
//	}
	@Bean
	public Docket customImplementation() {
	    return new Docket(DocumentationType.SWAGGER_2)
	        .select()
	        .apis(RequestHandlerSelectors.any())
	        .build()
	        .apiInfo(apiInfo());
	}
	
	@Bean
    public UiConfiguration uiConfig() {
        return UiConfiguration.DEFAULT;
    }
	
	private ApiInfo apiInfo() {
		org.apache.commons.configuration.Configuration config = ConfigurationFactory.getConfiguration();
		String appName = config.getString(ConfigurationFactory.APP_NAME);
		String appVersion = config.getString(ConfigurationFactory.APP_VERSION);
		return new ApiInfo(appName + " API Reference " + appVersion,
				"Automatically-generated documentation based on [Swagger](http://swagger.io/) and created by [Springfox](http://springfox.github.io/springfox/).",
				appVersion, "urn:tos", "", "API License: GNU Affero General Public License v3",
				"https://www.gnu.org/licenses/agpl-3.0.html");
	}
}
