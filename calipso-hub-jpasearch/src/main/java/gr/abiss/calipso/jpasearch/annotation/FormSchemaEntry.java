/**
 *
 *
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
