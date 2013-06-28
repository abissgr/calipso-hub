calipso-hub-jpasearch
=====================

Provides generic, reflection based search and form schemas [1] for arbitrary entity beans. 
Property types supported by the provided Spring specifications
are Boolean, Date, enum, ManyToOne (for members extending Spring's AbstractPersistable) and String.

To use your components should just extend the following classes without implementing anything:

Search is available at

/api/<entity, e.g. user>?params

Form schemata is available at 

/api/<entity, e.g. user>/form-schema?mode=<one of create, updat, search>

controller: AbstractServiceBasedRestController
repo: BaseRepository(Impl), see also "1.3.2 Adding custom behavior to all repositories" [2].

Under development: dynamic service for form schemas, see [2].

[1] https://github.com/powmedia/backbone-forms
[2] http://static.springsource.org/spring-data/commons/docs/current/reference/html/repositories.html
