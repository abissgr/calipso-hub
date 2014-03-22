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
define([ 'backbone', 'backgrid', 'model/generic-model','view/generic-collection-grid-view' ], 
function(Backbone, Backgrid, GenericModel, GenericCollectionGridView) {
	var GenericCollectionWrapperModel = Backbone.Model.extend({
		// TODO: bulk persist, see also if Supermodel can be used 
		// for useful stuff like updating the owner entities' children based on 
		// the owner property, see 
		// http://pathable.github.io/supermodel/
//		url: "/bulkupload",

		// save the complete collection? see also above
//		toJSON: function() {
//		    return this.wrappedCollection.toJSON(); // where model is the collection class YOU defined above
//		},
		defaults: {
         id: 'Search',
         name: 'Search',
         itemView: GenericCollectionGridView
		},	
//		id : "Search",
		getClassName: function(){
			var c = this.constructor.className;
			console.log("GenericCollectionWrapperModel#getClassName: "+c);
			return c;
		},
		initialize : function(attributes, options) {
			// the collection to wrap
			if (attributes){
				if (attributes.wrappedCollection) {
					this.wrappedCollection = attributes.wrappedCollection;
					console.log("GenericCollectionWrapperModel#initialize from attributes");
					// note collections modelClass if available,
					// this can be overriden by attributes.modelClass
					if (this.wrappedCollection.modelClass) {
						this.modelClass = this.wrappedCollection.modelClass;	
					}
				}
				// override modelClass
				if (attributes.modelClass) {
					this.modelClass = attributes.modelClass;	
			}
			}
			// use  given grid columns if provided, or the
			// collection model columns if not, finally default to 
			// the model columns
			if (attributes.schemaForGrid) {
				this.schemaForGrid = attributes.schemaForGrid;
			}
			else if (this.wrappedCollection && this.wrappedCollection.schemaForGrid) {
				this.schemaForGrid = this.wrappedCollection.schemaForGrid;
			} else {
				this.schemaForGrid = this.modelClass.prototype.getDefaultSchemaForGrid();
			}
			
			var thisModel = this;
		    this.on("change", function (model, options) {
			    console.log("GenericCollectionWrapperModel 'change' fired");
		    });
		}
	},
	// static members
	{
		className: "GenericCollectionWrapperModel"
	});
	GenericModel.prototype.getDefaultSchemaForGrid = function(instance){

		// override modelClass
		if (instance.wrappedCollection && instance.wrappedCollection.first()) {
			return instance.wrappedCollection.first().getDefaultSchemaForGrid();	
		}
		else if (instance.modelClass) {
			return instance.modelClass.prototype = options.modelClass;	
		}
	
	}
	//console.log("GenericModel done");
	return GenericCollectionWrapperModel;
});