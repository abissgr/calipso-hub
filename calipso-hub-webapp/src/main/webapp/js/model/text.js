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
define([ 'model/generic-model', 'component/edit-in-modal-cell', 'model/resource', 'model/host' ],
		function( GenericModel, EditInModalCell, ResourceModel, HostModel) {
			var TextModel = ResourceModel.extend({
				
				schemaComplete : function() {
					// superclass schema
					var superSchema = TextModel.__super__.schemaComplete.call(this);
					// own schema
					var schema = {
						"sourceContentType" : {
							"search": { type: 'Select', options: [ 'text/plain', 'text/x-markdown', 'text/html' ] },
							"default": { 
								type: 'Select', 
								options: [ 'text/plain', 'text/x-markdown', 'text/html' ], 	
								validators : [ 'required' ]
							}
						},
						"source" : {
							"search": 'Text',
							"update": {
								type: 'Text',
								validators : [ 'required' ],
							},
							"default": {
								type: 'Text',
								validators : [ 'required' ]
							}
						}
						
					};
					// return merged schemas
					return $.extend({}, superSchema, schema);
				},
			});

			TextModel.prototype.getDefaultSchemaForGrid = function() {
				return [{
					name : "path",
					label : "path",
					cell : "string"
				}, {
					name : "edit",
					label : "edit",
					editable : false,
					cell : EditInModalCell
				} ];
			}
			return TextModel;
		});