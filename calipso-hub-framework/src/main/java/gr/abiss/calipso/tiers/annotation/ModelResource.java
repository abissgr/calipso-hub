/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.tiers.annotation;

import java.io.Serializable;
import java.lang.annotation.*;


//Make the annotation available at runtime:
@Retention(RetentionPolicy.RUNTIME)
//Allow to use only on types:
@Target(ElementType.TYPE)
@Documented
/**
 * Marks a Model ato generate SCRUD tiers for
 */
public @interface ModelResource {

    Class<? extends Serializable> idClass() default String.class;
    String path() default "";
}
