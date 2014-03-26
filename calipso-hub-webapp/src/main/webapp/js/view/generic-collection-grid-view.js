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
define([ 'backbone', 'backgrid', 'marionette', 'backgrid-paginator', 'model/user', 'hbs!template/generic-collection-grid-view', 'collection/generic-collection' ], function(Backbone, Backgrid, Marionette, BackgridExtensionPaginator, User, genericCollectionTemplate, GenericCollection) {
	// Backgrid.Extension.Paginator = Paginator;
	var GenericCollectionGridView = Marionette.ItemView.extend(
	/** @lends collection/GenericCollectionGridView.prototype */
	{

		// Define view template
		template : genericCollectionTemplate,
		onShow : function() {
			var _self = this;
			// console.log("GenericCollectionGridView onShow");
			// /GenericCollectionGridView.__super__.render.apply(this);
			console.log("GenericCollectionGridView onShow, model className: " + (this.model.getClassName ? this.model.getClassName() : undefined));
			if (this.model.getClassName && this.model.getClassName() == "GenericCollectionWrapperModel") {
				this.collection = this.model.wrappedCollection;
				console.log("GenericCollectionGridView onShow, got collection from GenericCollectionWrapperModel");
			} else {
				console.log("GenericCollectionGridView onShow, got collection from GenericCollectionWrapperModel");
			}
			// console.log("GenericCollectionGridView onShow, this.collection:
			// "+this.collection + ", gridCollection: "+gridCollection);
			var backgrid = new Backgrid.Grid({
				columns : _self.collection.getGridSchema(),
				collection : _self.collection
			});
			//			

			// console.log("$('#backgrid').attr(id): "+$('#backgrid').attr("id"));
			$('#backgrid').append(backgrid.render().$el);
			var paginator = new Backgrid.Extension.Paginator({

				// If you anticipate a large number of pages, you can adjust
				// the number of page handles to show. The sliding window
				// will automatically show the next set of page handles when
				// you click next at the end of a window.
				windowSize : 20, // Default is 10

				// Used to multiple windowSize to yield a number of pages to slide,
				// in the case the number is 5
				slideScale : 0.25, // Default is 0.5

				// Whether sorting should go back to the first page
				goBackFirstOnSort : false, // Default is true

				collection : _self.collection
			});

			$('#backgrid-paginator').append(paginator.render().el);
			_self.collection.fetch({
				reset : true
			});
			// this.collection.fetch();

			// console.log("GenericCollectionGridView showed");
		}

	},
	// static members
	{
		className: "GenericCollectionGridView",
	});
	return GenericCollectionGridView;
});
