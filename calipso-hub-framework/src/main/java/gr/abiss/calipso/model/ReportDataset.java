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
package gr.abiss.calipso.model;

import gr.abiss.calipso.model.entities.AbstractPersistable;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;

/**
 */
@MappedSuperclass
public class ReportDataset<T extends AbstractPersistable, D extends Serializable> extends AbstractPersistable {
//	Query query = em.createQuery('select new ReportDataset(p, size(p.dogs)) from Person p');
//	return (List<Report<Person, Long>>) query.list();

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDataset.class);
	
	private String label;

	private T subject;
	
	private Map<Date, D> data;
	
	public ReportDataset() {
		super();
	}

	public ReportDataset(T subject, Map<Date, D> data) {
		this();
		this.subject = subject;
		this.data = data;
	}
	public ReportDataset(T subject, D datumm, Date date) {
		this();
		this.subject = subject;
		this.data = new HashMap<Date, D>();
		this.data.put(date, datumm);
		LOGGER.info("Created dataset: " + this);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("subject", this.getSubject()).append("data", this.getData()).toString();
	}


	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ReportDataset)) {
			return false;
		}
		ReportDataset that = (ReportDataset) obj;
		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public T getSubject() {
		return subject;
	}

	public void setSubject(T subject) {
		this.subject = subject;
	}

	public Map<Date, D> getData() {
		return data;
	}

	public void setData(Map<Date, D> data) {
		this.data = data;
	}

	
	

}