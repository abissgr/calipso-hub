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
define([ 'backbone', 'supermodel', 'backgrid', 'view/GenericFormTabContentView' ], 
function(Backbone, Supermodel, Backgrid, GenericFormTabContentView) {
	var GenericModel = Supermodel.Model.extend({
		modelKey: null,
		/**
		 * Prefer the collection URL if any for more specific CRUD, fallback to own otherwise 
		 */
		url: function() {
			var sUrl = this.collection	 ? _.result(this.collection, 'url') : _.result(this, 'urlRoot') || urlError();
			if (!this.isNew()) {
				sUrl = sUrl + (sUrl.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.get("id"));
			}
			console.log("GenericModel#url: " + sUrl + ", is new: "+this.isNew() + ", id: "+this.get("id"));
			return sUrl;
	    },
	    schemaComplete : function() {
	   	 return this.prototype.schemaComplete(this);
		},
	   schemaForAction : function(actionName) {
	 		// decide based on model persistence state if no action was given 
 			console.log("GenericModel.prototype.schema, actionName: "+actionName);
	 		if(!actionName){
	 			console.log("GenericModel.prototype.schema, this: "+this);
	 			console.log("GenericModel.prototype.schema, caller is " + arguments.callee.caller.toString());
	 			actionName = this.isNew() ? "create" : "update";
	 		}
	 		// the schema to build for the selected action 
	 		var schemaForAction = {};
	 		// get the complete schema to filter out from
	 		var schemaComplete = this.schemaComplete();
	 		//console.log("GenericModel#schema actionName: "+actionName+", schemaComplete: "+schemaComplete);
	 		
	 		// for each property, select the appropriate schema entry for the given action
	 		var propertySchema;
	 		var propertySchemaForAction;
	 		for(var propertyName in schemaComplete) {
	 		    if(schemaComplete.hasOwnProperty(propertyName)) {
	 		    	propertySchema = schemaComplete[propertyName];
	 	    		
	 		    	// if a schema exists for the property
	 		    	if(propertySchema){
	 		    		// try obtaining a schema for the specific action 
	 	    			propertySchemaForAction = propertySchema[actionName];
	 	    			// support wild card entries
	 	    			if(!propertySchemaForAction){
	 	    				propertySchemaForAction = propertySchema["default"];
	 	    			}
	 	    			if(propertySchemaForAction){
	 	    				schemaForAction[propertyName] = propertySchemaForAction;
	 	    			}
	 	    		}
	 	    	}
	 		    	
	 	    	// reset
	 	    	propertySchema = false;
	 	    	propertySchemaForAction = false;
	 	    }
	 		//console.log("GenericModel#schema schemaForAction: "+schemaForAction);
	 		return schemaForAction;
	 	},
//		url:  function () {
//			console.log("GenericModel#url");
//			var sUrl;
//			if(this.collection && this.collection.url){
//				sUrl = this.collection.get("url");
//				console.log("Using model's collection url: "+sUrl);
//			}
//			else{
//				sUrl = this.urlRoot;
//				console.log("Using model's own url: "+sUrl);
//			}
//			if(!this.isNew()){
//				sUrl = sUrl + this.get(this.idAttribute); 
//			}
//		},
		defaults: {
         itemView: GenericFormTabContentView
		},	
		getClassName: function(){
			var c = this.constructor.className;
			console.log("GenericModel#getClassName: "+c);
			return c;
		},
		initialize: function () {
		    Backbone.Model.prototype.initialize.apply(this, arguments);
		    var thisModel = this;
		    this.on("change", function (model, options) {
			    if (options && options.save === false) {
			    	return;
			    }
			    //var sUrl = model.url+"/"+model.get("id");
			    //(base.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.id);
			    //model.save({}, {url: sUrl});
		    });
		},
			
//			save : function(attributes, options) {
//				console.log("Saving change, attributes: "+attributes+", options: "+options);
//				var result = Backbone.Model.prototype.save.call(this, attributes, options);
//				console.log("Saved change");
//			},
	
			// validation: {
			// },

		
		},
		// static members
		{
			className: "GenericModel"
		}
	);
	GenericModel.prototype.getGridSchema = function(){
		console.log("GenericModel.prototype.getSchemaForGrid() called, will return undefined");
		return undefined;
	}

	GenericModel.prototype.getFormSchema = function(instance) {
		console.log("GenericModel.prototype.getFormSchemad() called, will return undefined");
		return undefined;
	}
	
	//console.log("GenericModel done");
	return GenericModel;
});