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
define([ 'app', 'backbone', 'marionette', 'hbs!template/generic-crud-layout', 'view/generic-collection-grid-view', 'view/GenericFormView', 
         'collection/generic-collection', 'hbs!template/tabs', 'hbs!template/tab-label', 'hbs!template/tab-content'],
function (CalipsoApp, Backbone, Marionette, tmpl, GenericCollectionGridView, GenericFormView, GenericCollection, tmplTabs, tmplTabLabel, tmplTabContent) {
	var TabLayout = Backbone.Marionette.Layout.extend({
	    template:  tmpl,
	    className: 'tabbable',
	    regions: {
	        labels: '.tab-labels',
	        panes:  '.contents'
	    },
	    onShow: function(){
      	 console.log("TabLayout#onShow");
	        var tabLabelsView   = new TabLabelCollectionView({   collection: this.collection});
	        var tabContentsView = new TabPaneCollectionView({ collection: this.collection});
	        this.labels.show(tabLabelsView);
	        this.panes.show(tabContentsView);

	        
	    },
	});
	

	var TabLabelCollectionView = Backbone.Marionette.CollectionView.extend({
	    className: 'nav nav-tabs tab-labels',
	    tagName: 'ul',
		 template: tmplTabs,
		 itemViewContainer: '.nav-tabs',
		 getItemView: function(item) {
			 return Backbone.Marionette.ItemView.extend({ 
		        tagName: 'li',
		        className: 'generic-crud-layout-tab-label',
		        id: "generic-crud-layout-tab-label-" + item.get("id"),
		        template: tmplTabLabel,

		 	    events: {
		 	        "click .show-tab": "viewTab",
		 	        "click .close": "closeTab"
		 	    },
		 	    viewTab: function(e) {
		      	 console.log("TabPaneCollectionView.itemView#viewTab");
		 	       e.stopPropagation();
		 	       e.preventDefault();
		 			CalipsoApp.vent.trigger("viewTab", this.model);
		 	    },
		 	    closeTab: function(e) {
		      	 console.log("TabPaneCollectionView.itemView#closeTab");
		 	       e.stopPropagation();
		 	       e.preventDefault();
		 	       this.model.collection.remove(this.model);
		 	       this.close();
		 				CalipsoApp.vent.trigger("viewTab", {id:"Search"});
		 	    },
		    });
		 }
	});

	var TabPaneCollectionView = Backbone.Marionette.CollectionView.extend({
	    className: 'tab-content',
	    getItemView: function(item) {
	   	 var someItemSpecificView;
	       if(item.get("itemView")){
	      	 someItemSpecificView = item.get("itemView");
	      	 console.log("TabPaneCollectionView#getItemView, view: "+someItemSpecificView.constructor);
	       }
	       else{
	      	 someItemSpecificView = GenericFormView;
	       }
	       return someItemSpecificView;
	     }
	});

	
	
    return TabLayout;
});

