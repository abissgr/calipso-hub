define(function(require) {
		var _ = require('underscore'),
			Marionette = require('marionette'),
			ModelDrivenBrowseLayout = require('view/md-browse-layout'),
			GenericFormView = require('view/GenericFormView'),
			tmpl = require('hbs!template/md-search-layout');
	var ModelDrivenSearchLayout = ModelDrivenBrowseLayout.extend({
		tagName : 'div',
		id: "calipsoModelDrivenSearchLayout",
		template : tmpl,
		regions : {
			sideBarRegion : "#calipsoModelDrivenSearchLayout-sideBarRegion",
			contentRegion : "#calipsoModelDrivenSearchLayout-contentRegion"
		},
		onShow: function(){
			this.showRSidebar(this.model);
			this.showContent(this.model);
		},
		showRSidebar: function(routeModel){
			console.log("ModelDrivenSearchLayout.showRSidebar: "+routeModel.get("id"));
			// show the search form
			this.sideBarRegion.show(new GenericFormView({formSchemaKey: "search", model: routeModel}));
		}
	},
	// static members
	{
 		getTypeName: function(){return "ModelDrivenSearchLayout"}
	});
	return ModelDrivenSearchLayout;
});