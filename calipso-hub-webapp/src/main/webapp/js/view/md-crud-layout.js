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
	var session = require('session'), 
	vent = require('vent'), 
	Backbone = require('backbone'), 
	Marionette = require('marionette'), 
	AbstractLayoutView = require('view/abstract-layout-view'),
	BackboneForm = require('backbone-forms'), 
	GenericCollectionGridView = require('view/md-collection-grid-view'), 
	GenericCollection = require('collection/generic-collection'), 
	GenericFormTabContentView = require('view/GenericFormTabContentView'), 
	tmplTabLabel = require('hbs!template/tab-label'), 
	tmpl = require('hbs!template/md-crud-layout');

	var ModelDrivenCrudLayout = AbstractLayoutView.extend({
		template : tmpl,
		tagName : "div",
		className : "col-sm-12",
		regions : {
			tabLabelsRegion : '#calipsoTabLabelsRegion',
			tabContentsRegion : '#calipsoTabContentsRegion'
		},
		initialize: function(options){
			
			Marionette.Layout.prototype.initialize.apply(this, arguments);
			if(options.collection){
				this.collection = options.collection;
			}
			else if(options.model && options.model.constructor.getTypeName() == "GenericCollectionWrapperModel"){
					this.collection = options.model.wrappedCollection;
			}
			if(!this.collection){
				throw "no collection or collection wrapper model was provided";
			}
			console.log("ModelDrivenCrudLayout.initialize, collection size: " + this.collection.length  +
					", collection.model: "+this.collection.model.getTypeName());
	  },
		onShow : function() {
			console.log("ModelDrivenCrudLayout#onShow");
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
 		getTypeName: function(){return "ModelDrivenCrudLayout"}
	});

	var TabLabelsCollectionView = Backbone.Marionette.CollectionView.extend({
		className : 'nav nav-pills',
		tagName : 'ul',
		itemViewContainer : '.nav-tabs',
		getItemView : function(item) {
			return Backbone.Marionette.ItemView.extend({
				tagName : 'li',
				className : 'md-crud-layout-tab-label',
				id : "md-crud-layout-tab-label-" + item.get("id"),
				template : tmplTabLabel,

				events : {
					"click .show-tab": "viewTab",
					"click .close-tab" : "closeTab"
				},
				 viewTab: function(e) {
					 console.log("TabPaneCollectionView.itemView#viewTab");
					 e.stopPropagation();
					 e.preventDefault();
					 vent.trigger("viewTab", this.model);
				 },
				closeTab : function(e) {
					console.log("TabPaneCollectionView.itemView#closeTab");
					e.stopPropagation();
					e.preventDefault();
//					this.model.collection.remove(this.model);
					this.close();
					vent.trigger("viewTab", {
						id : "Search"
					});
				},
			});
		}
	});

	var TabContentsCollectionView = Backbone.Marionette.CollectionView.extend({
		tagName : 'div',
		getItemView : function(item) {
			var ItemViewClass;
			if(item){
				if (item.get("itemView")) {
					ItemViewClass = item.get("itemView");
				} else {
					ItemViewClass = GenericFormTabContentView;
				}
				console.log("TabContentsCollectionView#getItemView for item class " + item.constructor.getTypeName() + " returns: " + ItemViewClass.getTypeName());
			}
			return ItemViewClass;
		},
		buildItemView: function(item, ItemViewClass){
			console.log("TabContentsCollectionView#buildItemView, ItemView: "+ItemViewClass.getTypeName()+", item: "+item.constructor.getTypeName() + ", wrapped collection: "+item.wrappedCollection);
			if(item){
				var options = {model: item};
				if(item && item.wrappedCollection){
					options.collection = item.wrappedCollection;
				}
			    // do custom stuff here
	
			    var view = new ItemViewClass(options);
	
			    // more custom code working off the view instance
	
			    return view;
			}
		  },
	});

	return ModelDrivenCrudLayout;
});
