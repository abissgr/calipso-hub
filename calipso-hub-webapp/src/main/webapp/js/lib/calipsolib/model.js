/*
 * calipso-hub-webapp - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
define([ 'jquery', 'underscore', 'bloodhound', 'typeahead', "lib/calipsolib/util", "lib/calipsolib/form",
		"lib/calipsolib/uifield", "lib/calipsolib/backgrid", "lib/calipsolib/view", 'handlebars', 'moment'],
	function ($, _, Bloodhoud, Typeahead, Calipso, CalipsoForm, CalipsoField, CalipsoGrid, CalipsoView, Handlebars, moment) {

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
				skipDefaultSearch: false,

				initialize: function () {
					Calipso.model.Model.prototype.initialize.apply(this, arguments);
				},
				toString: function () {
					return this.get(this.constructor.nameProperty) || this.get("name") || this.get("id");
				},
				/**
				 * Returns the URL for this model, giving precedence  to the collection URL if the model belongs to one,
				 * or a URL based on the model path fragment otherwise.
				 */
				url: function () {
					var sUrl = this.collection && _.result(this.collection, 'url') ? _.result(this.collection, 'url') : Calipso.getBaseUrl() + this.getBaseFragment() + this.getPathFragment() /*_.result(this, 'urlRoot')*/ || urlError();
					if (!this.isNew()) {
						sUrl = sUrl + (sUrl.charAt(sUrl.length - 1) === '/' ? '' : '/') + encodeURIComponent(this.get("id"));
					}
					return sUrl;
				},
				sync: function () {
					console.log("Calipso.Model#sync");
					// apply partial update hints
					if (!this.isNew()) {
						var changed = this.changedAttributes();
						if (changed != false) {
							this.set("changedAttributes", _.keys(changed));
						}
					}
					return Backbone.Model.prototype.sync.apply(this, arguments);
				},
				isPublic: function () {
					return this.constructor.isPublic(this);
				},
				getUseCaseContext: function (options) {
					options.model = this;
					return this.constructor.getUseCaseContext(options);
				},
				hasUseCase: function (key) {
					return this.constructor.hasUseCase(key);
				},
				getFields: function () {
					return this.constructor.getFields();
				},
				/**
				 * Retusn true if the model is just a search collection wrapper, false otherwise
				 */
				isSearchModel: function () {
					return this.wrappedCollection ? true : false;
				},
				getBaseFragment: function () {
					return this.constructor.getBaseFragment(this);
				},
				/**
				 * Get the URL path fragment for this model. Calls the prototype method with the same name.
				 * @returns the URL path fragment as a string
				 */
				getPathFragment: function () {
					return this.constructor.getPathFragment();
				},
				/**
				 *  Check if the model wants search result collections of it's type to be cached.
				 *  Calls the prototype method with the same name.
				 */
				isCollectionCacheable: function () {
					return this.constructor.isCollectionCacheable && this.constructor.isCollectionCacheable();
				},
				getTypeaheadSource: function (options) {
					return this.constructor.getTypeaheadSource(options);
				},
			}, {
				// static members
				/** (Default) 0Do not retrieve the form schema from the server */
				FORM_SCHEMA_CACHE_CLIENT: "FORM_SCHEMA_CACHE_CLIENT",
				/** Retrieve the form schema only once for all model instances */
				FORM_SCHEMA_CACHE_STATIC: "FORM_SCHEMA_CACHE_STATIC",
				/** Retrieve the form schema only once per model instance */
				FORM_SCHEMA_CACHE_INSTANCE: "FORM_SCHEMA_CACHE_INSTANCE",
				/** Retrieve the form schema every time it is accessed */
				FORM_SCHEMA_CACHE_NONE: "FORM_SCHEMA_CACHE_NONE",
				formSchemaCacheMode: this.FORM_SCHEMA_CACHE_CLIENT,
				typeName: "Calipso.Model",
				superClass: null,
				labelIcon: "fa fa-list fa-fw",
				public: false,
				nameProperty: "name",
				baseFragment: '/api/rest/',
				typeaheadSources: {},
				menuConfig: {
					rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					rolesExcluded: null,
				},
				isPublic: function () {
					return this.public || false;
				},
				create: function (attrs, options) {
					var modelAttributes = attrs;

					if (options && options.httpParams) {
						var params = _.isString(options.httpParams) ? Calipso.getHttpUrlParams(options.httpParams) : options.httpParams;
						_.extend(modelAttributes, params);
					}
					var model = new this(modelAttributes, options);
					if (!modelAttributes.id && this.getTypeName() != "Calipso.model.UserDetailsModel") {
						var collectionOptions = {
							model: this,
							url: Calipso.getBaseUrl() + this.baseFragment + this.getPathFragment(),
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
				isPublic: function () {
					return this.public;
				},
				isCollectionCacheable: function () {
					return false;
				},
				getBaseFragment: function () {
					return this.baseFragment;
				},
				/**
				 * Get the path fragment of this class
				 * @returns the the path fragment as a string
				 */
				getPathFragment: function (instance) {
					return this.pathFragment;
				},
				// TODO: refactor view to region names to
				// allow multiple views config peer layout
				fields: {},
				fieldNames: [],
				useCases: {
					view: {
						view: Calipso.view.BrowseLayout,
						viewOptions: {
							closeModalOnSync: true,
							formTemplatesKey: "horizontal",
						}
					},
					create: {
						view: Calipso.view.BrowseLayout,
						viewOptions: {
							closeModalOnSync: true,
							formTemplatesKey: "horizontal",
						}
					},
					update: {
						view: Calipso.view.BrowseLayout,
						viewOptions: {
							closeModalOnSync: true,
							formTemplatesKey: "horizontal",
						}
					},
					search: {
						view: Calipso.view.UseCaseSearchLayout,
						viewOptions: {
							formTemplatesKey: "vertical",
						}
					},
				},
				_getUseCaseConfig: function (key) {
					// get superclass config
					var useCaseConfig = this.superClass && this.superClass._getUseCaseConfig ? this.superClass._getUseCaseConfig(key) : {};
					// apply own config
					this.useCases && Calipso.deepExtend(useCaseConfig, this.useCases[key]);
					return useCaseConfig;
				},
				getUseCaseContext: function (options) {
					var useCaseConfig = this._getUseCaseConfig(options.key);
					Calipso.deepExtend(useCaseConfig.viewOptions, options.viewOptions);
					// setup a model instance if needed
					useCaseConfig.model = options.model ? options.model : this.create({
						id: options.modelId
					}, {
						httpParams: options.httpParams
					});

					useCaseConfig.factory = this;
					useCaseConfig.addToCollection = options.addToCollection;
					useCaseConfig.key = options.key;
					useCaseConfig.pathFragment = this.getPathFragment();
					return new Calipso.UseCaseContext(useCaseConfig);
				},
				hasUseCase: function (key) {
					var has = false;
					if (this.useCases[key] || (this.superClass && this.superClass.hasUseCase && this.superClass.hasUseCase(key))) {
						has = true;
					}
					return has;
				},
				getFields: function () {

					var fields = this.superClass && this.superClass.getFields ? this.superClass.getFields() : {};
					var ownFields = this.fields ? _.clone(this.fields) : {};
					Calipso.deepExtend(fields, ownFields);
					return fields;
				},
				getFieldNames: function () {
					var _this = this;
					if (!this.fieldNames) {
						_.each(this.fields, function (field, key) {
							_this.fieldNames.push(key);
						});
					}
					return this.fieldNames;
				},
				getTypeaheadSource: function (options) {
					var _this = this;
					var config = {
						query: "?name=%25wildcard%25",
						wildcard: "wildcard",
						pathFragment: _this.getPathFragment(),
					};
					_.extend(config, options);
					var sourceKey = config.pathFragment + config.wildcard + config.query;
					// if not lready created
					if (!_this.typeaheadSources[sourceKey]) {
						var sourceUrl = Calipso.getBaseUrl() + this.baseFragment + config.pathFragment + config.query;
						var bloodhound = new Bloodhound({
							remote: {
								url: sourceUrl,
								wildcard: config.wildcard,
								transform: function (response) {
									return response.content;
								}
							},
							identify: function (obj) {
								return obj.id;
							},
							queryTokenizer: Bloodhound.tokenizers.whitespace,
							datumTokenizer: function (d) {
								return Bloodhound.tokenizers.whitespace(d.name);
							},
						});

						bloodhound.initialize();
						_this.typeaheadSources[sourceKey] = bloodhound.ttAdapter();
					}

					return _this.typeaheadSources[sourceKey];
				},
			});


		Calipso.model.HostModel = Calipso.Model.extend(
			/** @lends Calipso.model.RoleModel.prototype */
			{
				toString: function () {
					return this.get("name");
				}
			}, {
				// static members
				labelIcon: "fa fa-server fa-fw",
				pathFragment: "hosts",
				typeName: "Calipso.model.HostModel",
				menuConfig: {
					rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					rolesExcluded: null,
				},


				fields: {
					name: {
						fieldType: "String",
					},
					description: {
						fieldType: "Text",
					},
					country: {
						fieldType: "RelatedModel",
						"pathFragment": "countries",
					},
					edit: {
						fieldType: "Edit",
					},
				},
			});


		Calipso.model.UserRegistrationCodeBatchModel = Calipso.Model.extend(
			/** @lends Calipso.model.UserRegistrationCodeBatchModel.prototype */
			{
				toString: function () {
					return this.get("name");
				}
			}, {
				// static members
				labelIcon: "fa fa-server fa-fw",
				pathFragment: "registrationCodeBatches",
				typeName: "Calipso.model.UserRegistrationCodeBatchModel",
				/*
				menuConfig: {
					rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					rolesExcluded: null,
				},
				 */
				useCases: {
					search: {
						fieldIncludes: ["name", "description", "batchSize", "available", "createdDate", "expirationDate", "csvExport", "edit"],
						rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					},
					create: {
						fieldIncludes: ["name", "description", "batchSize", "expirationDate"],
						rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					},
					update: {
						fieldIncludes: ["description", "expirationDate"],
						rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					},
				},
				fields: {
					name: {
						fieldType: "String",
					},
					description: {
						fieldType: "Text",
					},
					batchSize: {
						fieldType: "Integer",
					},
					available: {
						fieldType: "Integer",
					},
					createdDate: {
						fieldType: "Date",
					},
					expirationDate: {
						fieldType: "Date",
					},
					csvExport: {
						fieldType: "CsvExport",
					},
					edit: {
						fieldType: "Edit",
					},
				},
			});

		Calipso.model.UserRegistrationCodeModel = Calipso.Model.extend(
			/** @lends Calipso.model.UserRegistrationCodeBatchModel.prototype */
			{
				toString: function () {
					return this.get("id");
				}
			}, {
				// static members
				labelIcon: "fa fa-server fa-fw",
				pathFragment: "userRegistrationCodes",
				typeName: "Calipso.model.UserRegistrationCodeBatchModel",
				/*
				menuConfig: {
					rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					rolesExcluded: null,
				},
				 */
				useCases: {
					search: {
						fieldIncludes: ["batch", "id", "available"],
						rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					},
					create: {
						rolesExcluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR", "ROLE_USER"],
					},
					update: {
						rolesExcluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR", "ROLE_USER"],
					},
				},
				fields: {
					batch: {
						fieldType: "RelatedModel",
						"pathFragment": "registrationCodeBatches",
					},
					id: {
						fieldType: "String",
					},
					available: {
						fieldType: "Boolean",
					},
				},
			});
		/*
		 Calipso.util.UserInvitationRecepientModel = Backbone.Model.extend({
		 schema: {
		 firstName: {
		 type: 'Text',
		 validators: ['required']
		 },
		 lastName: {
		 type: 'Text',
		 validators: ['required']
		 },
		 email: {
		 type: 'Text',
		 validators: ['required', 'email']
		 },
		 },
		 //To string is how models in the list will appear in the "editor".
		 toString: function() {
		 var attrs = this.attributes;
		 return attrs.firstName + ' ' + attrs.lastName + '&lt;' + attrs.email + '&gt;';
		 }
		 });
		*/
		Calipso.model.UserInvitationsModel = Calipso.Model.extend(
			/** @lends Calipso.model.RoleModel.prototype */
			{
				toString: function () {
					return this.get("name");
				}
			}, {
				// static members
				labelIcon: "fa fa-envelope-o fa-fw",
				pathFragment: "invitations",
				typeName: "Calipso.model.UserInvitationsModel",
				menuConfig: {
					rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					rolesExcluded: null,
				},
				useCases: {
					create: {
						view: Calipso.view.UserInvitationsLayout
					}
				},
				fields: {
					addressLines: {
						fieldType: "Text",
					},
					recepients: {
						form: {
							type: 'List',
							itemType: 'NestedModel',
							model: Calipso.util.UserInvitationRecepientModel
						}
					},
				},
			});

		// Role model
		// ---------------------------------------

		Calipso.model.RoleModel = Calipso.Model.extend(
			/** @lends Calipso.model.RoleModel.prototype */
			{
				toString: function () {
					return this.get("name");
				}
			}, {
				// static members
				labelIcon: "fa fa-users fa-fw",
				pathFragment: "roles",
				typeName: "Calipso.model.RoleModel",
				menuConfig: {
					rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					rolesExcluded: null,
				},


				fields: {
					name: {
						fieldType: "String",
						backgrid: {
							cell: Calipso.components.backgrid.ViewRowCell,
						}
					},
					description: {
						fieldType: "String",
					},
					edit: {
						fieldType: "Edit",
					},
				},
			});


		Calipso.model.UserModel = Calipso.Model.extend(
			/** @lends Calipso.model.UserModel.prototype */
			{
				toString: function () {
					return this.get("username");
				}
				//urlRoot : "/api/rest/users"
			}, {
				// static members
				labelIcon: "fa fa-user fa-fw",
				public: true,
				pathFragment: "users",
				typeName: "Calipso.model.UserModel",
				menuConfig: {
					rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					rolesExcluded: null,
				},
				useCases: {
					view: {
						view: Calipso.view.UserProfileLayout,
					},
					register: {
						view: Calipso.view.UserRegistrationLayout,
						fieldIncludes: ["firstName", "lastName", "email"],
						overrides: {
							contentRegion: {
								viewOptions: {
									template: Calipso.getTemplate("UseCaseCardFormView"),
									title: Calipso.util.getLabels("tmpl.userRegistration.titleNewAccount") +
									'<a href="#" class="btn btn-secondary btn-sm btn-social btn-facebook  pull-right" title="' +
									Calipso.util.getLabels("tmpl.login.fbLinkAlt") +
									'"><i class="fa fa-facebook-f"></i> ' + Calipso.util.getLabels("tmpl.login.fbLink") + '</a>',
									message: Calipso.util.getLabels("tmpl.userRegistration.formHelpNewAccount"),
									placeHolderLabelsOnly: true,
									formControlSize: "lg",
									submitButton: '<i class="fa fa-user-plus" aria-hidden="true"></i> ' + Calipso.util.getLabels("calipso.words.register")
								},
							},
						},
					},
					search: {
						view: Calipso.view.UseCaseSearchLayout,
						viewOptions: {
							formTemplatesKey: "vertical",
							fieldsSearchBox: ["username", "firstName", "lastName", "email"],
						},
						overrides: {
							backgrid: {
								fieldIncludes: ["username", "firstName", "lastName", "edit"],
							},
							form: {
								fieldIncludes: ["username", "firstName", "lastName", "email"],
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
				fields: {
					username: {
						fieldType: "String",
						backgrid: {
							cell: Calipso.components.backgrid.ViewRowCell,
						}
					},

					firstName: {
						fieldType: "String",
					},
					lastName: {
						fieldType: "String",
					},
					email: {
						fieldType: "Email",
					},
					telephone: {
						fieldType: "Tel",
					},
					cellphone: {
						fieldType: "Tel",
					},
					active: {
						fieldType: "Boolean",
					},
					/*roles : {
					 fieldType : "List",
					 //"form" : {
					 //	"listModel" : Calipso.model.RoleModel
					 //}
					 },*/
					edit: {
						fieldType: "Edit",
					},
				},
			});


		//////////////////////////////////////////////////
		// More models
		//////////////////////////////////////////////////

		// Country model
		// ---------------------------------------
		Calipso.model.CountryModel = Calipso.Model.extend(
			/** @lends Calipso.model.RoleModel.prototype */
			{
				initialize: function () {
					Calipso.Model.prototype.initialize.apply(this, arguments);
					this.set("translatedName", Calipso.util.getLabels("countries." + this.get("id")));
				},
				toString: function () {
					return this.get("translatedName") || this.get("name");
				},
				text: function () {
					return this.get("translatedName") || this.get("name");
				}
				//urlRoot : "/api/rest/users"
			}, {
				// static members
				labelIcon: "fa fa-globe fa-fw",
				pathFragment: "countries",
				typeName: "Calipso.model.CountryModel",
				menuConfig: {
					rolesIncluded: ["ROLE_ADMIN", "ROLE_SITE_OPERATOR"],
					rolesExcluded: null,
				},
				fields: {
					"name": {
						fieldType: "String",
						form: {
							validators: ['required']
						}
					},
					"nativeName": {
						fieldType: "String",
						form: {
							validators: ['required']
						}
					},
					"callingCode": {
						fieldType: "String",
						form: {
							validators: ['required']
						}
					},
					"capital": {
						fieldType: "String",
						form: {
							validators: ['required']
						}
					},
					"currency": {
						fieldType: "String",
						form: {
							validators: ['required']
						}
					},
					"languages": {
						fieldType: "String",
						form: {
							validators: ['required']
						}
					},
				},
			});

		// Notification Model
		// -----------------------------------------
		Calipso.model.BaseNotificationModel = Calipso.Model.extend({},
			// static members
			{
				pathFragment: "baseNotifications",
				typeName: "Calipso.model.BaseNotificationModel",
				menuConfig: {
					rolesIncluded: [],
					rolesExcluded: null,
				},
			});

		Calipso.model.UserDetailsModel = Calipso.Model.extend(
			/** @lends Calipso.model.UserDetailsModel.prototype */
			{
				browseMenu: {},
				initialize: function () {
					Calipso.Model.prototype.initialize.apply(this, arguments);
					var _this = this;
					this.set("translatedName", Calipso.util.getLabels("countries." + this.get("id")));
					this.on('sync', function (model, response, options) {
						_this.onLogin(model, response, options);
					});
					this.on('error', function (model, response, options) {
						alert("Authentication failed!");
					});
				},
				onLogin: function (model, response, options) {
					// send logged in user on their way
					var fw = "home";
					if (Calipso.app.fw) {
						fw = Calipso.app.fw;
						Calipso.app.fw = null;
					}
					// reload the app if locale needs to be changed
					var userLocale = this.get("locale");
					var oldLocale = localStorage.getItem("locale");

					// change locale?
					if (!oldLocale || (oldLocale && oldLocale != userLocale)) {
						localStorage.setItem("locale", this.userDetails.get("locale"));
						Calipso.navigate(fw, {
							trigger: false
						});
						window.location.reload();
					} else {
						// is the application started?
						if (Calipso.app.isStarted()) {
							if (this.get("id")) {
								Calipso.app.updateHeaderFooter();
								Calipso.navigate(fw, {
									trigger: true
								});
							} else {
								alert("Invalid credentials")
							}
						} else {
							Calipso.app.start(Calipso.getConfigProperty("startOptions"));
						}
					}

				},
				buildBrowseMenu: function () {
					var _this = this;
					var allModelLabels = Calipso.util.getLabels("models");
					var browseMenu = null;
					var parseModel = function (ModelType) {
						// setup model-based usecase factories
						if (ModelType.getTypeName() != "Calipso.model.Model" &&
							ModelType.getTypeName() != "Calipso.model.UserRegistrationModel" &&
							ModelType.getTypeName() != "Calipso.model.UserDetailsModel" &&
							ModelType.getTypeName() != "Calipso.model.GenericModel") {

							// build "browse" menu
							if (ModelType.menuConfig) {
								var rolesIncluded = ModelType.menuConfig.rolesIncluded;
								var rolesExcluded = ModelType.menuConfig.rolesExcluded || {};
								// if inclusions are passed or empty
								if (!rolesIncluded || Calipso.isUserInAnyRole(rolesIncluded)) {
									// and exclusions have no match
									if (!Calipso.isUserInAnyRole(rolesExcluded)) {
										browseMenu || (browseMenu = {});
										var modelLabels = allModelLabels[ModelType.getPathFragment()] || {};
										browseMenu[ModelType.getPathFragment()] = {
											label: ModelType.label || Calipso.util.getLabel(ModelType.getPathFragment() + ".plural.label", allModelLabels),
											labelIcon: ModelType.labelIcon,
										}
									}
								}
							}

						}
					};
					_(Calipso.model).each(parseModel);
					_(Calipso.customModel).each(parseModel);
					_this.set("browseMenu", browseMenu);
				},
				// TODO: move to usecases/labels
				getViewTitle: function () {
					var schemaKey = this.getFormSchemaKey();
					var title = "";
					if (schemaKey == "create") {
						title += "Login ";
					} else if (schemaKey.indexOf("update") == 0) {
						title += "Change Password ";
					}
					return title;
				},
				toString: function () {
					return this.get("username");
				},
			},
			// static members
			{
				public: true,
				pathFragment: "userDetails",
				baseFragment: '/apiauth/',
				typeName: "Calipso.model.UserDetailsModel",
				useCases: {
					login: {
						view: Calipso.view.UserDetailsLayout,
						fieldIncludes: ["email", "password"],
						overrides: {
							contentRegion: {
								viewOptions: {
									template: Calipso.getTemplate("UseCaseCardFormView"),
									title: '<i class="fa fa-lock"></i> ' + Calipso.util.getLabels("useCases.userDetails.login.title") +
									'<div class="btn-group btn-group-sm pull-right" role="group">\
              <a class="btn btn-secondary" href="/userDetails/forgotPassword">' +
									Calipso.util.getLabels("useCases.userDetails.login.forgotPassword") + '</a>\
                  <a class="btn btn-secondary" href="/useCases/users/register">' +
									Calipso.util.getLabels("useCases.userDetails.login.newUser") + '</a>\
                </div>',
									message: Calipso.util.getLabels("useCases.userDetails.login.message"),
									placeHolderLabelsOnly: true,
									formControlSize: "lg",
									submitButton: '<i class="fa fa-sign-in" aria-hidden="true"></i> ' + Calipso.util.getLabels("calipso.words.login")
								}
							}
						},
					},
					forgotPassword: { // request reselt link by email
						view: Calipso.view.UserDetailsLayout,
						fieldIncludes: ["email"],
						defaultNext: "resetPassword",
						overrides: {
							contentRegion: {
								viewOptions: {
									template: Calipso.getTemplate("UseCaseCardFormView"),
									title: "<i class='fa fa-lock'></i> " + Calipso.util.getLabels("useCases.userDetails.login.forgotPassword"),
									message: Calipso.util.getLabels("useCases.userDetails.forgotPassword.message"),
									placeHolderLabelsOnly: true,
									formControlSize: "lg",
									submitButton: '<i class="fa fa-sign-in" aria-hidden="true"></i> ' + Calipso.util.getLabels("useCases.userDetails.forgotPassword.submitButton")
								}
							}
						},
					},
					resetPassword: { // enter new password
						view: Calipso.view.UserDetailsLayout,
						fieldIncludes: ["email", "resetPasswordToken", "password", "passwordConfirmation"],
						fields: {
							email: {
								form: {
									hideNonEmpty: true
								}
							},
							resetPasswordToken: {
								form: {
									hideNonEmpty: true
								}
							}
						},
						overrides: {
							contentRegion: {
								viewOptions: {
									template: Calipso.getTemplate("UseCaseCardFormView"),
									title: "<i class='fa fa-lock'></i> " + Calipso.util.getLabels("useCases.userDetails.resetPassword.title"),
									message: Calipso.util.getLabels("useCases.userDetails.resetPassword.message"),
									placeHolderLabelsOnly: true,
									formControlSize: "lg",
									submitButton: '<i class="fa fa-sign-in" aria-hidden="true"></i> ' + Calipso.util.getLabels("useCases.userDetails.resetPassword.submitButton")
								}
							}
						},
					},
				},
				fields : {
					id: {
						fieldType: "Hidden",
					},
					email : {
						fieldType: "String",
					},
					resetPasswordToken : {
						fieldType: "String",
					},
					currentPassword: {
						fieldType: "CurrentPassword",
					},
					password: {
						fieldType: "Password",
					},
					passwordConfirmation: {
						fieldType: "ConfirmPassword",
					}
				},
				create: function (options) {
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