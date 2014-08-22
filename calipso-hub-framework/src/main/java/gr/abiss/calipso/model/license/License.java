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
 * 
 * Members of this class were taken from the ccrdfjavalib project:
 * http://sourceforge.net/projects/ccrdfjavalib/
 * The code was (C) 2004  Peter Magic and licensed under the NU Lesser General Public License: 
 */

package gr.abiss.calipso.model.license;

import gr.abiss.calipso.model.cms.Resource;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "license")
public class License extends Resource{
	
	// Basic information common to every license
	//private String name;             	// A unique name for this license
	private String short_name;       	// A short textual representation of the license name
	private String full_text;        	// A URI to the full text of this license
	private String logo_uri;         	// An optional logo for this license
	private String extra_rdf;        	// A buffer for additional RDF outside the scope of the primitives
	// Permissions for the license
	private boolean reproduction;     	// Do we allow the work to be reproduced?
	private boolean distribution;     	// Do we allow public distribution of the work?
	private boolean derivatives;      	// Do we allow derivative works?
	// Restrictions for the license
	private boolean noncommercial;    	// Can others profit from the work?
	// Requirements of the license
	private boolean notice;           	// Do we require that copyright notices be maintained?
	private boolean attribution;      	// Do we require attribution?
	private boolean share_alike;        // Do we require that derivative works use the same license?

}
