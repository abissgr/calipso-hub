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
define([ 'bootstrap-markdown', 'component/backbone-forms-editor-markdown', 
         'model/generic-model', 'component/backgrid-edit-in-modal-button-cell', 
         'model/resource', 'model/host' ],
		function( Markdown, BackboneFormMarkdown,
				GenericModel, EditInModalCell, 
				ResourceModel, HostModel) {
			//Backbone.Form.editors.Markdown
			var TextModel = ResourceModel.extend({
				modelKey: "text",
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
							"default": {
								type: BackboneFormMarkdown,
								validators : [ 'required' ],
								editorAttrs: { "data-provide": 'markdown' }
							},
						}
						
					};
					// return merged schemas
					return $.extend({}, superSchema, schema);
				},
			},
			// static members
			{
				className: "TextModel"
			});

			TextModel.prototype.getDefaultSchemaForGrid = function() {
				return [
						{
							name : "name",
							label : "name",
							cell : "string"
						},
						{
							name : "pathName",
							label : "path name",
							cell : "string"
						},
						{
							name : "edit",
							label : "",
							editable : false,
							cell : EditInModalCell
						} ];
				// return merged schemas
				return $.extend({}, superSchema, schema);
			}
			return TextModel;
		});