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
define([ 'backbone-bootstrap-modal', 'backbone-forms', 'backgrid' ],
		function( BackboneBootstrapModal, BackboneForm, Backgrid) {
			

			var EditInModalCell = Backgrid.Cell.extend({
				tagName: "td class='modal-button-cell'",
				template : _.template('<button class="btn btn-xs btn-warning"><span class="glyphicon glyphicon-edit"></span></button>'),
				events : {
					"click" : "editRow"
				},
				editRow : function(e) {
					console.log("Hello");
					e.preventDefault();
					var rowModel = this.model;
					var rowModelSchema = rowModel.isNew() ? rowModel.schema("create") : rowModel.schema("update");
					var form = new Backbone.Form({
						model : rowModel,
						schema : rowModelSchema,
					});

					var modal = new Backbone.BootstrapModal({
						title : 'My form',
						content : form,
						okBtn : 'save'
					}).open();

					modal.on('ok', function() {
						var errs = form.commit();
						if (errs){
							return modal.preventClose();
						}
						var sUrl = rowModelSchema.url;
						sUrl = sUrl + (base.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.id);
						rowModelSchema.save({}, {url: sUrl});
					});
				},
				render : function() {
					this.$el.html(this.template());
					this.delegateEvents();
					return this;
				}
			});
			return EditInModalCell;
		});