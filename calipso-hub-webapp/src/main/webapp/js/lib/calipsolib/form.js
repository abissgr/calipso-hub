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

define(
		[ "lib/calipsolib/form-templates", 'underscore', 'handlebars', 'moment', 'backbone', 'backbone-forms',
		'backbone-forms-bootstrap3', 'backbone-forms-select2', 'marionette',

		'bloodhound', 'typeahead', 'bootstrap-datetimepicker', 'bootstrap-switch', 'intlTelInput'],
		function( Calipso, _, Handlebars, moment, Backbone,
			BackboneForms, BackboneFormsBootstrap, BackboneFormsSelect2, BackboneMarionette,
			Bloodhoud, Typeahead, BackboneDatetimepicker, BootstrapSwitch, intlTelInput) {


	// inline rendering for checkbox
	Backbone.Form.editors.Checkbox.prototype.className = '';
	Calipso.components.backboneform = {};
	Calipso.components.backboneform.validators = {
		digitsOnly : function(value, formValues) {
			if (value) {
				var reg = /^\d+$/;
				if (!reg.test(value)) {
					return {
						message : 'Numerical digits only'
					};
				}
			}
		},
		getUserEmailValidator : function(instance){
			return function(value, formValues){
				// is there a value to validate?
				if(value){
					var usersCollection = new Calipso.collection.GenericCollection([], {
						model : Calipso.model.UserModel,
						data : {
							email : value
						}
					});
					// find users wit hthe given email
					usersCollection.fetch({
						async:false,
						url : usersCollection.url,
						data : usersCollection.data
					});
					if(usersCollection.length > 0){
						// if not the same user
						var formModelId = Calipso.getObjectProperty(instance, "id");
						//console.log("Validating EMAIL, current ID: " + formModelId + ", found ID: " + usersCollection.at(0).get("id"));
						if(!formModelId || formModelId != usersCollection.at(0).get("id")){
							return {
								type : "email",
								message : "A user with that email already exists"
							}
						}
					}
				}
			}
		}
	}
	/*
	Calipso.components.backboneformTemplates = {
			horizontal : _.template('\
				<div class="form-group field-<%= key %>">\
				<label class="col-sm-2 control-label" for="<%= editorId %>">\
				  <% if (titleHTML){ %><%= titleHTML %>\
				  <% } else { %><%- title %><% } %>\
				</label>\
				<div class="col-sm-10">\
				  <span data-editor></span>\
				  <p class="help-block" data-error></p>\
				  <p class="help-block"><%= help %></p>\
				</div>\
				</div>\
			'),
			vertical :  _.template('\
		    <div class="form-group field-<%= key %>">\
		      <label class="control-label" for="<%= editorId %>">\
		        <% if (titleHTML){ %><%= titleHTML %>\
		        <% } else { %><%- title %><% } %>\
		      </label>\
		      <div class="">\
		        <span data-editor></span>\
		        <p class="help-block" data-error></p>\
		        <p class="help-block"><%= help %></p>\
		      </div>\
		    </div>\
		  '),
			inline :  _.template('\
		    <div class="form-group field-<%= key %>">\
		      <label class="control-label" for="<%= editorId %>">\
		        <% if (titleHTML){ %><%= titleHTML %>\
		        <% } else { %><%- title %><% } %>\
		      </label>\
		      <div class="">\
		        <span data-editor></span>\
		        <p class="help-block" data-error></p>\
		        <p class="help-block"><%= help %></p>\
		      </div>\
		    </div>\
		  ')
	};
	*/

	Calipso.components.backboneform.Form = Backbone.Form.extend({
		hintRequiredFields : true,
		capitalizeKeys : true,

		/**
		 * Constructor
		 *
		 * @param {Object} [options.schema]
		 * @param {Backbone.Model} [options.model]
		 * @param {Object} [options.data]
		 * @param {String[]|Object[]} [options.fieldsets]
		 * @param {String[]} [options.fields]
		 * @param {String} [options.idPrefix]
		 * @param {Form.Field} [options.Field]
		 * @param {Form.Fieldset} [options.Fieldset]
		 * @param {Function} [options.template]
		 * @param {Boolean|String} [options.submitButton]
		 * @param {Boolean|String} [options.hintRequiredFields]
		 */
		initialize : function(options) {
			options || (options = {});
			var hintRequiredFields = options.hintRequiredFields;
			if (!_.isUndefined(hintRequiredFields)) {
				this.hintRequiredFields = hintRequiredFields;
			}
			this.fieldTemplate = options.fieldTemplate || Calipso.util.formTemplates["field-horizontal"];
			Backbone.Form.prototype.initialize.apply(this, arguments);
		},
		/**
		 * Get all the field values as an object.
		 * Use this method when passing data instead of objects.
		 * Extends superclass to exclude fields with excludeFromCommit set to true
		 *
		 * @param {String} [key]    Specific field value to get
		 */
		getValue : function(key) {
			//Return only given key if specified
			if (key)
				return this.fields[key].getValue();

			//Otherwise return entire form
			var values = {};
			_.each(this.fields, function(field) {
				if (!field.excludeFromCommit) {
					values[field.key] = field.getValue();
				}
			});

			return values;
		},
		createField: function(key, schema) {

			if(!schema.template && this.fieldTemplate){
	   	 schema.template = this.fieldTemplate;
	    }

			return Backbone.Form.prototype.createField.apply(this, arguments);;
		},
		getDraft : function(){
			var values = {};
			_.each(this.fields, function(field) {
				if (!field.excludeFromCommit) {
					var value = field.getValue();
					if(value && value.id){
						value = {id : Calipso.getObjectProperty(value, "id")};
					}
					values[field.key] = value;
				}
			});
			delete values.currentStepIndex;
			delete values.highestStepIndex;
			return values;
		},
		render : function() {
			var self = this, fields = this.fields, $ = Backbone.$;

			//Render form
			var $form = $($.trim(this.template(_.result(this, 'templateData'))));
			if (this.$el) {
				var attributes = $form.prop("attributes");
				// loop through <select> attributes and apply them on <div>
				$.each(attributes, function() {
					if (this.name == "class") {
						self.$el.addClass(this.value);
					} else if (this.name != "id") {
						self.$el.attr(this.name, this.value);
					}
				});
				this.$el.html($form.html());
				$form = this.$el;
			} else {
				this.setElement($form);
			}
			//Render standalone editors
			$form.find('[data-editors]').add($form).each(function(i, el) {
				var $container = $(el), selection = $container.attr('data-editors');
				if (_.isUndefined(selection))
					return;

				//Work out which fields to include
				var keys = (selection == '*') ? self.selectedFields || _.keys(fields) : selection.split(',');

				//Add them
				_.each(keys, function(key) {
					var field = fields[key];

					$container.append(field.editor.render().el);
				});
			});

			//Render standalone fields
			$form.find('[data-fields]').add($form).each(function(i, el) {
				var $container = $(el), selection = $container.attr('data-fields');

				if (_.isUndefined(selection))
					return;

				//Work out which fields to include
				var keys = (selection == '*') ? self.selectedFields || _.keys(fields) : selection.split(',');

				//Add them
				_.each(keys, function(key) {
					var field = fields[key];

					$container.append(field.render().el);
				});
			});

			//Render fieldsets
			$form.find('[data-fieldsets]').add($form).each(function(i, el) {
				var $container = $(el), selection = $container.attr('data-fieldsets');

				if (_.isUndefined(selection))
					return;

				_.each(self.fieldsets, function(fieldset) {
					$container.append(fieldset.render().el);
				});
			});

			//Set class
			$form.addClass(this.className);
			this.trigger("attach");
			return this;
		},
		close : function() {
			this.trigger("close");
			this.remove();
			this.unbind();
		},
		toJson : function() {
			var nodeName = this.$el[0].nodeName.toLowerCase();
			return _.reduce((nodeName == "form" ? this.$el : this.$("form")).serializeArray(), function(hash, pair) {
				if (pair.value) {
					hash[pair.name] = pair.value;
				}
				return hash;
			}, {});
		},
	});
	Calipso.components.backboneform.Form.Field = Backbone.Form.Field.extend({

		render : function() {
			var schema = this.schema, editor = this.editor, $ = Backbone.$;
			// pickup field template
			console.log("Calipso.components.backboneform.Form.Field schema.template: " + schema.template);
			console.log("Calipso.components.backboneform.Form.Field this.constructor.template: " + this.constructor.template);
			/*
			if(!schema.template && this.constructor.template){
				this.schema.template = this.constructor.template;
			}
			*/
			//Only render the editor if Hidden
			if (schema.type.ownRender) {
				return this.setElement(editor.render().el);
			} else {
				return Backbone.Form.Field.prototype.render.apply(this, arguments);
			}
		}
	}, {

	});

	Calipso.components.backboneform.Markup = Backbone.Form.editors.Hidden.extend({
		tagName : "div",
		excludeFromCommit : true,
		events : {},
		initialize : function(options) {
			Backbone.Form.editors.Hidden.prototype.initialize.call(this, options);
			var markup = this.schema.text;
			/*if(!markup){
				markup = this.schema.template ? this.schema.template(this) : this.constructor.html;
			}*/
			this.$el.removeAttr("class type");
			this.$el.html(markup);

		},
		getTemplate : function() {
			return this.schema.template || this.constructor.template;
		},
		validate : function() {
		},
		setValue : function() {
		},
		getValue : function() {
		},
		commit : function() {
		}
	}, {
		ownRender : true
	});

	Calipso.components.backboneform.Hr = Calipso.components.backboneform.Markup.extend({
		tagName : "hr",
	});

	Calipso.components.backboneform.P = Calipso.components.backboneform.Markup.extend({
		tagName : "p",
	}, {
		// static
		title : "No html or template was provided in schema"
	});

	Calipso.components.backboneform.PDanger = Calipso.components.backboneform.P.extend({
		className : "text-danger",
	});
	Calipso.components.backboneform.H3 = Calipso.components.backboneform.P.extend({
		tagName : "h3",
	});
	Calipso.components.backboneform.H4 = Calipso.components.backboneform.P.extend({
		tagName : "h4",
	});
	Calipso.components.backboneform.H5 = Calipso.components.backboneform.P.extend({
		tagName : "h5",
	});
	Calipso.components.backboneform.ListGroup = Calipso.components.backboneform.Markup.extend({
		tagName : "div",
		className : "list-group",
		initialize : function(options) {
			Backbone.Form.editors.Hidden.prototype.initialize.call(this, options);
			var html = "";
			// TODO: OR labels options
			if (this.schema.options) {
				for (var i = 0; i < options.schema.options.length; i++) {
					html += '<div class="list-group-item">' + '<h5 class"list-group-item-heading"><strong>' + this.schema.options[i].heading + '</strong></h5>' + '<p class="list-group-item-tex">' + this.schema.options[i].text + '</p></div>';
				}
			}
			this.$el.removeAttr("type class");
			this.$el.addClass("list-group");
			this.$el.html(html);

		},
	});

	Calipso.components.backboneform.Text = Backbone.Form.editors.Text.extend({
		config : {},
		initialize : function(options) {
			Backbone.Form.editors.Text.prototype.initialize.call(this, options);
			options = options || {};
			if(options.schema && options.schema.config){
				this.config = $.extend({}, this.config, options.schema.config);
			}
			if (this.form) {
				var _this = this;
				this.listenToOnce(this.form, "attach", function() {
					_this.onFormAttach();
				});
				this.listenToOnce(this.form, "close", function() {
					_this.onFormClose();
				});
			}
		},
		onFormAttach : function() {
			var _this = this;
			if (_this.schema.maxLength) {
				var pHelp = _this.$el.parent().parent().find('.help-block:not([data-error])').first();
				pHelp.append("<span class=\"chars-remaining\">" + _this.schema.maxLength + ' characters remaining</span>');

				_this.$el.keyup(function() {
					var text_length = _this.getValue() ? _this.getValue().length : 0;
					var text_remaining = _this.schema.maxLength - text_length;
					var c = text_remaining == 1 ? "character" : "characters"
					var $msgElem = _this.$el.parent().parent().find('.chars-remaining');
					$msgElem.html(text_remaining + ' ' + c + ' remaining');

					if (text_remaining < 0) {
						$msgElem.addClass('text-danger');
					} else {
						$msgElem.removeClass('text-danger');
					}

				});
			}
		},
		onFormClose : function() {
			if (this.onBeforeClose) {
				this.onBeforeClose();
			}
			this.remove();
			this.unbind();
		},
	});
	Calipso.components.backboneform.Textarea = Calipso.components.backboneform.Text.extend({
		tagName : "textarea"
	});

	Calipso.components.backboneform.Password = Calipso.components.backboneform.Text.extend({
	},
	// static members
	{
		template : _.template('\
			<div class="form-js3">\
				<label for="<%= editorId %>">\
					<% if (titleHTML){ %><%= titleHTML %>\
					<% } else { %><%- title %><% } %>\
				</label>\
				<div class="input-group" data-editor>\
					<span class="input-group-btn">\
						<button class="btn btn-default" type="button">show</button>\
					</span>\
					<div data-error></div>\
					<div><%= help %></div>\
				</div>\
			</div>\
		', null, Backbone.Form.templateSettings),
	});
	Calipso.components.backboneform.NonEmptyOrHidden = Calipso.components.backboneform.Text.extend({
		render : function(){
			console.log("RENDER value: " + this.getValue());
			if (this.getValue()) {
				return this.setElement(editor.render().el.attr("type", "hidden"));
    	}
			else{
				return Calipso.components.backboneform.Text.render.apply(this, arguments);
			}
		}
	});

	Calipso.components.backboneform.NumberText = Calipso.components.backboneform.Text.extend({
		getValue : function() {
			var value = Backbone.Form.editors.Text.prototype.getValue.apply(this, arguments);
			if (!(_.isUndefined(value) || _.isNull(value) || value == "")) {
				return value * 1;
			} else {
				return null;
			}
		},
	});

	Calipso.components.backboneform.Radio = Backbone.Form.editors.Radio.extend({
		tagName : 'div',
		className : "list-group",
	}, {
		//STATICS
		template : _.template('\
    <% _.each(items, function(item) { %>\
     <label class="list-group-item" for="<%= item.id %>">\
				<input type="radio" name="<%= item.name %>" value="<%- item.value %>" id="<%= item.id %>"\
				 <% if (!_.isUndefined(this.value) && this.value == item.value){ %> checked="checked" <% } %>  />\
				&nbsp;<% if (item.labelHTML){ %><%= item.labelHTML %><% }else{ %><%- item.label %><% } %>\
			</label>\
    <% }); %>\
  ', null, Backbone.Form.templateSettings),
	});

	Calipso.components.backboneform.RadioInline = Calipso.components.backboneform.Radio.extend({
		className : "list-group list-group-horizontal",
	});

	Calipso.components.backboneform.Checkboxes = Backbone.Form.editors.Checkboxes.extend({
		tagName : 'div',
		className : "list-group",
		/**
		* Create the checkbox list HTML
		* @param {Array}   Options as a simple array e.g. ['option1', 'option2']
		*                      or as an array of objects e.g. [{val: 543, label: 'Title for object 543'}]
		* @return {String} HTML
		*/
		_arrayToHtml : function(array) {
			var html = $();
			var self = this;

			_.each(array, function(option, index) {
				var itemHtml = $('<label class="list-group-item" for="' + self.id + '-' + index + '">');
				if (_.isObject(option)) {
					if (option.group) {
						itemHtml = null;
						html = html.add(self._arrayToHtmloption.options());
					} else {
						var val = (option.val || option.val === 0) ? option.val : '';
						itemHtml.append($('<input type="checkbox" name="' + self.getName() + '" id="' + self.id + '-' + index + '" />').val(val));
						if (option.labelHTML) {
							itemHtml.append("&nbsp;" + option.labelHTML);
						} else {
							itemHtml.append("&nbsp;" + option.label);
						}
					}
				} else {
					itemHtml.append($('<input type="checkbox" name="' + self.getName() + '" id="' + self.id + '-' + index + '" />').val(option));
				}

				if (itemHtml) {
					html = html.add(itemHtml);
				}
			});

			return html;
		}
	}, {
	//STATICS
	});

	Calipso.components.backboneform.CheckboxesInline = Calipso.components.backboneform.Checkboxes.extend({
		className : "list-group list-group-horizontal",
	});

	Calipso.components.backboneform.Tel = Calipso.components.backboneform.Text.extend({
		errorCodes : {
			"-99" : "DEFAULT",
			"1" : "INVALID_COUNTRY_CODE",
			"2" : "TOO_SHORT",
			"3" : "TOO_LONG",
			"4" : "NOT_A_NUMBER"
		},
		config : {
			nationalMode : true,
		},
		/*intlValidate : function() {
			this.form.fields[this.getName()].validate();
		},*/
		initialize : function(options) {
			var _this = this;
			this.labels = Calipso.util.getLabels();
			if (!options.schema.validators) {
				options.schema.validators = [];
			}
			if (options.value) {
				this.value = options.value;
			}
			options.schema.validators.push(function(value, formValues) {
				if (value != null && value != "" && !_this.$el.intlTelInput("isValidNumber")) {
					var msgKey = _this.errorCodes[_this.$el.intlTelInput("getValidationError") + ""] || "DEFAULT";
					var err = {
						type : _this.getName(),
						message : _this.labels.intlTelInput[msgKey]
					};
					return err;
				}
			});
			Calipso.components.backboneform.Text.prototype.initialize.call(this, options);

			this.config.customPlaceholder = function(selectedCountryPlaceholder, selectedCountryData) {
				return _this.labels.intlTelInput.eg + ' ' + selectedCountryPlaceholder;
			};
		},
		setValue : function(value) {
			if (!value) {
				this.value = null;
			} else {
				this.value = value;
			}
		},
		getValue : function() {
			return this.$el.intlTelInput("getNumber");
		},
		onFormAttach : function() {
			var _this = this;
			this.$el.intlTelInput(this.config);
			if (this.value) {
				this.$el.intlTelInput("setNumber", this.value);
			}
			this.$el.change(function() {
				_this.setValue(_this.$el.intlTelInput("getNumber"));
				//_this.intlValidate();
			});
		},
		onBeforeClose : function() {
			this.$el.off("change");
			this.$el.intlTelInput("destroy");
		},
	});
	/*
	 *  based on typeahead/bloodhound 0.11.1, see
	 * https://github.com/twitter/typeahead.js
	 */
	Calipso.components.backboneform.Typeahead = Calipso.components.backboneform.Text.extend({
		tagName : 'div',
		//className: "form-control",
		typeaheadSource : null,
		minLength : 2,
		placeholder : "",
		initialize : function(options) {
			Calipso.components.backboneform.Text.prototype.initialize.call(this, options);
			// set the options source
			if (this.schema && this.schema.typeaheadSource) {
				this.typeaheadSource = this.schema.typeaheadSource;
			} else {
				throw "Missing required option: 'typeaheadSource'";
			}
			if (this.schema.minLength) {
				this.minLength = this.schema.minLength;
			}
			if (this.schema.placeholder) {
				this.placeholder = " placeholder=\"this.schema.placeholder\" ";
			}
			this.$el.removeAttr("id class name type autocomplete");
			this.$el.html('<input type="text" id="' + this.id + '" name="' + this.getName() + '"  class="form-control"  autocomplete="off" ' + this.placeholder + '/>');
		},
		/**
		 * Adds the editor to the DOM
		 */
		onFormAttach : function() {
			var _this = this;
			var $el = _this.$el.find("#" + _this.id);

			//				var editorAttrs = _this.schema.editorAttrs;
			//				if(editorAttrs){
			//					$.each(editorAttrs, function() {
			//						$el.attr(this.name, this.value);
			//					});
			//				}
			$el.typeahead({
				minLength : _this.minLength,
				highlight : true,
				hint : true
			}, _this.typeaheadSource);
		},
		onBeforeClose : function() {
			var _this = this;
			var $el = _this.$el.find("#" + _this.id);
			this.$el.typeahead("destroy");
		},
	});
	Calipso.components.backboneform.TypeaheadObject = Calipso.components.backboneform.Typeahead.extend({
		initialize : function(options) {
			Calipso.components.backboneform.Typeahead.prototype.initialize.call(this, options);
			this.$el.removeAttr("id class name type autocomplete");
			this.$el.html('<input type="hidden" id="' + this.id + '" name="' + this.getName() + '" />' + '<input type="text" class="form-control" id="' + this.id + 'Typeahead" name="' + this.getName() + 'Typeahead" autocomplete="off" ' + this.placeholder + '/>');
		},
		/**
		 * Adds the editor to the DOM
		 */
		onFormAttach : function() {
			var _this = this;
			var $hidden = _this.$el.find("#" + _this.id);
			var $el = _this.$el.find("#" + _this.id + "Typeahead");

			$el.typeahead({
				minLength : _this.minLength,
				highlight : true,
				hint : true
			}, _this.typeaheadSource).on('typeahead:selected', function(e, suggestion, name) {
				_this.setValue(suggestion, name);
			});
			$el.bind('typeahead:change', function(e, query) {
				_this.setValue(null);
			});
			if (_this.value) {
				var val = Calipso.getObjectProperty(_this.value, "id", _this.value);
				var nameVal = Calipso.getObjectProperty(_this.value, "name");
				$hidden.val(val);
				$el.typeahead('val', nameVal);
			}
		},
		onBeforeClose : function() {
			var _this = this;
			var $el = _this.$el.find("#" + _this.id + "Typeahead");

			$el.typeahead("destroy");
		},
		setValue : function(value, name) {
			//console.log("Calipso.components.backboneform.TypeaheadObject#setValue, value: '" + value + "', name: " + name);
			var _this = this;
			if (!value) {
				value = null;
			}
			this.value = value;
			if (name) {
				_this.$el.find("#" + this.id).attr("name", name);
				_this.$el.find("#" + this.id).val(value && value.id ? value.id : value);
			}
		},
		getValue : function() {
			//console.log("Calipso.components.backboneform.TypeaheadObject#getValue, value: '" + this.value + "'");
			var value = this.value;
			var query = this.$el.find("#" + this.id + "Typeahead").typeahead('val');
			// if empty value or input, return plain
			return value && query ? value : null;
		},
	});

	// uses  https://github.com/Eonasdan/bootstrap-datetimepicker
	Calipso.components.backboneform.Datetimepicker = Calipso.components.backboneform.Text.extend({
		getConfig : function(){
			return {
				locale : Calipso.util.getLocale(),
				allowInputToggle : true,
			};
		},
		initialize : function(options) {
			Calipso.components.backboneform.Text.prototype.initialize.call(this, options);
			this.schema.config = _.defaults({}, this.schema.config, this.getConfig());
			// set position if empty
			if (!this.schema.config.widgetPositioning) {
				this.schema.config.widgetPositioning = {
					horizontal : 'auto',
					vertical : 'bottom'
				}
				var pos = $.inArray(this.getName(), this.form.fields);
				if (pos && pos > (this.form.fields.length / 2)) {
					this.schema.config.widgetPositioning.vertical = "top";
				}
			}
		},
		callDataFunction : function(functionName, param) {
			//console.log("callDataFunction:  " + functionName + ", param: " + param);
			this.$el.data("DateTimePicker")[functionName](param);
		},
		onFormAttach : function() {
			var _this = this;
			_this.$el.attr('autocomplete', 'off');
			_this.$el.parent().addClass("input-group");
			_this.$el.parent().append("<span class=\"input-group-addon\"><span class=\"glyphicon glyphicon-calendar\"></span></span>");
			_this.$el.parent().datetimepicker(this.schema.config);
			//console.log("Calipso.components.backboneform.Datetimepicker#render, _this.value: " + _this.value);
			var value = _this.schema.fromProperty ? _this.model.get(_this.schema.fromProperty) : _this.value;
			if (value) {
				var initValue = new Date(value);
				_this.$el.parent().data("DateTimePicker").date(initValue);
			}
		},
		onBeforeClose : function() {
			this.$el.parent().data("DateTimePicker").destroy();
		},
		getValue : function() {
			return this.$el.parent().data("DateTimePicker").date();
		},
	});


		/**
		 * Select2
		 *
		 * A simple Select2 - jQuery based replacement for the Select editor.
		 *
		 * Usage: Works the same as Select editor, with the following extensions for Select2:
		 * schema.config: configuration object passed to Select2
		 * schema.multiple: sets 'multiple' property on the HTML <select>
		 *
		 * Example:
		 * schema: {title: {type:'Select2', options:['Mr','Mrs',Ms], config: {}, multiple: false}
		 */
		Backbone.Form.editors.SimpleTypeSelect2 = Backbone.Form.editors.Select.extend({
			config : {width: "100%"},
			initialize : function(options) {
				// add model collection options if needed
				if(options.schema && !options.schema.options && options.schema.listModel){
					options.schema.options = new Calipso.collection.AllCollection([], {
						url : function() {
							return Calipso.getBaseUrl() + "/api/rest/" + options.schema.listModel.getPathFragment();
						},
						model : options.schema.listModel,
					});
				}
				console.log("SimpleTypeSelect2#initialize, options: ");
				console.log(options);
				Backbone.Form.editors.Select.prototype.initialize.call(this, options);
				options = options || {};
				if(options.schema && options.schema.config){
					if(options.schema.config){
						this.config = $.extend({}, this.config, options.schema.config);
					}
				}
				if (this.form) {
					var _this = this;
					this.listenToOnce(this.form, "attach", function() {
						_this.onFormAttach();
					});
					this.listenToOnce(this.form, "close", function() {
						_this.onFormClose();
					});
				}
			},
			setValue : function(value) {
	//			console.log("setValue, value: ");
	//			console.log(value);

				console.log("setValue, value: " + (value?value.id:value));
				this.$el.find('option[selected]').removeAttr('selected');

				// add new selection
				var newValue, id;
				// multiple selection
				if (this.schema.multiple) {
					newValue = [];
					for(var i = 0; i < value.length; i++) {
						id = value[i] && value[i].id ? value[i].id : value[i];
						newValue.push(id);
						this.$el.find('option[value="' + id + '"]').prop('selected', true);
					}
				}
				// single selection
				else{
					id = value && value.id ? value.id : value;
					newValue = id;
					this.$el.find('option[value="' + id + '"]').prop('selected', true);
				}
				this.$el.val(newValue);
				this.$el.trigger("change");
			},
			onFormAttach : function() {
				Backbone.Form.editors.Select.prototype.render.apply(this, arguments);
				var _this = this;
				if (this.schema.multiple) {
					this.$el.prop('multiple', true);
				}

				this.$el.select2(this.config).on("change", function() {
					_this.trigger('change', _this);
				});

			},
			onFormClose : function() {
				this.$el.select2('destroy');
				if (this.onBeforeClose) {
					this.onBeforeClose();
				}
				this.remove();
				this.unbind();
			},
		});

		/**
		 * ModelSelect2
		 *
		 * A simple Select2 - jQuery based replacement for the Select editor
		 * that selects a model VS a string value.
		 *
		 * Usage: Works the same as Select editor, with the following extensions for Select2:
		 * schema.config: configuration object passed to Select2
		 * schema.multiple: sets 'multiple' property on the HTML <select>
		 *
		 * Example:
		 * schema: {title: {type:'Select2', options:myCollection, config: {}, multiple: false}
		 */
		Backbone.Form.editors.ModelSelect2 = Backbone.Form.editors.SimpleTypeSelect2.extend({
			//  select a model VS a string value.

			getValue : function() {
				var simpleValue = this.$el.val();
	//			console.log("Backbone.Form.editors.ModelSelect2#getValue, value: ");
	//			console.log(simpleValue);
				var value;
				if(simpleValue){

					if (this.schema.multiple) {
						value = [];
						for (var i = 0; i < simpleValue.length; i++) {
							value.push(this.schema.options.findWhere({
								id : simpleValue[i]
							}));
						}
					} else {
						value = this.schema.options.findWhere({
							id : simpleValue
						});
					}
				}
	//			console.log("getValue, simpleValue: " + simpleValue + ", model value: ");
	//			console.log(value);
				return value;
			},
		});

		/*
		 * Use the Select2 v4 Theme for Bootstrap, see
		 * https://github.com/fk/select2-bootstrap-theme
		 */
		$.fn.select2.defaults.set("theme", "bootstrap");

	return Calipso;

});
