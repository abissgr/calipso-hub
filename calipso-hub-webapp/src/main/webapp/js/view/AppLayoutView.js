define([ 'underscore', 'marionette', 'hbs!template/applayout' ], function(_, Marionette, tmpl) {
	return Marionette.Layout.extend({
		className : 'app-layout',
		template : tmpl,// _.template(templates.applayout),
		regions : {
			mainContentNavRegion : "#calipsoMainContentNavRegion",
			content : "#content"
		},
		initialize : function(options) {
			$('body').removeClass('stripes-bg');
		}
	});
});
