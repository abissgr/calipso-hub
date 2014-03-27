define([ 'underscore', 'marionette', 'hbs!template/applayout' ], function(_, Marionette, tmpl) {
	return Marionette.Layout.extend({
		tagName: "div",
		className : 'app-layout container-fluid',
		template : tmpl,// _.template(templates.applayout),
		regions : {
			navRegion : "#calipsoAppLayoutNavRegion",
			contentRegion : "#calipsoAppLayoutContentRegion"
		},
//		initialize : function(options) {
//			$('body').removeClass('stripes-bg');
//		}
	});
});
