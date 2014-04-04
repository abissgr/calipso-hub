define(['underscore', 'marionette', 'hbs!template/notfound'], function(_, Marionette, tmpl) {

  return Marionette.ItemView.extend({
    className: 'container span8 home',
    
    template: tmpl,

    initialize: function (options) {
      _.bindAll(this);
    }

  },
	{
		typeName: "NotFoundView"
	});

});
