define([ 'underscore', 'marionette', 'hbs!template/header' ], function(_, Marionette, tmpl) {

	return Marionette.ItemView.extend({
		template : tmpl,

		initialize : function(options) {
			_.bindAll(this);
		},
//		events : {
//			"click li" : "linkClicked"
//		},

//		linkClicked : function(e) {
//			console.log("linkClicked");
//			e.preventDefault();
//			this.$el.find('.navbar-nav li.active').removeClass('active');
//			$(e.currentTarget).addClass('active');
//		},

	},
	{
		typeName: "HeaderView"
	});

});
