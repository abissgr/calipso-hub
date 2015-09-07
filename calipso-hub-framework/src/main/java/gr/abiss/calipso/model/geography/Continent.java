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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("rawtypes")
@Entity
@Table(name = "continent")
@AttributeOverrides({
    @AttributeOverride(name="id", column=@Column(unique = true, nullable = false, length = 2)),
})
public class Continent extends AbstractFormalRegion<Continent> {

	private static final long serialVersionUID = 6388235900995527471L;

	public Continent() {
		super();
	}
	
	public Continent(String id, String name) {
		super(id, name, null);
	}

}