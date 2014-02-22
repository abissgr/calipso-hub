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
define([ 'backbone', 'model/generic-model', 'model/host' ],
		function(Backbone, GenericModel, HostModel) {
			var ResourceModel = GenericModel.extend({
				schemaComplete : function() {
					var schema = {//
						"host" : {
							"default": {
								type: 'NestedModel',
								model : HostModel,
							}
						},
						"path" : {
							"search": 'Text',
							"default": {
								type: 'Text',
								validators : [ 'required' ]
							}
						},
						
					};
					return schema;
				},
			});

			ResourceModel.prototype.getDefaultSchemaForGrid = function() {
				return [
				{
					name : "path",
					label : "path",
					cell : "string"
				}];
			}
			return ResourceModel;
		});