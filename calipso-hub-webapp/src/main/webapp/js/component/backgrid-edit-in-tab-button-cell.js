/*
 * Copyright (c) 2007 - 2016 Manos Batsis
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
define([ 'app', 'backbone-forms', 'backgrid', 'bootstrap-markdown','backbone-forms-bootstrap3' ],
function( CalipsoApp, BackboneForm, Backgrid, Markdown) {

	var EditInTabCell = Backgrid.Cell.extend({
		tagName: "td class='modal-button-cell modal-button-cell-edit'",
		template : _.template('<button class="btn btn-xs btn-warning"><span class="glyphicon glyphicon-edit"></span></button>'),
		events : {
			"click" : "editRow"
		},
		editRow : function(e) {
//					var thisModal = this;
			console.log("EditInTabCell#editRow ");
			e.stopPropagation();
			e.preventDefault();
			var rowModel = this.model;
			console.log("editRow, rowModel: "+rowModel.constructor.name);
			CalipsoApp.vent.trigger("editItem", rowModel);
		},
		render : function() {
			this.$el.html(this.template());
			this.delegateEvents();
			return this;
		}
	});
	return EditInTabCell;
});