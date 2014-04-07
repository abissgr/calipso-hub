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
  'model/userDetails',
  'view/md-browse-layout',
  'view/md-search-layout',
  'view/GenericFormView'
],

function ($, _, Backbone, GenericModel, HostModel, ResourceModel, PageModel, TextModel, UserModel, UserDetailsModel, 
		ModelDrivenBrowseLayout, ModelDrivenSearchLayout, GenericFormView){

  var modelsConfig = {
		  models : {
			  GenericModel: GenericModel,
			  HostModel: HostModel,
			  ResourceModel: ResourceModel,
			  PageModel: PageModel,
			  TextModel: TextModel,
			  UserModel: UserModel,
			  UserDetailsModel: UserDetailsModel
		  },

		  layouts : {
			  ModelDrivenBrowseLayout: ModelDrivenBrowseLayout,
			  ModelDrivenSearchLayout: ModelDrivenSearchLayout,
			  GenericFormView: GenericFormView,
		  }
  };

  return modelsConfig;

});