/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/lgpl-3.0.txt
 */
package gr.abiss.calipso.jpasearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides form schema fragments for a specific field, e.g:
 * 
 * <pre>
 * &#064;FormSchemaEntry(
 * 	create = &quot;{ \&quot;validators\&quot;: [\&quot;required\&quot;, \&quot;email\&quot;] }&quot;, 
 * 	update = &quot;{ \&quot;validators\&quot;: [\&quot;required\&quot;, \&quot;email\&quot;] }&quot;, 
 * 	search = &quot;{ \&quot;validators\&quot;: [\&quot;email\&quot;] }&quot;
 * )
 * private String email;
 * </pre>
 * 
 * Documentation of the schema format can be found at <a
 * href="https://github.com/powmedia/backbone-forms‎"> powmedia/backbone-forms
 * </a>
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormSchemaEntry {
	String create() default "'Text'";

	String update() default "'Text'";

	String search() default "'Text'";

}
