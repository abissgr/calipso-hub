/**
 * calipso-hub-utilities - A full stack, high level framework for lazy application hackers.
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
package gr.abiss.calipso.utils.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.http.converter.json.Jackson2ObjectMapperFactoryBean;

public class JsonClientJackson2ObjectMapperFactoryBean extends Jackson2ObjectMapperFactoryBean {

    public JsonClientJackson2ObjectMapperFactoryBean() {
        setIndentOutput(true);
        afterPropertiesSet();
        ObjectMapper objectMapper = getObject();
        objectMapper.registerModule(new JodaModule());
    }

}