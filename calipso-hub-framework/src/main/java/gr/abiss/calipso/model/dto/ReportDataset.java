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
package gr.abiss.calipso.model.dto;

import gr.abiss.calipso.model.entities.AbstractPersistable;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;

/**
 */
public class ReportDataset {
//	Query query = em.createQuery('select new ReportDataset(p, size(p.dogs)) from Person p');
//	return (List<Report<Person, Long>>) query.list();

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDataset.class);
	private static FastDateFormat dateFormat = FastDateFormat.getInstance("yyyyMMdd");
	private String label;

	private Long count;
	private AbstractPersistable subject;
	
	private Map<Date, Serializable> data;
	
	public ReportDataset() {
		super();
	}

	public ReportDataset(Object subject, Long count, Serializable datum, String dateString) throws ParseException {
		this((AbstractPersistable) subject, count, datum, dateFormat.parse(dateString));
		LOGGER.info("Constructor 2");
	}
	
	public ReportDataset(Object subject, Long count, Serializable datum, Date date) {
		this((AbstractPersistable) subject, count, datum, date);
		LOGGER.info("Constructor 3");
	}
	
	public ReportDataset(AbstractPersistable subject, Long count, Serializable datum, Date date) {
		this();
		LOGGER.info("Constructor 4");
		LOGGER.info("Crating dataset, subject: " + subject + ", datum: " + datum + ", date:" + date);
		this.count = count;
		this.subject = subject;
		this.data = new HashMap<Date, Serializable>();
		this.data.put(date, datum);
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
		return null == this.getData() ? false : this.getData().equals(that.getData());
	}
	
	

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public AbstractPersistable getSubject() {
		return subject;
	}

	public void setSubject(AbstractPersistable subject) {
		this.subject = subject;
	}

	public Map<Date, Serializable> getData() {
		return data;
	}

	public void setData(Map<Date, Serializable> data) {
		this.data = data;
	}

}