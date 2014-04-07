define([
'marionette',
'underscore'
],
 
function(Marionette, _) {
 
var Vent = {};
 
Vent = Backbone.Wreqr.EventAggregator.extend({
 
constructor: function(debug){
 
this.commands = new Backbone.Wreqr.Commands();
this.reqres = new Backbone.Wreqr.RequestResponse();
 
Backbone.Wreqr.EventAggregator.prototype.constructor.apply(this, arguments);
 
}
 
});
 
_.extend(Vent.prototype, {
// Command execution, facilitated by Backbone.Wreqr.Commands
execute: function(){
var args = Array.prototype.slice.apply(arguments);
this.commands.execute.apply(this.commands, args);
},
 
// Request/response, facilitated by Backbone.Wreqr.RequestResponse
request: function(){
var args = Array.prototype.slice.apply(arguments);
return this.reqres.request.apply(this.reqres, args);
}
});
 
return new Vent();
 
});