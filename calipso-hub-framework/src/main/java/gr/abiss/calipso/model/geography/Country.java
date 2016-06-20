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
package gr.abiss.calipso.model.geography;

import gr.abiss.calipso.model.base.AbstractAssignedidPersistable;
import gr.abiss.calipso.tiers.annotation.ModelResource;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Class to represent a country, including ISO 3166-1 alpha-2 code, name, languages, 
 * capital and currency, native name, calling codes. 
 */
@ModelResource(path = "countries")
@Entity
@Table(name = "country")
@AttributeOverrides({
    @AttributeOverride(name="id", column=@Column(unique = true, nullable = false, length = 2)),
    @AttributeOverride(name="name", column=@Column(unique = true, nullable = false, length = 50)),
})
public class Country extends AbstractFormalRegion<Continent> {

	private static final long serialVersionUID = 3723330183409907891L;
	
	@Column(unique = true, nullable = true, length = 50)
	private String nativeName;
	
	@Column(unique = false, nullable = true, length = 15)
	private String callingCode;
	
	@Column(unique = false, nullable = true, length = 50)
	private String capital;
	
	@Column(unique = false, nullable = true, length = 30)
	private String currency;
	
	@Column(unique = false, nullable = true, length = 30)
	private String languages;

	public Country() {
		super();
	}
	
	public Country(String id) {
		this.setId(id);
	}
	
	public Country(String id, String name, String nativeName, String callingCode, Continent continent, 
			String capital, String currency, String languages) {
		super(id, name, continent);
		this.nativeName = nativeName;
		this.callingCode = callingCode;
		this.capital = capital;
		this.currency = currency;
		this.languages = languages;
	}

	public String getNativeName() {
		return nativeName;
	}

	public void setNativeName(String nativeName) {
		this.nativeName = nativeName;
	}

	public String getCallingCode() {
		return callingCode;
	}

	public void setCallingCode(String callingCode) {
		this.callingCode = callingCode;
	}

	public String getCapital() {
		return capital;
	}

	public void setCapital(String capital) {
		this.capital = capital;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}
	
}