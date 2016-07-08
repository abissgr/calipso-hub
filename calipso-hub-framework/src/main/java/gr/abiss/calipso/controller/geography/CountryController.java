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
package gr.abiss.calipso.controller.geography;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import gr.abiss.calipso.model.geography.Country;
import gr.abiss.calipso.service.geography.CountryService;
import gr.abiss.calipso.tiers.controller.AbstractModelController;
import io.swagger.annotations.Api;

@Controller
@Api(tags = "Countries", description = "Operations about countries")
@RequestMapping(value = "/api/rest/countries", 
	produces = { "application/json", "application/xml" })
public class CountryController extends AbstractModelController<Country, String, CountryService> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);
    
}
