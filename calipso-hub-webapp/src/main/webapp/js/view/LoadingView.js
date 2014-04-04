define(['underscore', 'marionette', 'hbs!template/loading'], function (_, Marionette, tmpl) {

  return Marionette.ItemView.extend({

    tagName: 'tr',

    className: 'loading',

    template: tmpl,

    initialize: function (opt) {
      this.model.set(opt.loadingViewData, { silent: true });
    }

  },
	{
		typeName: "LoadingView"
	});

});
