calipso-hub-jpasearch
=====================

Provides generic, reflection based search 
for arbitrary entity beans. Property types supported by the provided Spring specifications
are Boolean, Date, enum, ManyToOne (for members extending Spring's AbstractPersistable) and String.

To use your components should just extend the following classes without implementing anything:

controller: AbstractServiceBasedRestController
repo: BaseRepository(Impl), see also "1.3.2 Adding custom behavior to all repositories" [1].

Under development: dynamic service for form schemas, see [2].

[1] http://static.springsource.org/spring-data/commons/docs/current/reference/html/repositories.html
[2] https://github.com/powmedia/backbone-forms