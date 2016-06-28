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
 */package gr.abiss.calipso.model.geography;

import gr.abiss.calipso.model.base.AbstractAssignedidPersistable;
import gr.abiss.calipso.tiers.annotation.ModelResource;
import io.swagger.annotations.ApiModel;

import java.io.UnsupportedEncodingException;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract base class for formal (usually political) geographical regions
 * @param <T> The parent category type
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@ModelResource(path = "countries")
@ApiModel(value = "Region", description = "A model representing a geographcal region.")
public abstract class AbstractFormalRegion<P extends AbstractFormalRegion> 
	extends AbstractAssignedidPersistable<String> {

	private static final long serialVersionUID = -1397566289646826160L;
	private static final String PATH_SEPARATOR = ": ";

	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "path", nullable = false)
	private String path;
	@Column(name = "path_level", nullable = false)
	private Short pathLevel;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="parent")
	private P parent;

	public AbstractFormalRegion() {
		super();
	}


	public AbstractFormalRegion(String id, String name, P parent) {
		super(id);
		this.name = name;
		this.parent = parent;
	}

	@JsonIgnore
	@Transient
	public String getPathSeparator(){
		return PATH_SEPARATOR;
	}
	
	@PreUpdate
    @PrePersist
    public void normalizePath() throws UnsupportedEncodingException{
		// set path
		if(this.getPath() == null){
			StringBuffer path = new StringBuffer();
			if(this.getParent() != null){
				path.append(this.getParent().getPath());
			}
			path.append(getPathSeparator());
			path.append(this.getName());
			this.setPath(path.toString());
		}
		// set path level
		Integer pathLevel = StringUtils.countMatches(this.getPath(), getPathSeparator());
		this.setPathLevel(pathLevel.shortValue());
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public P getParent() {
		return parent;
	}
	public void setParent(P parent) {
		this.parent = parent;
	}
	public Short getPathLevel() {
		return pathLevel;
	}
	public void setPathLevel(Short pathLevel) {
		this.pathLevel = pathLevel;
	}
	

}
