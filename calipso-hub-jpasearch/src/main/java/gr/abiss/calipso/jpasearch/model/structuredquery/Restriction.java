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
package gr.abiss.calipso.jpasearch.model.structuredquery;

import java.util.LinkedList;
import java.util.List;

/**
 * Used as a simple JSON DTO for structured queries
 */
public class Restriction {
	public static enum Junction {
		AND, OR;
	}
	public static enum Operator {
		EQUALS, NOT_EQUALS, LIKE, IN, BETWEEN;
	}

	private Junction junction = null;

	private Operator operator = null;

	private List<Restriction> restrictions = null;

	private String field = null;

	private List<String> values = null;

	public Junction getJunction() {
		return junction;
	}

	public void setJunction(Junction junction) {
		this.junction = junction;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public List<Restriction> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<Restriction> restrictions) {
		this.restrictions = restrictions;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Restriction addRestriction(Restriction child) {
		if (this.restrictions == null) {
			this.restrictions = new LinkedList<Restriction>();
		}
		this.restrictions.add(child);
		return this;
	}
}
