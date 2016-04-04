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

define([ "lib/calipsolib/form", 'underscore', 'handlebars', 'moment', 'backbone', 'backbone-forms', 'backbone-forms-bootstrap3', 'backbone-forms-select2', 'marionette',

'bloodhound', 'typeahead', 'bootstrap-datetimepicker', 'bootstrap-switch', 'intlTelInput' ],
function(Calipso, _, Handlebars, moment, Backbone, BackboneForms, BackboneFormsBootstrap, BackboneFormsSelect2, BackboneMarionette, Bloodhoud, Typeahead, BackboneDatetimepicker, BootstrapSwitch, intlTelInput) {

	Calipso.uifield = {};

	// Base attribute dataType
	Calipso.datatypes.Base = Marionette.Object.extend({}, {
	});
/*
	Calipso.datatypes.Base.extend = function(protoProps, staticProps) {
		// Call default extend method
		var extended = Backbone.Marionette.extend.call(this, protoProps, staticProps);
		// Add a usable super method for better inheritance
		extended.prototype._super = this.prototype;
		// Apply new or different defaults on top of the original
		if (protoProps.defaults && this.prototype.defaults) {
			extended.prototype.defaults = _.deepExtend({}, this.prototype.defaults, extended.prototype.defaults);
		}
		return extended;
	};
*/
	Calipso.datatypes.String = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "string",
		"backgrid" : {
			name : null,
			label : null,
			editable : false,
			sortable : false,
			cell : "string", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
			headerCell : Backgrid.HeaderCell, //"select-all"
		},
		"form" : {
			type : "Text",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Text = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "text",
		"form" : {
			type : "TextArea",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Boolean = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "boolean",
		"form" : {
			type : "Checkbox",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Integer = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "integer",
		"form" : {
			type : "Number",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Decimal = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "decimal",
		"form" : {
			type : "Number",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Money = Calipso.datatypes.Decimal.extend({}, {
		dataTypeKey : "money",
		"form" : {
			type : "Number",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Datetime = Calipso.datatypes.Integer.extend({}, {
		dataTypeKey : "datetime",
		"form" : {
			type : Calipso.components.backboneform.Datetimepicker,
			validators : [ /*'required'*/],
			config : {
				locale : Calipso.util.getLocale(),
				format : 'YYYY-MM-DD HH:mm',
				viewMode : 'months',
				widgetPositioning : {
					//horizontal : "right"
				}
			},
		}
	});
	Calipso.datatypes.Date = Calipso.datatypes.Datetime.extend({}, {
		dataTypeKey : "date",
		"form" : {
			type : Calipso.components.backboneform.Datetimepicker,
			validators : [ /*'required'*/],
			config : {
				locale : Calipso.util.getLocale(),
				format : 'YYYY-MM-DD',
				viewMode : 'months',
				widgetPositioning : {
					//horizontal : "right"
				}
			},
		}
	});
	Calipso.datatypes.Time = Calipso.datatypes.Datetime.extend({}, {
		dataTypeKey : "time",
		"form" : {
			type : Calipso.components.backboneform.Datetimepicker,
			validators : [ /*'required'*/],
			config : {
				locale : Calipso.util.getLocale(),
				format : 'HH:mm',
				//viewMode : 'months',
				widgetPositioning : {
					//horizontal : "right"
				}
			}
		}
	});
	Calipso.datatypes.Lov = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "lov",
	});
	Calipso.datatypes.List = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "list",
		"form" : {
			type : Backbone.Form.editors.ModelSelect2,
		}
	});
	Calipso.datatypes.Email = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "email",
		"form" : {
			type : "Text",
			validators : [ 'email' ],
		},
		"backgrid" : {
			cell : "email"
		}
	});


	Calipso.datatypes.Tel = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "tel",
		"form" : {
			type : Calipso.components.backboneform.Tel,
			validators : [ Calipso.components.backboneform.validators.digitsOnly ]
		}
	});
	Calipso.datatypes.Link = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "link",
	});
	Calipso.datatypes.File = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "file",
	});
	Calipso.datatypes.Image = Calipso.datatypes.File.extend({}, {
		dataTypeKey : "image",
	});
	Calipso.datatypes.Color = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "color",
	});
	Calipso.datatypes.Json = Calipso.datatypes.Text.extend({}, {
		dataTypeKey : "json",
	});
	Calipso.datatypes.Markdown = Calipso.datatypes.Text.extend({}, {
		dataTypeKey : "markdown",
	});
	Calipso.datatypes.Html = Calipso.datatypes.Text.extend({}, {
		dataTypeKey : "html",
	});
	Calipso.datatypes.Csv = Calipso.datatypes.Text.extend({}, {
		dataTypeKey : "csv",
	});

	Calipso.datatypes.Edit = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "edit",
		"backgrid" : {
			cell : Calipso.components.backgrid.EditRowInModalCell,
			headerCell : Calipso.components.backgrid.CreateNewInModalHeaderCell
		}
	});
	return Calipso;

});
