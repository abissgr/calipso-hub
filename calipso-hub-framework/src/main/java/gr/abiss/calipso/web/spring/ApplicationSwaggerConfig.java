/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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