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
		dataTypeKey : "String",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Text", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
		},
		"form" : {
			type : "Text",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Text = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "text",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Text", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
		},
		"form" : {
			type : "Text",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Boolean = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "Boolean",
		"form" : {
			type : "Checkbox",
			validators : [ /*'required'*/],
		},
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Boolean", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
		},
	});
	Calipso.datatypes.Integer = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "Integer",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Integer", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
		},
		"form" : {
			type : "Integer",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Decimal = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "Decimal",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Number", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
		},
		"form" : {
			type : "Number",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Money = Calipso.datatypes.Decimal.extend({}, {
		dataTypeKey : "Money",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Number", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
		},
		"form" : {
			type : "Number",
			validators : [ /*'required'*/],
		}
	});
	Calipso.datatypes.Datetime = Calipso.datatypes.Integer.extend({}, {
		dataTypeKey : "Datetime",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Datetime", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
		},
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
		dataTypeKey : "Date",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Date", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
		},
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
		dataTypeKey : "Time",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Time", //integer,number, date, uri, select-row, Calipso.components.backgrid.ChildStringAttributeCell
		},
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
		dataTypeKey : "Lov",
	});
	Calipso.datatypes.List = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "List",
		"form" : {
			type : Backbone.Form.editors.ModelSelect2,
		}
	});
	Calipso.datatypes.Email = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "Email",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Email"
		},
		"form" : {
			type : "Text",
			validators : [ 'email' ],
		},
	});


	Calipso.datatypes.Tel = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "Tel",
		"backgrid" : {
			editable : false,
			sortable : false,
			cell : "Text"
		},
		"form" : {
			type : Calipso.components.backboneform.Tel,
			validators : [ Calipso.components.backboneform.validators.digitsOnly ]
		}
	});
	Calipso.datatypes.Link = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "Link",
	});
	Calipso.datatypes.File = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "file",
	});
	Calipso.datatypes.Image = Calipso.datatypes.File.extend({}, {
		dataTypeKey : "Image",
	});
	Calipso.datatypes.Color = Calipso.datatypes.String.extend({}, {
		dataTypeKey : "Color",
	});
	Calipso.datatypes.Json = Calipso.datatypes.Text.extend({}, {
		dataTypeKey : "Json",
	});
	Calipso.datatypes.Markdown = Calipso.datatypes.Text.extend({}, {
		dataTypeKey : "Markdown",
	});
	Calipso.datatypes.Html = Calipso.datatypes.Text.extend({}, {
		dataTypeKey : "Html",
	});
	Calipso.datatypes.Csv = Calipso.datatypes.Text.extend({}, {
		dataTypeKey : "Csv",
	});

	Calipso.datatypes.Edit = Calipso.datatypes.Base.extend({}, {
		dataTypeKey : "Edit",
		"backgrid" : {
			cell : Calipso.components.backgrid.EditRowInModalCell,
			headerCell : Calipso.components.backgrid.CreateNewInModalHeaderCell
		}
	});
	return Calipso;

});
