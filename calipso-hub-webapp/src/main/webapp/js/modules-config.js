define([
  'jquery',
  'underscore',
  'backbone',
  'model/generic-model',
  'model/host',
  'model/resource',
  'model/page',
  'model/text',
  'model/user',
  'view/md-browse-layout',
  'view/md-search-layout',
  'view/GenericFormView',
  'view/home-view'
],

function ($, _, Backbone, GenericModel, HostModel, ResourceModel, PageModel, TextModel, UserModel,  
		ModelDrivenBrowseLayout, ModelDrivenSearchLayout, GenericFormView, HomeView){

  var modelsConfig = {
		  models : {
			  GenericModel: GenericModel,
			  HostModel: HostModel,
			  ResourceModel: ResourceModel,
			  PageModel: PageModel,
			  TextModel: TextModel,
			  UserModel: UserModel,
		  },

		  layouts : {
			  ModelDrivenBrowseLayout: ModelDrivenBrowseLayout,
			  ModelDrivenSearchLayout: ModelDrivenSearchLayout,
			  GenericFormView: GenericFormView,
			  HomeView: HomeView,
		  }
  };

  return modelsConfig;

});