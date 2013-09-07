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
