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
package gr.abiss.calipso.model.dto;

import java.util.Map;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class ReportDataEntry implements Comparable<ReportDataEntry>{

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportDataEntry.class);
	
	private static FastDateFormat dateFormat = FastDateFormat.getInstance("yyyyMMdd");
	
	private String label;
	private Map<String, Number> entryData;
	
	private ReportDataEntry() {
		super();
	}
	
	public ReportDataEntry(String label, Map<String, Number> entryData) {
		this();
		this.label = label;
		this.entryData = entryData;
	}


	@Override
	public int compareTo(ReportDataEntry that) {
	     return new CompareToBuilder()
	       //.appendSuper(super.compareTo(o)
	       .append(this.getLabel(), that.getLabel())
	       .toComparison();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("label", this.getLabel()).append("entryData", this.getEntryData()).toString();
	}


	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ReportDataEntry)) {
			return false;
		}
		ReportDataEntry that = (ReportDataEntry) obj;
		return new EqualsBuilder()
        .appendSuper(super.equals(obj))
        .append(this.getLabel(), that.getLabel())
        //.append(this.getEntryData(), that.getEntryData())
        .isEquals();
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Map<String, Number> getEntryData() {
		return entryData;
	}
	
	public void setEntryData(Map<String, Number> entryData) {
		this.entryData = entryData;
	}
	

}