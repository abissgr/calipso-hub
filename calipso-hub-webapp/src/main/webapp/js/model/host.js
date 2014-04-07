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
define([ 'model/generic-model', 'component/backgrid-edit-in-tab-button-cell', 'component/backgrid-view-in-tab-cell' ],
		function( GenericModel, EditInTabCell, ViewInTabCell) {
			var HostModel = GenericModel.extend({
			},
			// static members
			{
				parent: GenericModel
			});
			/**
			 * Get the model class URL fragment corresponding this class
			 * @returns the URL path fragment as a string
			 */
			HostModel.prototype.getPathFragment = function() {
				return "hosts";
			}
		
			HostModel.prototype.getTypeName = function() {
			 		return "HostModel";
			}
			HostModel.prototype.getLayoutViewType = function() {
				return require('view/md-search-layout')
			}
			HostModel.prototype.getFormSchemas = function() {
				console.log("HostModel.prototype.getFormSchemas() called, will return undefined");
				return {//
					domain : {
						"search": 'Text',
						"default": {
							type: 'Text',
							validators : [ 'required' ]
						}
					},
				};
			}
			HostModel.prototype.getGridSchema = function() {
				return [
				{
					name : "domain",
					label : "domain",
					editable : false,
					cell : ViewInTabCell
				}, {
					name : "edit",
					label : "",
					editable : false,
					cell : EditInTabCell
				}];
			}
			return HostModel;
		});