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
	Calipso.backboneform = {};
	Calipso.backboneform.validators = {
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

	Calipso.backboneform.Field = Backbone.Form.Field.extend({
		render: function() {
			//Only render the editor if Hidden
			if (this.schema.type.isHidden || this.schema.isHidden) {
				return this.setElement(this.editor.render().el);
			} else {
				return Backbone.Form.Field.prototype.render.apply(this, arguments);
			}
		},
	});

	Calipso.backboneform.Fieldset = Backbone.Form.Fieldset.extend({

		initialize: function(options) {
			options || (options = {});
			Backbone.Form.Fieldset.prototype.initialize.apply(this, arguments);
			this.form = options.form;
		},
	  render: function() {
	    var schema = this.schema,
	        fields = this.fields,
					form = this.form,
	        $ = Backbone.$,
					_this = this;
	    //Render fieldset
	    var $fieldset = $($.trim(this.template(_.result(this, 'templateData'))));
	    //Render fields
	    $fieldset.find('[data-fields]').add($fieldset).each(function(i, el) {
	      var $container = $(el),
	          selection = $container.attr('data-fields');
	      if (_.isUndefined(selection)) return;

				_.each(fields, function(field) {
					if(form.fieldsInitiallyShown
						&& $.inArray(field.key, form.fieldsInitiallyShown) < 0){
						form.lazyFieldContainers[field.key] = $container;
					}
					else{
						$container.append(field.render().el);
					}
	      });
	    });
	    this.setElement($fieldset);
	    return this;
	  },
	});
	Calipso.backboneform.Form = Backbone.Form.extend({
		hintRequiredFields : true,
		capitalizeKeys : true,
		lazyFieldContainers : {},
		Field : Calipso.backboneform.Field,
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
			options.Fieldset || (options.Fieldset = Calipso.backboneform.Fieldset);
			var hintRequiredFields = options.hintRequiredFields;
			if (!_.isUndefined(hintRequiredFields)) {
				this.hintRequiredFields = hintRequiredFields;
			}
			this.formClassName = options.formClassName;
			this.fieldTemplate = options.fieldTemplate;
			this.fieldsetTemplate = options.fieldsetTemplate;
			this.fieldsInitiallyShown = options.fieldsInitiallyShown;
			// add search mode if missing
			if(!options.schema["_searchmode"]){
				options.schema["_searchmode"] = {
					type : Calipso.backboneform.SearchMode,
					isHidden : true,
				};
			}
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
		createFieldset: function(schema) {
	    var options = {
      	form: this,
	      schema: schema,
	      fields: this.fields,
	      legend: schema.legend || null
	    };
			this.fieldsetTemplate && (options.template = this.fieldsetTemplate);
	    return new this.Fieldset(options);
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
		renderLazyField : function(fieldKey){
			var $container = this.lazyFieldContainers[fieldKey];
			if($container){
				$container.append(this.fields[fieldKey].render().el);
				delete this.lazyFieldContainers[fieldKey];
			}
		},
		addOrPrepareFieldsforContainer : function(fieldKeys, $container, fields){
			var _this = this;
			//Add them
			_.each(fieldKeys, function(key) {
				var field = fields[key];
				if(_this.fieldsInitiallyShown && !$.inArray(key, _this.fieldsInitiallyShown)){
					_this.lazyFieldContainers[key] = $container;
				}
				else{
					$container.append(field.editor.render().el);
				}
			});
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
				this.$el.addClass(this.formClassName).html($form.html());
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
				self.addOrPrepareFieldsforContainer(keys, $container, fields);

			});
			//Render standalone fields
			$form.find('[data-fields]').add($form).each(function(i, el) {
				var $container = $(el), selection = $container.attr('data-fields');

				if (_.isUndefined(selection))
					return;

				//Work out which fields to include
				var keys = (selection == '*') ? self.selectedFields || _.keys(fields) : selection.split(',');
				self.addOrPrepareFieldsforContainer(keys, $container, fields);

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
	  templateData: function() {
	    var options = this.options;

	    return {
	      submitButton: options.submitButton,
	      fieldsInitiallyShown : options.fieldsInitiallyShown
	    }
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
		toQueryString : function() {
			var hash = this.toJson();
			var q = $.param( hash );
			return q;
		},
	});
	Calipso.backboneform.SearchMode = Backbone.Form.editors.Text.extend({
		defaultValue : "AND",
	},
	{
		isHidden : true,
	});

	Calipso.backboneform.Text = Backbone.Form.editors.Text.extend({
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
	Calipso.backboneform.Textarea = Calipso.backboneform.Text.extend({
		tagName : "textarea"
	});

	Calipso.backboneform.Password = Calipso.backboneform.Text.extend({
		onFormAttach : function() {
			Calipso.backboneform.Text.prototype.onFormAttach.call(this, arguments);
			var _this = this;
			// set the options source
			this.$el.addClass("form-control").attr("type", "password");
			this.$el.parent().addClass("input-group").append('\
				<span class="input-group-btn">\
	        <button class="btn btn-secondary pass-toggle" type="button" title="' + Calipso.util.getLabels("calipso.words.show") + '"><i class="fa fa-eye"> </i></button>\
	      </span>');
			this.$el.parent().find('button.pass-toggle:first').on('click', function (e) {
				_this.passToggle(e);
		  })
		},
		passToggle : function(e) {
			if(this.$el.attr("type").toLowerCase() == "password"){
				this.$el.attr("type", "text");
				$(e.currentTarget).attr("title", Calipso.util.getLabels("calipso.words.hide")).html('<i class="fa fa-eye-slash"> </i>');
			}
			else{
				this.$el.attr("type", "password");
				$(e.currentTarget).attr("title", Calipso.util.getLabels("calipso.words.show")).html('<i class="fa fa-eye"> </i>');
			}
		},
	},
	// static members
	{
	});
	Calipso.backboneform.NonEmptyOrHidden = Calipso.backboneform.Text.extend({
		render : function(){
			if (this.getValue()) {
				return this.setElement(editor.render().el.attr("type", "hidden"));
    	}
			else{
				return Calipso.backboneform.Text.render.apply(this, arguments);
			}
		}
	});

	Calipso.backboneform.NumberText = Calipso.backboneform.Text.extend({
		getValue : function() {
			var value = Backbone.Form.editors.Text.prototype.getValue.apply(this, arguments);
			if (!(_.isUndefined(value) || _.isNull(value) || value == "")) {
				return value * 1;
			} else {
				return null;
			}
		},
	});

	Calipso.backboneform.Radio = Backbone.Form.editors.Radio.extend({
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

	Calipso.backboneform.RadioInline = Calipso.backboneform.Radio.extend({
		className : "list-group list-group-horizontal",
	});

	Calipso.backboneform.Checkboxes = Backbone.Form.editors.Checkboxes.extend({
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

	Calipso.backboneform.CheckboxesInline = Calipso.backboneform.Checkboxes.extend({
		className : "list-group list-group-horizontal",
	});

	Calipso.backboneform.Tel = Calipso.backboneform.Text.extend({
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
			Calipso.backboneform.Text.prototype.initialize.call(this, options);

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
	Calipso.backboneform.Typeahead = Calipso.backboneform.Text.extend({
		tagName : 'div',
		//className: "form-control",
		typeaheadSource : null,
		minLength : 2,
		placeholder : "",
		initialize : function(options) {
			Calipso.backboneform.Text.prototype.initialize.call(this, options);
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
	Calipso.backboneform.TypeaheadObject = Calipso.backboneform.Typeahead.extend({
		initialize : function(options) {
			Calipso.backboneform.Typeahead.prototype.initialize.call(this, options);
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
			var _this = this;
			if (!value) {
				value = null;
			}
			this.value = value;
			if (name) {
				console.log("Changing name to " + name)
				_this.$el.find("#" + this.id).attr("name", name);
				_this.$el.find("#" + this.id).val(value && value.id ? value.id : value);
			}
		},
		getValue : function() {
			var value = this.value;
			var query = this.$el.find("#" + this.id + "Typeahead").typeahead('val');
			// if empty value or input, return plain
			return value && query ? value : null;
		},
	});

	Calipso.backboneform.SearchBox = Calipso.backboneform.Typeahead.extend({
		//excludeFromCommit : true,
		initialize : function(options) {
			Calipso.backboneform.Typeahead.prototype.initialize.call(this, options);
		},
		/**
		 * Adds the editor to the DOM
		 */
		onFormAttach : function() {
			var _this = this;
			var $el = _this.$el.find("#" + _this.id);

			$el.typeahead({
				minLength : _this.minLength,
				highlight : true,
				hint : true
			}, _this.typeaheadSource).on('typeahead:selected', function(e, suggestion, name) {
				_this.setValue(suggestion[name]);
			});
			$el.bind('typeahead:change', function(e, query) {
				_this.setValue(query);
			});
			if (_this.value) {
				this.setValue(_this.value);
			}
			_this.form.fields["_searchmode"].setValue("OR");
		},
		setValue : function(value) {
			console.log("setValue: " + value);
			var _this = this;
			this.value = value;
			_this.$el.find("#" + _this.id).typeahead('val', value);
			_.each(this.schema.fields, function(key){
				_this.form.fields[key].setValue(value);
			});

		},
		getValue : function() {
			return this.value;
		},
	});


	// uses  https://github.com/Eonasdan/bootstrap-datetimepicker
	Calipso.backboneform.Datetimepicker = Calipso.backboneform.Text.extend({
		getConfig : function(){
			return {
				locale : Calipso.util.getLocale(),
				allowInputToggle : true,
			};
		},
		initialize : function(options) {
			Calipso.backboneform.Text.prototype.initialize.call(this, options);
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
			this.$el.data("DateTimePicker")[functionName](param);
		},
		onFormAttach : function() {
			var _this = this;
			_this.$el.attr('autocomplete', 'off');
			_this.$el.parent().addClass("input-group");
			_this.$el.parent().append("<span class=\"input-group-addon\"><span class=\"glyphicon glyphicon-calendar\"></span></span>");
			_this.$el.parent().datetimepicker(this.schema.config);
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

	// uses  https://github.com/Eonasdan/bootstrap-datetimepicker
	Calipso.backboneform.Datetimepicker = Calipso.backboneform.Text.extend({
		initialize : function(options) {
			Calipso.backboneform.Text.prototype.initialize.call(this, options);
		},
		callDataFunction : function(functionName, param) {
			this.$el.data("DateTimePicker")[functionName](param);
		},
		onFormAttach : function() {
			var _this = this;
			_this.$el.attr('autocomplete', 'off');
			_this.$el.parent().addClass("input-group");
			_this.$el.parent().append("<span class=\"input-group-addon\"><span class=\"glyphicon glyphicon-calendar\"></span></span>");
			_this.$el.parent().datetimepicker(this.schema.config);
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
