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
package gr.abiss.calipso.model.contactDetails.base;

import gr.abiss.calipso.model.geography.LocalRegion;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AbstractLocalRegionMailingAddress extends AbstractMailingAddressDetail {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "region_id", referencedColumnName = "id", nullable = false)
	private LocalRegion city;
	
	public AbstractLocalRegionMailingAddress(){
		
	}
	
	
}
