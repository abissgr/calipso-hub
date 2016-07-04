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

define([ 'jquery', 'underscore', "lib/calipsolib/util", "lib/calipsolib/form",
         "lib/calipsolib/uifield", "lib/calipsolib/backgrid", "lib/calipsolib/view", 'handlebars', 'moment' ],
function($, _, Calipso, CalipsoForm, CalipsoField, CalipsoGrid, CalipsoView, Handlebars, moment) {

	/**
	 * A base model implementation to extend for your own models.
	 * Provides usecase metadata as a means to declaratively define view
	 * hierarchies. that may also map to dynamic (i.e. non-explicit)
	 * controller routes.
	 * @constructor
	 * @requires Backbone
	 * @augments module:Backbone.Model
	 */
	Calipso.Model = Calipso.model.Model.extend(
	/** @lends Calipso.Model.prototype */
	{
		skipDefaultSearch : false,

		initialize : function() {
			Calipso.model.Model.prototype.initialize.apply(this, arguments);
		},
    toString : function() {
      return this.get(this.constructor.nameProperty) || this.get("name") || this.get("id");
    },
		/**
		 * Returns the URL for this model, giving precedence  to the collection URL if the model belongs to one,
		 * or a URL based on the model path fragment otherwise.
		 */
		url : function() {
			var sUrl = this.collection && _.result(this.collection, 'url') ? _.result(this.collection, 'url') : Calipso.getBaseUrl() + this.getBaseFragment() + this.getPathFragment()/*_.result(this, 'urlRoot')*/|| urlError();
			//console.log("GenericModel#url, sUrl: " + sUrl);
			if (!this.isNew()) {
				sUrl = sUrl + (sUrl.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.get("id"));
			}
			// console.log("GenericModel#url: " + sUrl + ", is new: " + this.isNew() + ", id: " + this.get("id"));
			return sUrl;
		},
		sync : function() {
			// apply partial update hints
			if (!this.isNew()) {
				var changed = this.changedAttributes();
				//console.log("sync, changed attributes: " + changed);
				if (changed != false) {
					//console.log(_.keys(changed));
					this.set("changedAttributes", _.keys(changed));
				}
			}
			return Backbone.Model.prototype.sync.apply(this, arguments);
		},
		isPublic : function() {
			return this.constructor.isPublic(this);
		},
		getUseCaseContext : function(key) {
			return this.constructor.getUseCaseContext(key);
		},
		hasUseCase : function(key) {
			return this.constructor.hasUseCase(key);
		},
		getFields : function() {
			return this.constructor.getFields();
		},
		/**
		 * Retusn true if the model is just a search collection wrapper, false otherwise
		 */
		isSearchModel : function() {
			return this.wrappedCollection ? true : false;
		},
		getBaseFragment : function() {
			return this.constructor.getBaseFragment(this);
		},
		/**
		 * Get the URL path fragment for this model. Calls the prototype method with the same name.
		 * @returns the URL path fragment as a string
		 */
		getPathFragment : function() {
			return this.constructor.getPathFragment();
		},
		/**
		 *  Check if the model wants search result collections of it's type to be cached.
		 *  Calls the prototype method with the same name.
		 */
		isCollectionCacheable : function() {
			return this.constructor.isCollectionCacheable && this.constructor.isCollectionCacheable();
		},
		getUseCaseContext : function(options) {
      options.model = this;
			return this.constructor.getUseCaseContext(options);
		},
		getTypeaheadSource : function(options) {
			return this.constructor.getTypeaheadSource(options);
		},

	}, {
		// static members
		/** (Default) 0Do not retrieve the form schema from the server */
		FORM_SCHEMA_CACHE_CLIENT : "FORM_SCHEMA_CACHE_CLIENT",
		/** Retrieve the form schema only once for all model instances */
		FORM_SCHEMA_CACHE_STATIC : "FORM_SCHEMA_CACHE_STATIC",
		/** Retrieve the form schema only once per model instance */
		FORM_SCHEMA_CACHE_INSTANCE : "FORM_SCHEMA_CACHE_INSTANCE",
		/** Retrieve the form schema every time it is accessed */
		FORM_SCHEMA_CACHE_NONE : "FORM_SCHEMA_CACHE_NONE",
		formSchemaCacheMode : this.FORM_SCHEMA_CACHE_CLIENT,
		typeName : "Calipso.Model",
		superClass : null,
		label : "GenericModel",
		showInMenu : false,
		public : false,
		nameProperty : "name",
		baseFragment : '/api/rest/',
		typeaheadSources : {},
		isPublic : function() {
			return this.public || false;
		},
		create : function(attrs, options) {
      var modelAttributes = attrs;
      if(options && options.httpParams){
        var params = _.isString(options.httpParams) ? Calipso.getHttpUrlParams(options.httpParams) : options.httpParams;
        modelAttributes = _.extend(params);
  		}
			var model = new this(modelAttributes, options);
      if (!modelAttributes.id && this.getTypeName() != "Calipso.model.UserDetailsModel") {
  			var collectionOptions = {
  				model : this,
  				url : Calipso.getBaseUrl() + this.baseFragment + this.getPathFragment(),
  			};
  			if (options.httpParams) {
  				collectionOptions.data = options.httpParams;
  			}
  			// create a model to use as a wrapper for a collection of
  			// instances of the same type, fill it with any given search criteria
  			model.wrappedCollection = Calipso.util.cache.getCollection(collectionOptions);
  		}
      return model;
		},
		isPublic : function() {
			return this.public;
		},
		isCollectionCacheable : function() {
			return false;
		},
		getBaseFragment : function() {
			return this.baseFragment;
		},
		/**
		 * Get the path fragment of this class
		 * @returns the the path fragment as a string
		 */
		getPathFragment : function(instance) {
			//console.log("GenericModel.getPathFragment returns: " + this.pathFragment);
			return this.pathFragment;
		},
		// TODO: refactor view to region names to
		// allow multiple views config peer layout
		fields : {},
		fieldNames : [],
		useCases : {
			create : {
				view : Calipso.view.BrowseLayout,
				viewOptions : {
					closeModalOnSync : true,
          formTemplatesKey : "horizontal",
				}
			},
			update : {
				view : Calipso.view.BrowseLayout,
				viewOptions : {
					closeModalOnSync : true,
          formTemplatesKey : "horizontal",
				}
			},
			search : {
				view : Calipso.view.UseCaseSearchLayout,
				viewOptions : {
          formTemplatesKey : "vertical",
				}
			},

		},
		_getUseCaseConfig : function(key) {
      // get superclass config
			var useCaseConfig = this.superClass && this.superClass._getUseCaseConfig ? this.superClass._getUseCaseConfig(key) : {};
      // apply own config
      this.useCases && Calipso.deepExtend(useCaseConfig, this.useCases[key]);
      return useCaseConfig;
		},
		getUseCaseContext : function(options) {
			var useCaseConfig = this._getUseCaseConfig(options.key);
      Calipso.deepExtend(useCaseConfig.viewOptions, options.viewOptions);
      // setup a model instance if needed
      useCaseConfig.model = options.model || this.create({id : options.modelId}, {httpParams : options.httpParams});
      useCaseConfig.factory = this;
      useCaseConfig.addToCollection = options.addToCollection;
      useCaseConfig.key = options.key;
      useCaseConfig.pathFragment = this.getPathFragment();
      return new Calipso.UseCaseContext(useCaseConfig);
		},
		hasUseCase : function(key) {
			var has = false;
			if (this.useCases[key] || (this.superClass && this.superClass.hasUseCase && this.superClass.hasUseCase(key))) {
				has = true;
			}
			return has;
		},
		getFields : function() {

			var fields = this.superClass && this.superClass.getFields ? this.superClass.getFields() : {};
			var ownFields = this.fields ? _.clone(this.fields) : {};
			Calipso.deepExtend(fields, ownFields);
			return fields;
		},
		getFieldNames : function() {
			var _this = this;
			if (!this.fieldNames) {
				_.each(this.fields, function(field, key) {
					_this.fieldNames.push(key);
				});
			}
			return this.fieldNames;
		},
		getTypeaheadSource : function(options) {
			var _this = this;
			var config = {
				query : "?name=%25wildcard%25",
				wildcard : "wildcard",
				pathFragment : _this.getPathFragment(),
			};
			_.extend(config, options);
			var sourceKey = config.pathFragment + config.wildcard + config.query;
			// if not lready created
			if (!_this.typeaheadSources[sourceKey]) {
				var sourceUrl = Calipso.getBaseUrl() + baseFragment + config.pathFragment + config.query;
				//console.log(_this.getTypeName() + "#getTypeaheadSource creating new source for url " + sourceUrl);
				var bloodhound = new Bloodhound({
					remote : {
						url : sourceUrl,
						wildcard : config.wildcard,
						transform : function(response) {
							//console.log(_this.getTypeName() + ' transform', response.content);
							return response.content;
						}
					},
					identify : function(obj) {
						return obj.id;
					},
					queryTokenizer : Bloodhound.tokenizers.whitespace,
					datumTokenizer : function(d) {
						return Bloodhound.tokenizers.whitespace(d.name);
					},
				});
				bloodhound.initialize();
				_this.typeaheadSources[sourceKey] = bloodhound.ttAdapter();
			}

			return _this.typeaheadSources[sourceKey];
		},
	});

	// Role model
	// ---------------------------------------

  Calipso.model.RoleModel = Calipso.Model.extend(
  /** @lends Calipso.model.RoleModel.prototype */
  {
    toString : function() {
      return this.get("name");
    }
  }, {
    // static members
    label : "Role",
    pathFragment : "roles",
    typeName : "Calipso.model.RoleModel",
    formSchemaCacheMode : this.FORM_SCHEMA_CACHE_STATIC,

    fields : {
      name : {
        fieldType : "String",
        backgrid : {
          cell : Calipso.components.backgrid.ViewRowCell,
        }
      },
      description : {
        fieldType : "String",
      },
      edit : {
        fieldType : "Edit",
      },
    },
  });


	Calipso.model.UserModel = Calipso.Model.extend(
	/** @lends Calipso.model.UserModel.prototype */
	{
		toString : function() {
			return this.get("username");
		}
	//urlRoot : "/api/rest/users"
	}, {
		// static members
		label : "User",
		showInMenu : true,
		pathFragment : "users",
		typeName : "Calipso.model.UserModel",
		useCases : {
      view : {
				view : Calipso.view.UserProfileLayout,
			},
			create : {
				view : Calipso.view.UserRegistrationLayout,
				fieldIncludes : [ "firstName", "lastName", "email" ]
			},
			search : {
				view : Calipso.view.UseCaseSearchLayout,
        viewOptions : {
          formTemplatesKey : "vertical",
          fieldsSearchBox : ["username", "firstName", "lastName", "email"],
        },
				overrides : {
					backgrid : {
						fieldIncludes : [ "username", "firstName", "lastName", "edit" ],
					},
					form : {
						fieldIncludes : [ "username", "firstName", "lastName", "email" ],
						//viewOptions : {
            //  fieldsInitiallyShown : [ "username", "email" ],
            //},
					},
				}
			},
		},
		/*

			id : {
				fieldType : "Hidden",
			},
			email : {
				fieldType : "String",
			},
			resetPasswordToken : {
				fieldType : "String",
			},
			currentPassword : {
				fieldType : "CurrentPassword",
			},
			password : {
				fieldType : "Password",
			},
			passwordConfirmation : {
				fieldType : "ConfirmPassword",
			}
		*/
		fields : {
			username : {
				fieldType : "String",
				backgrid : {
					cell : Calipso.components.backgrid.ViewRowCell,
				}
			},

			firstName : {
				fieldType : "String",
			},
			lastName : {
				fieldType : "String",
			},
			email : {
				fieldType : "Email",
			},
			telephone : {
				fieldType : "Tel",
			},
			cellphone : {
				fieldType : "Tel",
			},
			active : {
				fieldType : "Boolean",
			},
			/*roles : {
				fieldType : "List",
			//"form" : {
			//	"listModel" : Calipso.model.RoleModel
			//}
      },*/
			edit : {
				fieldType : "Edit",
			},
		},

	});

/*
	Calipso.model.HostModel = Calipso.Model.extend({},
	// static members
	{
		label : "Host",
		pathFragment : "hosts",
		typeName : "Calipso.model.HostModel",
		fields : {
			"domain" : {
				fieldType : "Link",
				backgrid : {
					cell : Calipso.components.backgrid.ViewRowCell,
				}
			},
			edit : {
				fieldType : "Edit",
			},
		},
	});
  */


	//////////////////////////////////////////////////
	// More models
	//////////////////////////////////////////////////

	// Country model
	// ---------------------------------------
	Calipso.model.CountryModel = Calipso.Model.extend(
	/** @lends Calipso.model.RoleModel.prototype */
	{
		initialize : function() {
			Calipso.Model.prototype.initialize.apply(this, arguments);
			this.set("translatedName", Calipso.util.getLabels("countries." + this.get("id")));
		},
		toString : function() {
			return this.get("translatedName") || this.get("name");
		},
		text : function() {
			return this.get("translatedName") || this.get("name");
		}
	//urlRoot : "/api/rest/users"
	}, {
		// static members
		label : "Role",
		pathFragment : "countries",
		typeName : "Calipso.model.RoleModel",
		fields : {
			"name" : {
				fieldType : "String",
				form : {
					validators : [ 'required' ]
				}
			},
			"nativeName" : {
				fieldType : "String",
				form : {
					validators : [ 'required' ]
				}
			},
			"callingCode" : {
				fieldType : "String",
				form : {
					validators : [ 'required' ]
				}
			},
			"capital" : {
				fieldType : "String",
				form : {
					validators : [ 'required' ]
				}
			},
			"currency" : {
				fieldType : "String",
				form : {
					validators : [ 'required' ]
				}
			},
			"languages" : {
				fieldType : "String",
				form : {
					validators : [ 'required' ]
				}
			},
			"edit" : {
				fieldType : "Edit",
			},
		},
	});

	// Notification Model
	// -----------------------------------------
	Calipso.model.BaseNotificationModel = Calipso.Model.extend({},
	// static members
	{
		pathFragment : "baseNotifications",
		typeName : "Calipso.model.BaseNotificationModel",
	});

	Calipso.model.UserDetailsModel = Calipso.Model.extend(
	/** @lends Calipso.model.UserDetailsModel.prototype */
	{
		// TODO: move to usecases/labels
		getViewTitle : function() {
			var schemaKey = this.getFormSchemaKey();
			var title = "";
			if (schemaKey == "create") {
				title += "Login ";
			} else if (schemaKey.indexOf("update") == 0) {
				title += "Change Password ";
			}
			return title;
		},
		toString : function() {
			return this.get("username");
		},

	},
	// static members
	{
		public : true,
		pathFragment : "userDetails",
		baseFragment : '/apiauth/',
		typeName : "Calipso.model.UserDetailsModel",
		useCases : {
			login : {
				titleHtml : "<i class='fa fa-lock'></i> User Login",
				description : "To login, please enter your credentials below.",
				view : Calipso.view.UserDetailsLayout,
				fieldIncludes : [ "email", "password" ],
        childViewOptions : {
          template : Calipso.getTemplate("login")
        }
			},
			resetPassword : {
				titleHtml : "<i class='fa fa-lock'></i> Reset password",
				description : "To create a new password, please complete the form below.",
				view : Calipso.view.UserDetailsLayout,
				fieldIncludes : [ "email", "resetPasswordToken", "password", "passwordConfirmation" ],
				fields : {
					email : {
						hideNonEmpty : true
					},
					resetPasswordToken : {
						hideNonEmpty : true
					}
				}
			},
			forgotPassword : {// enter new password
				titleHtml : "<i class='fa fa-lock'></i> Forgot password",
				description : "Please enter your email address bellow. You will receive a confirmation email in your inbox with instructions to create a new password. ",
				view : Calipso.view.UserDetailsLayout,
				fieldIncludes : [ "email" ],
				defaultNext : "resetPassword",
			},
		},
		fields : {
			id : {
				fieldType : "Hidden",
			},
			email : {
				fieldType : "String",
			},
			resetPasswordToken : {
				fieldType : "String",
			},
			currentPassword : {
				fieldType : "CurrentPassword",
			},
			password : {
				fieldType : "Password",
			},
			passwordConfirmation : {
				fieldType : "ConfirmPassword",
			}
		},
		create : function(options) {
      console.log(this.getTypeName() + "#create singleton ");
			if (this._instance === undefined) {
				this._instance = new this(options);
			} else {
				this._instance.clear();
			}
			this._instance.set(options);
			return this._instance;
		},
	});



});
