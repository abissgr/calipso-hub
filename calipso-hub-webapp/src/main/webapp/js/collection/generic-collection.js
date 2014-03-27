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
define([ 'backbone', 'backbone-pageable' ], function(Backbone, BackbonePageableCollection) {
	var GenericCollection = Backbone.PageableCollection.extend({
		mode : "server",
		initialize : function(attributes, options) {
			if (options.model) {
				this.model = options.model;
				console.log("GenericCollection#initialize, model given: "+this.model.className);
			}
			else{
				console.log("GenericCollection#initialize, model given: "+this.model.className);
			}
			// use given grid columns if provided, or the
			// default model columns otherwise
			if (options.schemaForGrid) {
				this.schemaForGrid = options.schemaForGrid;
			}
		},
		// Initial pagination states
		state : {
			firstPage : 1,
			currentPage : 1,
			pageSize : 10,
		},
		getGridSchema : function() {
			// use explicit configuration if available
			var configuredSchema = this.schemaForGrid;
			// try obtaining the grid schema from the model otherwise
			if (!configuredSchema && this.model && this.model.prototype.getGridSchema) {
				configuredSchema = this.model.prototype.getGridSchema();
			}

			// ensure proper configuration is available
			if (!configuredSchema) {
				throw new "A grid schema has not been given and the collection model does not offer one or is undefined";
			}
			return configuredSchema;
		},
		// You can remap the query parameters from `state keys from
		// the default to those your server supports
		queryParams : {
			currentPage : "page",
			pageSize : "size",
			totalPages : "totalPages",
			totalRecords : "totalElements",
			sortKey : "properties",
			direction : "order"
		},
		/*
		 totalElements: 32
		 lastPage false 
		 totalPages 4
		 numberOfElements 10
		 firstPage true
		 sort [Object { direction="DESC", property="id", ascending=false}]
		 number 0
		 size 10
		 */
		//		
		// parseState: function (resp, queryParams, state, options) {
		// return {
		// totalRecords: resp.totalElements
		// };
		// },
		// //
		// parseRecords: function (resp, options) {
		// return resp.content ;
		// },

		// Parse the JSON response and get the total number of
		// elements.
		// Return only the content JSON element, that contains
		// the users.
		// These are necessary for paging to work.
		// parse : function(resp) {
		// this.total = resp.totalElements;
		// this.totalPages = resp.totalPages;
		// return resp.content;
		// }
		parse : function(response) {
			console.log("GenericCollection#parse");
			_self = this;
			this.total = response.totalElements;
			this.totalPages = response.totalPages;
			superModelAwareInstances = [];
			console.log("GenericCollection#parse, items: "+response.content.length);
			_.each(response.content, function(modelItem) {
				// make Backbone Supermodel aware of this item
				console.log("GenericCollection#parse model id: "+modelItem.constructor);
				superModelAwareInstance = _self.model.create(modelItem);
				superModelAwareInstance.collection = _self;
				// add to results
				superModelAwareInstances.push(superModelAwareInstance);
			});
			return superModelAwareInstances;
		}

	});
	return GenericCollection;
});
