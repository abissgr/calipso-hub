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

define([ 'jquery', 'underscore', "lib/calipsolib/util", "lib/calipsolib/form", "lib/calipsolib/uifield", "lib/calipsolib/backgrid", "lib/calipsolib/view", 'handlebars', 'moment' ], function($, _, Calipso, CalipsoForm, CalipsoField, CalipsoGrid, CalipsoView, Handlebars, moment) {

	//////////////////////////////////////////
	// Models
	//////////////////////////////////////////
	/**
	 * Abstract model implementation to extend through your own models.
	 * Subclasses of this model should follow the model driven
	 * design conventions used in the Calipso backbone stack, note the " REQUIRED" parts
	 * of the example for details. Properly extending this class allows
	 * "model driven" routes, forms, grids and selection of item/collection/layout views.
	 *
	 * @constructor
	 * @requires Backbone
	 * @requires Backgrid
	 * @augments module:Backbone.Model
	 */
	Calipso.model.GenericModel = Backbone.Model.extend(
	/** @lends Calipso.model.GenericModel.prototype */
	{
		isPublic : function(){
			return this.constructor.isPublic(key);
		},
		getUseCase : function(key) {
			return this.constructor.getUseCase(key);
		},
		getFields : function() {
			return this.constructor.getFields();
		},
		getFormSubmitButton : function() {
			return null;
		},
		getViewTitle : function() {
			var schemaKey = this.getFormSchemaKey();
			var title = "";
			if (schemaKey.indexOf("create") == 0) {
				title += "New ";
			}
			if (schemaKey.indexOf("update") == 0) {
				title += "Edit ";
			}

			if (this.get("name")) {
				title += this.get("name");
			} else if (this.constructor.label) {
				title += this.constructor.label;
			}

			return title;
		},
		skipDefaultSearch : false,
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
		/**
		 * Retusn true if the model is just a search collection wrapper, false otherwise
		 */
		isSearchModel : function() {
			return this.wrappedCollection ? true : false;
		},
		getBaseFragment : function() {
			return this.constructor.getBaseFragment(this);
		},
		/*
		 * Will return <code>search</code> if the model is a search model,
		 * <code>create</code> if the model is new ans not a search model,
		 * <code>update</code> otherwise. The method is used to choose an appropriate
		 * form schema during form generation, see GenericFormView
		 */
		getFormSchemaKey : function() {
			var formSchemaKey = this.get("formSchemaKey");
			if (!formSchemaKey) {
				if (this.isSearchModel()) {
					formSchemaKey = "search";
				} else {
					formSchemaKey = this.isNew() ? "create" : "update";
				}
			}
			return formSchemaKey;
		},
		getFormTemplateKey : function() {
			var schemaKey = this.getFormSchemaKey();
			var formTemplateKey = "vertical";
			if (schemaKey.indexOf("report") == 0) {
				formTemplateKey = "nav";
			}
			return formTemplateKey;
		},
		/**
		 * Get the URL path fragment for this model. Calls the prototype method with the same name.
		 * @returns the URL path fragment as a string
		 */
		getPathFragment : function() {
			return this.constructor.getPathFragment();
		},
		/**
		 * Get the name of this class. Calls the prototype method with the same name.
		 * TODO: switch to named constructors
		 * @returns the class name as a string
		 */
		getTypeName : function() {
			return this.constructor.getTypeName();
		},
		/**
		 *  Check if the model wants search result collections of it's type to be cached.
		 *  Calls the prototype method with the same name.
		 */
		isCollectionCacheable : function() {
			return this.constructor.isCollectionCacheable && this.constructor.isCollectionCacheable();
		},
		/**
		 * Get the complete set of form schemas. You can also obtain the form schema for
		 * a specific action like "create", "update" or "search" using
		 * {@linkcode getFormSchema} instead.
		 *
		 * To define form schemas for your subclass under a static or instance context on the client-side, override
		 * {@link Calipso.model.GenericMogetFormSchemasgetFormSchemas} or {@link getFormSchemas} respectively.
		 *
		 * This method will attempt to retrieve the model schema in the following order:
		 * 	<ul><li>Schema set to the model by the server</li>
		 * 	<li>schemas defined by the model's prototype object</li>
		 * 	</ul>
		 *
		 * Form schemas are picked up by form views like {@linkcode GenericFormView} or layout views
		 * that use such form views in their regions.
		 *
		 * @see {@link Calipso.model.GenericModel.getFormSchemas}
		 */
		getFormSchemas : function() {
			return this.constructor.getFormSchemas(this);
		},
		getFormActions : function() {
			return this.constructor.getFormActions(this);
		},
		isRequired : function(schema) {
			var required = schema.required;
			if (!required && schema.validators) {
				required = $.inArray('required', schema.validators) > -1;
			}
			return required;
		},
		initialize : function() {
			Backbone.Model.prototype.initialize.apply(this, arguments);
			var thisModel = this;
			// make any submit button available to templates
			if (this.getFormSubmitButton()) {
				this.set("calipsoFormSubmitButton", this.getFormSubmitButton());
			}
			/*
			this.on("change", function(model, options) {
				console.log("Model on change, saving self");
				if (options && options.save === false) {
					return;
				}
			});
			*/
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
		typeName : "Calipso.model.GenericModel",
		label : "GenericModel",
		showInMenu : false,
		public : false,
		businessKey : "name",
		baseFragment : '/api/rest/',
		typeaheadSources : {},
		isPublic : function(){
			return this.public || false;
		},
		create : function(attrs, options) {
			return new this(attrs, options);
		},
		isPublic : function() {
			return this.public;
		},
		isCollectionCacheable : function() {
			return false;
		},
		/**
		 * Get the name of this class
		 * @returns the class name as a string
		 */
		getTypeName : function(instance) {
			return this.typeName;
		},
		getBaseFragment : function() {
			return this.baseFragment;
		},
		/**
		 * Get the path fragment of this class
		 * @returns the class name as a string
		 */
		getPathFragment : function(instance) {
			//console.log("GenericModel.getPathFragment returns: " + this.pathFragment);
			return this.pathFragment;
		},
		// TODO: refactor view to region names to
		// allow multiple views config peer layout
		useCases : {
			create : {
				view : Calipso.view.BrowseLayout,
				viewOptions : {
					childViewOptions : {
						formOptions : {
							submitButton : "WOW"
						}
					}
				},
			},
			update : {
				view : Calipso.view.BrowseLayout,
				viewOptions : {
					childViewOptions : {
						formOptions : {
							submitButton : "WOW"
						}
					}
				}
			},
			search : {
				view : Calipso.view.SearchLayout,
			},

		},

		getUseCase : function(key) {
			return $.extend({}, this.useCases[key]);
		},

		getFields : function() {
			//console.log("GenericModel.getPathFragment returns: " + this.pathFragment);
			var fields = $.extend({}, this.fields);
			return fields;
		},
		fieldNames : [],
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
				var sourceUrl = Calipso.getBaseUrl() + "/api/rest/" + config.pathFragment + config.query;
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

	/**
	 * Encance the extend function to properly merge the
	 * static "fields" and "useCases" hashes
	 */

	Calipso.model.GenericModel.extend = function(protoProps, staticProps) {
		var _this = this;
		_.each([ "fields", "useCases" ], function(mergableStaticProp) {

			if (!_.isUndefined(staticProps[mergableStaticProp]) && !_.isUndefined(_this[mergableStaticProp])) {
				_.each(_.keys(_this[mergableStaticProp]), function(key) {
					staticProps[mergableStaticProp][key] = $.extend({}, (_this[mergableStaticProp][key] || {}), (staticProps[mergableStaticProp][key] || {}));
				});
			}
		});
		return Backbone.Model.extend.apply(this, arguments);
		;
	};

	// Role model
	// ---------------------------------------
	Calipso.model.RoleModel = Calipso.model.GenericModel.extend(
	/** @lends Calipso.model.RoleModel.prototype */
	{
		toString : function() {
			return this.get("name");
		}
	//urlRoot : "/api/rest/users"
	}, {
		// static members
		parent : Calipso.model.GenericModel,
		label : "Role",
		pathFragment : "roles",
		typeName : "Calipso.model.RoleModel",
		formSchemaCacheMode : this.FORM_SCHEMA_CACHE_STATIC,

		fields : {
			name : {
				"datatype" : "String",
				backgrid : {
					cell : Calipso.components.backgrid.ViewRowCell,
				}
			},
			description : {
				"datatype" : "String",
			},
			edit : {
				"datatype" : "Edit",
			},
		},
	});

	Calipso.model.UserModel = Calipso.model.GenericModel.extend(
	/** @lends Calipso.model.UserModel.prototype */
	{
		toString : function() {
			return this.get("username");
		}
	//urlRoot : "/api/rest/users"
	}, {
		// static members
		parent : Calipso.model.GenericModel,
		label : "User",
		showInMenu : true,
		pathFragment : "users",
		typeName : "Calipso.model.UserModel",
		fields : {
			username : {
				"datatype" : "String",
				backgrid : {
					cell : Calipso.components.backgrid.ViewRowCell,
				}
			},

			firstName : {
				"datatype" : "String",
			},
			lastName : {
				"datatype" : "String",
			},
			email : {
				"datatype" : "Email",
			},
			telephone : {
				"datatype" : "Tel",
			},
			cellphone : {
				"datatype" : "Tel",
			},
			active : {
				"datatype" : "Boolean",
			},
			roles : {
				"datatype" : "List",
				"form" : {
					"listModel" : Calipso.model.RoleModel
				}
			},
			edit : {
				"datatype" : "Edit",
			},
		},

	});

	Calipso.model.HostModel = Calipso.model.GenericModel.extend({},
	// static members
	{
		parent : Calipso.model.GenericModel,
		label : "Host",
		typeName : "Calipso.model.HostModel",
		fields : {
			"domain" : {
				"datatype" : "Link",
				backgrid : {
					cell : Calipso.components.backgrid.ViewRowCell,
				}
			},
			edit : {
				"datatype" : "Edit",
			},
		},
		getPathFragment : function() {
			return "hosts";
		},

	});
	Calipso.model.UserProfileModel = Calipso.model.UserModel.extend(
	/** @lends Calipso.model.UserDetailsModel.prototype */
	{
		skipDefaultSearch : false
	},
	// static members
	{
		parent : Calipso.model.UserModel,
		viewFragment : "userProfile",
		typeName : "Calipso.model.UserProfileModel",
		layoutViewType : Calipso.view.UserProfileLayout,
		itemViewType : Calipso.view.UserProfileView,
	});

	//////////////////////////////////////////////////
	// More models
	//////////////////////////////////////////////////

	// Country model
	// ---------------------------------------
	Calipso.model.CountryModel = Calipso.model.GenericModel.extend(
	/** @lends Calipso.model.RoleModel.prototype */
	{
		initialize : function() {
			Calipso.model.GenericModel.prototype.initialize.apply(this, arguments);
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
		parent : Calipso.model.GenericModel,
		label : "Role",
		pathFragment : "countries",
		typeName : "Calipso.model.RoleModel",
		formSchemas : {//
			name : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			nativeName : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			callingCode : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			capital : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			currency : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
			languages : {
				"search" : 'Text',
				"default" : {
					type : 'Text',
					validators : [ 'required' ]
				}
			},
		},
		gridSchema : [ {
			name : "name",
			label : "Name",
			cell : Calipso.components.backgrid.ViewRowCell,
			editable : false
		}, {
			name : "nativeName",
			label : "Native name",
			editable : false,
			cell : "string"
		}, {
			name : "callingCode",
			label : "Calling code",
			editable : false,
			cell : "string"
		}, {
			name : "capital",
			label : "Capital",
			editable : false,
			cell : "string"
		}, {
			name : "currency",
			label : "Currency",
			editable : false,
			cell : "string"
		}, {
			name : "languages",
			label : "Languages",
			editable : false,
			cell : "string"
		}, {
			name : "edit",
			label : "",
			editable : false,
			cell : Calipso.components.backgrid.EditRowInModalCell,
			headerCell : Calipso.components.backgrid.CreateNewInModalHeaderCell
		} ],
	});

	// Notification Model
	// -----------------------------------------
	Calipso.model.BaseNotificationModel = Calipso.model.GenericModel.extend({},
	// static members
	{
		parent : Calipso.model.GenericModel,
		pathFragment : "baseNotifications",
		typeName : "Calipso.model.BaseNotificationModel",
	});

	Calipso.model.UserDetailsModel = Calipso.model.GenericModel.extend(
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
		/*
		sync : function(method, model, options) {
			this.set("id", null);
			var _this = this;
			options = options || {};
			options.timeout = 30000;
			if (!options.url) {
				options.url = Calipso.getBaseUrl() +
				Calipso.getConfigProperty("apiAuthPath") + "/" +
				_this.getPathFragment(); // + "/" + _this.getΙδ()	;
			}
			// options.dataType = "jsonp"; // JSON is default.
			return Backbone.sync(method, model, options);
		}
		*/

	},
	// static members
	{
		parent : Calipso.model.GenericModel,
		public : true,
		pathFragment : "userDetails",
		baseFragment : '/apiauth/',
		typeName : "Calipso.model.UserDetailsModel",
		useCases : {
			login : {
				view : Calipso.view.UserDetailsLayout,
				fieldIncludes : ["email", "password" ],
			},
			resetPassword : {
				view : Calipso.view.UserDetailsLayout,
				fieldIncludes : ["email", "resetPasswordToken", "password", "passwordConfirmation" ],
				fields : {
					email : {
						dataType : "Hidden"
					}
				}
			},
			forgotPassword : {// enter new password
				view : Calipso.view.UserDetailsLayout,
				fieldIncludes : ["email" ],
				defaultNext : "resetPassword",
			},
		},
		fields : {
			id : {
				"datatype" : "Hidden",
			},
			email : {
				"datatype" : "String",
			},
			resetPasswordToken : {
				"datatype" : "String",
			},
			currentPassword : {
				"datatype" : "CurrentPassword",
			},
			password : {
				"datatype" : "Password",
			},
			passwordConfirmation : {
				"datatype" : "ConfirmPassword",
			}
		},
   	create: function () {
	   	if (this._instance === undefined) {
	    	this._instance = new this();
	    }
	    return this._instance;
	  },
		/*
		getFormSchemas : function(instance) {
			var passwordText = {
				type : 'Password',
				validators : [ 'required' ]
			};
			var passwordConfirm = {
				type : 'Password',
				validators : [ 'required', {
					type : 'match',
					field : 'password',
					message : 'Passwords must match!'
				} ],
			};
			// is a password reset token already present?
			var pwResetTokenPresent = instance && instance.get("resetPasswordToken");

			return {
				id : {
					"update" : {
						type : 'Hidden',
						hidden : true,
					}
				},
				isResetPasswordReguest : {
					"update-createToken" : {
						type : 'Hidden',
						hidden : true,
					},
				},
				email : {
					"create" : {
						type : 'Text',
						label : "Username or Email",
						validators : [ 'required' ],
					},
					"update-createToken" : {
						type : 'Text',
						validators : [ 'required', 'email' ],
					},
				},
				resetPasswordToken : {
					"create-withToken" : {
						type : pwResetTokenPresent ? "Hidden" : 'Text',
						validators : [ 'required' ]
					},

				},
				currentPassword : {
					"update" : {
						type : 'Password',
						validators : [ 'required', function checkPassword(value, formValues) {
							// verify current password
							var userDetails = new Calipso.model.UserDetailsModel({
								email : Calipso.session.userDetails.get("email"),
								password : value
							});
							userDetails.save(null, {
								async : false,
								url : Calipso.getBaseUrl() + Calipso.getConfigProperty("apiAuthPath") + "/verifyPassword",
							});
							var err = {
								type : 'password',
								message : 'Incorrect current password'
							};
							//console.log("checkPassword: ");
							//console.log(userDetails);
							if (!userDetails.get("id"))
								return err;
						} ],//validators
					},
				},
				password : {
					"create" : passwordText,
					"update" : {
						type : 'Password',
						validators : [ 'required' ],
					},
					"create-withToken" : {
						extend : "update",
					},
				},
				passwordConfirmation : {
					"update" : passwordConfirm,
					"create-withToken" : passwordConfirm,
				},
			};
		}*/
	});

	// User Registration Model
	// -----------------------

	/**
	 * Subclasses UserModel to provide layout, forms etc. configuration
	 * for user registration flows.
	 */
	Calipso.model.UserRegistrationModel = Calipso.model.GenericModel.extend(
	/** @lends Calipso.model.UserRegistrationModel */
	{
		label : "Register",
		getFormSubmitButton : function() {
			return "<i class=\"fa fa-floppy-o\"></i>&nbsp;Register"
		},
		initialize : function() {
			Calipso.model.GenericModel.prototype.initialize.apply(this, arguments);
			this.set("locale", Calipso.util.getLocale());
		},
	//getFormTemplateKey : function(){
	//	return "auth";
	//}
	}, {
		// static members
		parent : Calipso.model.UserModel,
		label : "Register",
		pathFragment : "users",
		typeName : "Calipso.model.UserRegistrationModel",
		getLayoutViewType : function(instance) {
			return Calipso.view.UserRegistrationLayout;
		},
		getFormSchemas : function(instance) {
			//console.log("UserRegistrationModel.getFormSchemas for " + instance.getTypeName());
			var requiredText = {
				type : 'Text',
				validators : [ 'required' ]
			};
			var passwordText = {
				type : 'Password',
				validators : [ 'required' ]
			};
			var passwordConfirm = {
				type : 'Password',
				validators : [ {
					type : 'match',
					field : 'password',
					message : 'Passwords must match!'
				} ]
			};
			return {
				locale : {
					"default" : {
						type : 'Hidden'
					},
				},
				firstName : {
					"create" : requiredText,
					"update" : requiredText
				},
				lastName : {
					"create" : requiredText,
					"update" : requiredText
				},
				username : {
					//"create" : requiredText,
					"update" : requiredText
				},
				email : {
					"search" : {
						type : 'Text',
						validators : [ 'email' ]
					},
					"default" : {
						type : 'Text',
						validators : [ 'required', 'email', Calipso.components.backboneform.validators.getUserEmailValidator(instance) ]
					},
				},
				password : {
					//"create" : passwordText,
					"update" : passwordText
				},
				passwordConfirm : {
					//"create" : passwordConfirm,
					"update" : passwordConfirm
				},
			};

		},

	});

	/*
		Calipso.model.UserDetailsConfirmationModel = Calipso.model.UserDetailsModel.extend(
		{
			getFormSubmitButton : function() {
				return "<i class=\"fa fa-floppy-o\"></i>&nbsp;Confirm"
			}
		}, {
			label : "Email Confirmation",

			pathFragment : "accountConfirmations",
			typeName : "Calipso.model.UserRegistrationModel",
			layoutViewType : Calipso.view.UserRegistrationLayout,
			getItemViewType : function() {
				return Calipso.view.GenericFormPanelView.extend({
					commit : function(e) {
						Calipso.stopEvent(e);
						if (!this.isFormValid()) {
							return false;
						}
						// if no validation errors,
						// use the email confirmation link route
						else {
							Calipso.navigate("accountConfirmations/" + this.model.get("confirmationToken"), {
								trigger : true
							});
						}
					}
				});
			},
			formSchemas : {
				confirmationToken : {
					"default" : {
						title : 'Please check your email for a confirmation key',
						type : 'Text',
						validators : [ 'required' ]
					}
				},
			},

		});
	*/
	// Report Dataset Model
	// This model is used by the router controller when a
	// subjectModelTypeFragment/reports
	// route is matched, where the subjectModelType matches a model type's URL fragent.
	// The controller uses the ReportDataSetModel
	// as the route model after configuring it with the targe rRoute model
	// type, from which the ReportDataSetModel obtains any custom configuration
	// for route layouts, views and form/grid schemas according to the following table:
	// ReportDataSet                    ModelType
	// getLayoutViewType()              getReportLayoutType()
	// getCollectionViewType()          getReportCollectionViewType()
	// getPathFragment()                getPathFragment() + "/reports"
	// getFormSchemaKey()               "report"
	// getReportKpiOptions()            getReportKpiOptions(this.get("reportType"/*URL param*/)
	// -----------------------------------------
	Calipso.model.ReportDataSetModel = Calipso.model.GenericModel.extend({
		subjectModelType : null,
		// TODO: inline form tmpl
		defaults : {
			formTemplateKey : "horizontal",
			kpi : "sum",
			timeUnit : "DAY",
			reportType : "Businesses",
			calipsoFormSubmitButton : "Show Report"
		},
		initialize : function() {
			Calipso.model.GenericModel.prototype.initialize.apply(this, arguments);
			//this.subjectModelType = options.subjectModelType;
			var subjectModelType = this.get("subjectModelType");
			//console.log("Calipso.model.ReportDataSetModel#initialize, subjectModelType: ");
			//console.log(subjectModelType);
			//console.log("Calipso.model.ReportDataSetModel#initialize, attributes: ");
			//console.log(this.attributes);
			if (!(_.isNull(subjectModelType) || _.isUndefined(subjectModelType))) {
				this.set("reportType", subjectModelType.getReportTypeOptions()[0]);
				var now = new Date();
				this.set("period", (now.getUTCMonth() + 1) + '/' + now.getUTCFullYear());
			}
		},
		getPathFragment : function() {
			return this.get("subjectModelType").getPathFragment() + "/reports";
		},
		getFormSchemaKey : function() {
			return "report";
		},
		getCollectionViewType : function() {
			return this.get("subjectModelType").getReportCollectionViewType();
		},
		getLayoutViewType : function() {
			return this.get("subjectModelType").getReportLayoutType ? this.get("subjectModelType").getReportLayoutType() : Calipso.view.ModelDrivenReportLayout;
		},
		getReportTypeOptions : function() {
			return this.get("subjectModelType").getReportTypeOptions ? this.get("subjectModelType").getReportTypeOptions() : null;
		},
		getReportKpiOptions : function(reportType) {
			var options;
			if (!reportType) {
				reportType = this.get("reportType");
			}

			if (this.get("subjectModelType").getReportKpiOptions) {
				options = this.get("subjectModelType").getReportKpiOptions(reportType);
			}

			if (!options) {
				options = [ {
					val : "sum",
					label : 'Sum'
				}, {
					val : "count",
					label : 'Count'
				} ];
			}
			return options;
		},
		getFormSchema : function(actionName) {
			//console.log("Calipso.model.ReportDataSetModel#getFormSchema actionName: " + actionName);
			var formSchema = {};
			var reportTypeOptions = this.getReportTypeOptions();
			if (reportTypeOptions) {
				formSchema.reportType = {
					title : "Report Type",
					type : 'Select',
					options : reportTypeOptions,
					template : this.fieldTemplate
				// TODO: validate option
				// validators : [ 'required' ]
				};
			}

			formSchema.kpi = {
				title : "KPI",
				type : 'Select',
				options : this.getReportKpiOptions(),
				template : this.fieldTemplate
			// TODO: validate option
			// validators : [ 'required' ]
			};
			formSchema.timeUnit = {
				title : "by",
				type : 'Select',
				options : [ {
					val : "DAY",
					label : 'Day'
				}, {
					val : "MONTH",
					label : 'Month'
				} ],
				template : this.fieldTemplate
			// TODO: validate option
			// validators : [ 'required' ]
			};

			formSchema.period = {
				title : "Period",
				type : Calipso.components.backboneform.Datetimepicker,
				template : this.fieldTemplate,
				config : {
					locale : Calipso.util.getLocale(),
					format : 'MM/YYYY',
					viewMode : 'months',
					widgetPositioning : {
						horizontal : "right"
					}
				},
				validators : [ 'required' ]
			};
			//console.log("Calipso.model.ReportDataSetModel#getFormSchema formSchema: ");
			//console.log(formSchema);
			return formSchema;
		},
		getGridSchema : function(kpi) {
			//console.log("Calipso.model.ReportDataSetModel#getGridSchema kpi: " + kpi);
			// sum or count
			if (!kpi) {
				kpi = this.get("kpi");
				//console.log("Calipso.model.ReportDataSetModel#getGridSchema this.kpi: " + kpi);
			}
			var schema = [ {
				name : "label",
				label : "",
				editable : false,
				cell : "text",
			} ];
			//console.log("Calipso.model.ReportDataSetModel#getGridSchema returns: ");
			var entries = this.wrappedCollection.first().get("entries");
			for (var i = 0; i < entries.length; i++) {
				schema.push({
					name : "entries." + i + ".entryData." + kpi,
					label : entries[i].label,
					editable : false,
					cell : Calipso.components.backgrid.ChildNumberAttributeCell,
				});
			}
			//console.log("Calipso.model.ReportDataSetModel#getGridSchema returns: ");
			//console.log(schema);
			return schema;
		},
		fieldTemplate : _.template('\
    <div class="form-group field-<%= key %>">&nbsp;\
      <label class="control-label" for="<%= editorId %>">\
        <% if (titleHTML){ %><%= titleHTML %>\
        <% } else { %><%- title %><% } %>\
      </label>&nbsp;\
     <span data-editor></span>\
    </div>&nbsp;\
  '),
	},
	// static members
	{
		parent : Calipso.model.GenericModel,
	});

	Calipso.model.ReportDataSetModel.getTypeName = function() {
		return "Calipso.model.ReportDataSetModel";
	};

	Calipso.model.ReportDataSetModel.getItemViewType = function() {
		return Calipso.view.ReportFormView;
	};

	Calipso.model.ReportDataSetModel.getCollectionViewType = function() {
		return Calipso.view.ModelDrivenReportView;
	};

});
