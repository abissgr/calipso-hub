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
define([ 'backbone', 'marionette', 'hbs!template/generic-crud-layout', 'view/generic-collection-grid-view', 
         'collection/generic-collection', 'hbs!template/tabs', 'hbs!template/tab-label', 'hbs!template/tab-content'],
function (Backbone, Marionette, tmpl, GenericCollectionGridView, GenericCollection, tmplTabs, tmplTabLabel, tmplTabContent) {
	var TabLayout = Backbone.Marionette.Layout.extend({
	    template:  tmpl,
	    className: 'tabbable',
	    regions: {
	        labels: '.tab-labels',
	        panes:  '.contents'
	    },
	    onShow: function(){
      	 console.log("TabLayout#onShow");
	        var tabLabels   = new TabLabelCollection({   collection: this.collection});
	        var tabContents = new TabPaneCollection({ collection: this.collection});
	    
	        this.labels.show(tabLabels);
	        this.panes.show(tabContents);
	    },
	});
	

	var TabLabelCollection = Backbone.Marionette.CollectionView.extend({
	    className: 'nav nav-tabs tab-labels',
	    tagName: 'ul',
		 template: tmplTabs,
		 itemViewContainer: '.nav-tabs',
	    events: {
	        "click .show-tab": "showTab",
	        "click .close-tab": "closeTab"
	    },
	    showTab: function(e) {
	   	 // TODO:
	    },
	    closeTab: function(e) {
	       e.stopPropagation();
	       e.preventDefault();
	       this.model.collection.remove(this.model);
	       this.close();
	    },
		 getItemView: function(item) {
			 return Backbone.Marionette.ItemView.extend({ 
		        tagName: 'li',
		        id: "generic-crud-layout-tabs-" + item.get("name"),
		        template: tmplTabLabel
		    });
		 }
	});

	var TabPaneCollection = Backbone.Marionette.CollectionView.extend({
	    className: 'tab-content',
	    getItemView: function(item) {
	   	 var someItemSpecificView;
      	 console.log("TabPaneCollection#getItemView, item is instance of "+ item.getClassName());
	       if(item.getClassName() == "GenericCollectionWrapperModel"){
	      	 someItemSpecificView = GenericCollectionGridView;
	       }
	       else{
	      	 someItemSpecificView = Backbone.Marionette.ItemView.extend({
		  	        className: 'tab-pane',
			        template: tmplTabContent
			    });
	       }
	       return someItemSpecificView;
	     }
	});

	
	
    return TabLayout;
});

