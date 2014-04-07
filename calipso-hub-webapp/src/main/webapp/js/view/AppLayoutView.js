define([ 'view/abstract-layout-view', 'hbs!template/applayout' ], function(AbstractLayoutView, tmpl) {
	return AbstractLayoutView.extend({
		tagName: "div",
		className : 'row',
		template : tmpl,// _.template(templates.applayout),
		regions : {
			navRegion : "#calipsoAppLayoutNavRegion",
			contentRegion : "#calipsoAppLayoutContentRegion"
		},
	},
	{
		getTypeName: function(){return "AppLayoutView"}
	});
	
});
