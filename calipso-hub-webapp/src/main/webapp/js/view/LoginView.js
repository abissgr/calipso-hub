define([
  'underscore',
  'backbone',
  'marionette',
  'hbs!template/login'
],

function (_, Backbone, Marionette, tmpl) {

  return Marionette.ItemView.extend({
    
    className: 'container span4 login',

    template: tmpl,

    triggers: {
      'submit' : 'app:login'
    }

  });
  
});