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
define([ 'calipso', 'component/backgrid-edit-in-tab-button-cell', 'backgrid-view-row-maximized-cell'],
		function( Calipso, EditInTabCell, ViewRowCell) {
			var ResourceModel = Calipso.model.GenericModel.extend({
				modelKey: "resource",
				apiPath : "/api/rest/resource/",
				
			},
			// static members
			{
				parent: Calipso.model.GenericModel
			});

			/**
			 * Get the model class URL fragment corresponding this class
			 * @returns the URL path fragment as a string
			 */
			ResourceModel.prototype.getPathFragment = function() {
				return "resources";
			}
			ResourceModel.prototype.getFormSchemas = function() {
				var schema = {//
					"host" : {
						"default": {
							type: 'NestedModel',
							model : Calipso.model.HostModel,
						}
					},
					"name" : {
						"search": 'Text',
						"default": {
							type: 'Text',
							validators : [ 'required' ]
						}
					},
					"pathName" : {
						"search": null,
						"default": {
							type: 'Text',
							validators : [ 'required' ]
						}
					},
					
				};
				return schema;
			}
			ResourceModel.prototype.getGridSchema = function() {
				return [
						{
							name : "name",
							label : "name",
							editable : false,
							cell : ViewRowCell
						},
						{
							name : "pathName",
							label : "path name",
							editable : false,
							cell : "string"
						}, {
							name : "edit",
							label : "",
							editable : false,
							cell : EditInTabCell
						}];
			}
			return ResourceModel;
});