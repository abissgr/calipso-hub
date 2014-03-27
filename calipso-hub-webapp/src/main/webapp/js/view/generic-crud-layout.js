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
define(function(require) {
	var CalipsoApp = require('app'), 
	Backbone = require('backbone'), 
	Marionette = require('marionette'), 
	BackboneForm = require('backbone-forms'), 
	GenericCollectionGridView = require('view/generic-collection-grid-view'), 
	GenericCollection = require('collection/generic-collection'), 
	GenericFormTabContentView = require('view/GenericFormTabContentView'), 
	tmplTabLabel = require('hbs!template/tab-label'), 
	tmpl = require('hbs!template/generic-crud-layout');

	var TabLayout = Backbone.Marionette.Layout.extend({
		template : tmpl,
		tagName : "div",
		regions : {
			tabLabelsRegion : '#calipsoTabLabelsRegion',
			tabContentsRegion : '#calipsoTabContentsRegion'
		},
		onShow : function() {
			console.log("TabLayout#onShow");
			var tabLabelsView = new TabLabelsCollectionView({
				collection : this.collection
			});
			var tabContentsView = new TabContentsCollectionView({
				collection : this.collection
			});
			this.tabLabelsRegion.show(tabLabelsView);
			this.tabContentsRegion.show(tabContentsView);

		},
	},
	// static members
	{
		className : "TabLayout",
	});

	var TabLabelsCollectionView = Backbone.Marionette.CollectionView.extend({
		className : 'nav nav-tabs',
		tagName : 'ul',
		itemViewContainer : '.nav-tabs',
		getItemView : function(item) {
			return Backbone.Marionette.ItemView.extend({
				tagName : 'li',
				className : 'generic-crud-layout-tab-label',
				id : "generic-crud-layout-tab-label-" + item.get("id"),
				template : tmplTabLabel,

				events : {
					"click .show-tab": "viewTab",
					"click .close-tab" : "closeTab"
				},
				 viewTab: function(e) {
					 console.log("TabPaneCollectionView.itemView#viewTab");
					 e.stopPropagation();
					 e.preventDefault();
					 CalipsoApp.vent.trigger("viewTab", this.model);
				 },
				closeTab : function(e) {
					console.log("TabPaneCollectionView.itemView#closeTab");
					e.stopPropagation();
					e.preventDefault();
//					this.model.collection.remove(this.model);
					this.close();
					CalipsoApp.vent.trigger("viewTab", {
						id : "Search"
					});
				},
			});
		}
	});

	var TabContentsCollectionView = Backbone.Marionette.CollectionView.extend({
		tagName : 'div',
		getItemView : function(item) {
			var someItemSpecificView;
			if (item.get("itemView")) {
				someItemSpecificView = item.get("itemView");
			} else {
				someItemSpecificView = GenericFormTabContentView;
			}
			//console.log("TabContentsCollectionView#getItemView returns: " + someItemSpecificView.className);
			return someItemSpecificView;
		}
	});

	return TabLayout;
});
