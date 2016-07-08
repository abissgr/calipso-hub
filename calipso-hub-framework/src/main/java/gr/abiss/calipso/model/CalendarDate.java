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
package gr.abiss.calipso.model;

import java.util.Date;

import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.joda.time.LocalDate;
import org.springframework.data.domain.Persistable;

/**
 * Represents a row in the calendar table
 * @author manos
 */
@Entity
@Table(name = "calendar_table")
public class CalendarDate implements Persistable<Date> {
	
	@Id
	@Column(name = "id", nullable = false, unique = true)
    @Temporal(TemporalType.DATE)
	private Date id;
	
	@Formula(" id ")
	private Date date;
	
	@Column(name = "is_weekday", nullable = true)
	private Boolean isWeekday;
	
	@Column(name = "y", nullable = true)
	private Short year;
	
	@Column(name = "q", nullable = true)
	private Short quarter;
	
	@Column(name = "m", nullable = true)
	private Short month;

	@Column(name = "d", nullable = true)
	private Short dayOfMonth;
	
	@Column(name = "dw", nullable = true)
	private Short dayOfWeek;

	@Column(name = "w", nullable = true)
	private Short week;
	
	@Column(name = "monthname", nullable = true)
	private String monthname;
	
	@Column(name = "dayname", nullable = true)
	private String dayname;
	
	public CalendarDate() {
		super();
	}
	
	public CalendarDate(Date date) {
		super();
		this.id = date;
		// TODO: init remaining fields usinf the date
	}

	@Override
	public boolean isNew() {
		// is the @Formula attribute initialized?
		return null == this.getDate();
	}

	

	public Date getId() {
		return id;
	}

	public void setId(Date id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean getIsWeekday() {
		return isWeekday;
	}


	public void setIsWeekday(Boolean isWeekday) {
		this.isWeekday = isWeekday;
	}


	public Short getYear() {
		return year;
	}


	public void setYear(Short year) {
		this.year = year;
	}


	public Short getQuarter() {
		return quarter;
	}


	public void setQuarter(Short quarter) {
		this.quarter = quarter;
	}


	public Short getMonth() {
		return month;
	}


	public void setMonth(Short month) {
		this.month = month;
	}


	public Short getDayOfMonth() {
		return dayOfMonth;
	}


	public void setDayOfMonth(Short dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}


	public Short getDayOfWeek() {
		return dayOfWeek;
	}


	public void setDayOfWeek(Short dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}


	public Short getWeek() {
		return week;
	}


	public void setWeek(Short week) {
		this.week = week;
	}

	public String getMonthname() {
		return monthname;
	}

	public void setMonthname(String monthname) {
		this.monthname = monthname;
	}

	public String getDayname() {
		return dayname;
	}

	public void setDayname(String dayname) {
		this.dayname = dayname;
	}


	
}
