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
import gr.abiss.calipso.model.types.TimeUnit;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class ReportDataSet {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDataSet.class);
	private static FastDateFormat dayFormat = FastDateFormat.getInstance("yyyy-MM-dd");
	private static FastDateFormat monthFormat = FastDateFormat.getInstance("yyyy-MM");
	private Object subject;
	
	
	private List<ReportDataEntry> entries;
	
	private Date dateFrom;
	private Date dateTo;
	private TimeUnit timeUnit;
	private Integer dateIndex;
	private Map<String, Integer> keyIndexes;

	/**
	 * 
	 * @param dateFrom the report starting date
	 * @param dateTo the report ending date
	 * @param timeUnit the time interval used in the report (daily, monthly etc.)
	 * @param keys the report keys (having removed the first two: subject and date)
	 * @param objects 
	 * @return
	 */

	private ReportDataSet(){
	}
	
	public ReportDataSet(Object subject, Date dateFrom, Date dateTo, TimeUnit timeUnit, Map<String, Integer> keyIndexes) {
		this(subject, dateFrom, dateTo, timeUnit, keyIndexes , null);
	}

	public ReportDataSet(Object subject, Date dateFrom, Date dateTo, TimeUnit timeUnit, Map<String, Integer> keyIndexes, Map<String, Serializable> defaultDataEntry) {

		// init members
		this.subject = subject;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.timeUnit = timeUnit;
		this.keyIndexes = keyIndexes;
		
		initDefaults(dateFrom, dateTo, timeUnit, keyIndexes, defaultDataEntry);
	}

	protected void initDefaults(Date dateFrom, Date dateTo, TimeUnit timeUnit,
			Map<String, Integer> keyIndexes,
			Map<String, Serializable> defaultDataEntry) {
		// init default record values, to be used in missing date slots to ensure regular records
		if(defaultDataEntry == null){
			defaultDataEntry = new HashMap<String, Serializable>();
			for(String key : keyIndexes.keySet()){
				defaultDataEntry.put(key, new Integer(0));
			}
		}
		
		// init default values for all dates in range to ensure regular records
		Calendar start = Calendar.getInstance();
		// get start of day
		start.setTime(DateUtils.truncate(dateFrom, Calendar.DATE));
		Calendar end = Calendar.getInstance();
		// get end of day
		end.setTime(DateUtils.addMilliseconds(DateUtils.ceiling(dateTo, Calendar.DATE), -1));
		for (Date date = start.getTime(); start.before(end); start.add(timeUnit.toCalendarUnit(), 1), date = start.getTime()) {
		   this.addEntry(date, defaultDataEntry);
		}
	}
	

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("subject", this.getSubject()).append("data", this.getEntries()).toString();
	}


	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ReportDataSet)) {
			return false;
		}
		ReportDataSet that = (ReportDataSet) obj;
		return new EqualsBuilder()
	        .appendSuper(super.equals(obj))
	        .append(this.getSubject(), that.getSubject())
	        //.append(this.getData(), that.getData())
	        .isEquals();
	}



	public void addEntry(Date date, Map<String, Serializable> datum) {
		String label = null;
		if(this.timeUnit.equals(TimeUnit.DAY)){
			label = dayFormat.format(date);
		}
		else{
			label = monthFormat.format(date);
		}
		this.addEntry(new ReportDataEntry(label, datum));
	}
	
	public void addEntry(ReportDataEntry entry) {
		if(this.entries == null){
			this.entries = new LinkedList<ReportDataEntry>();
		}

		LOGGER.info("Adding entry: " + ReflectionToStringBuilder.toString(entry));
		this.entries.add(entry);
	}

	public Object getSubject() {
		return subject;
	}

	public void setSubject(Object subject) {
		this.subject = subject;
	}

	public List<ReportDataEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ReportDataEntry> entries) {
		this.entries = entries;
	}
	
	

}