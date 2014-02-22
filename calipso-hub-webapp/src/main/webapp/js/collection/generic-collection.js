/*
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
define([ 'backbone', 'backbone-pageable' ],
function(Backbone, BackbonePageableCollection) {
	var GenericCollection = Backbone.PageableCollection.extend({
		mode: "server",
		initialize : function(attributes, options) {
			if (options.model) {
				this.model = options.model;	
			}
			// use given grid columns if provided, or the
			// default model columns otherwise
			if (options.schemaForGrid) {
				this.schemaForGrid = options.schemaForGrid;
			} else {
				this.schemaForGrid = this.model.prototype.getDefaultSchemaForGrid();
			}
		},

		schemaForGrid : {},
		// Initial pagination states
		state: {
			firstPage: 1,
			currentPage: 1,
			pageSize: 15,
		},
		
		// You can remap the query parameters from `state keys from
		// the default to those your server supports
		queryParams: {
			 currentPage: "page",
			 pageSize: "size",
			 totalPages: "totalPages",
			 totalRecords: "totalElements",
			 sortKey: "properties",
			 direction: "order"
		},
						//		
//						 parseState: function (resp, queryParams, state, options) {
//							 return {
//								 totalRecords: resp.totalElements
//							 };
//						 },
//						//		
//						 parseRecords: function (resp, options) {
//							 return resp.content ;
//						 },

						// Parse the JSON response and get the total number of
						// elements.
						// Return only the content JSON element, that contains
						// the users.
						// These are necessary for paging to work.
//						parse : function(resp) {
//							this.total = resp.totalElements;
//							this.totalPages = resp.totalPages;
//							return resp.content;
//						}
		 parse: function(resp){
			this.total = resp.totalElements;
			this.totalPages = resp.totalPages;
		    var _resp = {};
		    _resp.results = [];
		    _.each(resp.content, function(model) {
		    	model.collection - this;
		        _resp.results.push(model);
		    });
		    return _resp.results;
		 }


	});
	return GenericCollection;
});
