/*
 * Copyright (c) 2007 - 2014 www.Abiss.gr
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

define(
		[ "lib/calipsolib/util", 'underscore', 'handlebars', 'moment', 'backbone', 'backbone.paginator',
		 'marionette', 
		 'backgrid', 'backgrid-moment', 'backgrid-text', 'backgrid-responsive-grid', 'backgrid-paginator',
		 'bootstrap-switch'],
		function( Calipso, _, Handlebars, moment, Backbone, PageableCollection,
			BackboneMarionette, 
			Backgrid, BackgridMoment, BackgridText, BackgridResponsiveGrid, BackgridPaginator,
			BootstrapSwitch) {

			// make backgrid tables responsive
			var BackgridCellInitialize = Backgrid.Cell.prototype.initialize;
			Backgrid.Cell.prototype.initialize = function() {
				BackgridCellInitialize.apply(this, arguments);
				this.$el.attr("data-title", this.column.get("label"));
			}
			
			Calipso.components.backgrid = {};
			Calipso.components.backgrid.SmartHighlightRow = Backgrid.Row.extend({
				initialize : function() {
					Backgrid.Row.prototype.initialize.apply(this, arguments);
					this.listenTo(this.model, 'change', function(model) {
						this.$el.toggleClass('bg-warning', model.hasChanged());
					});
					this.listenTo(this.model, 'sync', function(model) {
						// creating an empty element and applying our class to it to get bootstrap class bg color
						var origBg = this.$el.css("background-color");
						var bgcolor = $("<div>").appendTo("body").addClass("bg-success").css("background-color");

						this.$el.removeClass('bg-warning');
						this.$el.animate({
							backgroundColor : bgcolor
						}, {
							queue : true,
							duration : 1500
						});
						this.$el.animate({
							backgroundColor : origBg
						}, {
							queue : true,
							duration : 5000
						});
					});
					this.listenTo(this.model, 'added', function(model) {
						// creating an empty element and applying our class to it to get bootstrap class bg color
						var origBg = this.$el.css("background-color");
						var bgcolor = $("<div>").appendTo("body").addClass("bg-success").css("background-color");
						this.$el.removeClass('bg-warning');
						this.$el.animate({
							backgroundColor : bgcolor
						}, {
							queue : true,
							duration : 1500
						});
						this.$el.animate({
							backgroundColor : origBg
						}, {
							queue : true,
							duration : 5000
						});
					});
				}
			});
			Calipso.components.backgrid.ViewRowCell = Backgrid.StringCell.extend(
			/** @lends Calipso.components.backgrid.ViewRowCell.prototype */
			{
				className : "view-row-cell",
				initialize : function(options) {
					Backgrid.StringCell.prototype.initialize.apply(this, arguments);
					this.viewRowEvent = "layout:viewModel";
				},
				events : {
					"click" : "viewRow"
				},
				viewRow : function(e) {
					Calipso.stopEvent(e);
					Calipso.vent.trigger(this.viewRowEvent, this.model);
				},
				render : function() {
					this.$el.empty();
					var model = this.model;
					var formattedValue = this.formatter.fromRaw(model.get(this.column.get("name")), model);
					this.$el.append($("<a>", {
						tabIndex : -1,
						title : formattedValue
					}).text(formattedValue));
					this.delegateEvents();
					return this;
				}

			});

			//

			Calipso.components.backgrid.BootstrapSwitchCell = Backgrid.BooleanCell.extend(
			/** @lends Calipso.components.backgrid.BootstrapSwitchCell.prototype */
			{
				render : function() {
					Backgrid.BooleanCell.prototype.render.apply(this, arguments);
					var _this = this;
					setTimeout(function() {
						_this.$el.find("input").bootstrapSwitch();
					}, 250);
					return this;
				}
			});

			Calipso.components.backgrid.EditRowCell = Backgrid.Cell.extend(
			/** @lends Calipso.components.backgrid.EditRowCell.prototype */
			{
				tagName : "td",
				className : "modal-button-cell modal-button-cell-edit",
				events : {
					"click" : "editEntry",
					"click button" : "editEntry",
				},
				editEntry : function(e) {
					Calipso.stopEvent(e);
					var rowModel = this.model;
					//console.log("EditInTabCell#editEntry "+ rowModel.getTypeName()+'#'+rowModel.get("id"));
					// console.log("editRow, rowModel: " + rowModel.constructor.name);
					Calipso.vent.trigger("genericShowContent", rowModel);
				},
				render : function() {
					this.$el.html("<button class='btn btn-xs btn-info' title='Edit entry'><i class='glyphicon glyphicon-edit'></i>&nbsp;Edit</button>");
					//this.delegateEvents();
					return this;
				}
			});

			Calipso.components.backgrid.EditRowInModalCell = Calipso.components.backgrid.EditRowCell.extend(
			/** @lends Calipso.components.backgrid.EditRowCell.prototype */
			{
				editEntry : function(e) {
					Calipso.stopEvent(e);
					var rowModel = this.model;
					var ContentViewType = rowModel.getItemViewType();
					var contentView = new ContentViewType({
						model : rowModel,
						modal : true
					});
					Calipso.vent.trigger("modal:showInLayout", {
						view : contentView,
						model : rowModel,
					});
				}
			});

			Calipso.components.backgrid.ChildStringAttributeCell = Backgrid.StringCell.extend({
				render : function() {
					var path = this.column.get("path");
					if (!path) {
						path = this.column.get("name");
					}
					var result = Calipso.getPathValue(this.model, path);
					if (!(_.isUndefined(result) || _.isNull(result))) {
						this.$el.text(result);
					}
					this.delegateEvents();
					return this;
				},
			});

			Calipso.components.backgrid.ChildNumberAttributeCell = Backgrid.NumberCell.extend({
				render : function() {
					var path = this.column.get("path");
					if (!path) {
						path = this.column.get("name");
					}
					var result = Calipso.getPathValue(this.model, path);
					if (!(_.isUndefined(result) || _.isNull(result))) {
						this.$el.text(this.formatter.fromRaw(result));
					}
					this.delegateEvents();
					return this;
				},
			});

			Calipso.components.backgrid.CreateNewHeaderCell = Backgrid.HeaderCell.extend({

				tagName : "th",
				className : "renderable backgrid-create-new-header-cell",
				events : {
					"click" : "createNewForManualEdit"
				},
				initialize : function(options) {
					Backgrid.HeaderCell.prototype.initialize.apply(this, arguments);
				},
				createNewForManualEdit : function(e) {
					//console.log("CreateNewHeaderCell#newRow, rowModel: " + this.collection.model);
					Calipso.stopEvent(e);
					Calipso.vent.trigger("layout:createModel", {
						modelType : this.collection.model
					});
				},
				render : function() {
					var html = $("<button title='Create new' class='btn btn-xs btn-success'><i class='fa fa-file-text'></i>&nbsp;New</button>");
					this.$el.html(html);
					//this.delegateEvents();
					return this;
				}
			});

			Calipso.components.backgrid.CreateNewInModalHeaderCell = Calipso.components.backgrid.CreateNewHeaderCell.extend({

				createNewForManualEdit : function(e) {
					Calipso.stopEvent(e);
					var rowModel = this.collection.model.create();
					var ContentViewType = rowModel.getItemViewType();
					var contentView = new ContentViewType({
						model : rowModel,
						modal : true,
						addToCollection : this.collection
					});

					Calipso.vent.trigger("modal:showInLayout", {
						view : contentView,
						model : rowModel,
					});
				},
			});

		
});
