#calipso-hub-jpasearch

Provides generic, reflection based search services and form schemas [1] for arbitrary entity beans. 
Property types supported by the provided Spring specifications
are Boolean, Date, enum, ManyToOne (for members extending Spring's AbstractPersistable) and String.

## Howto

To use this module in your RESTHub project you need to do the following:

0) Include the dependency in your project's Maven POM (TODO: point to repo):

```xml
<dependency>
    <groupId>gr.abiss.calipso</groupId>
    <artifactId>calipso-hub-jpasearch</artifactId>
	<version>${project.version}</version>
</dependency>
```

1) Have your entities implement org.springframework.data.domain.Persistable

```java
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "users")
public class User implements Persistable<String> {
//...
```

2) Add Calipso's custom repository factory in your Spring config:

```xml
<!-- Dynamic JPA Search -->
<jpa:repositories base-package="my.package" 
	factory-class="gr.abiss.calipso.jpasearch.repository.RepositoryFactoryBean" />
```
see also "1.3.2 Adding custom behavior to all repositories" [2].

3) Have your repository interfaces extend BaseRepository:

```java
import gr.abiss.calipso.jpasearch.repository.BaseRepository;

public interface UserRepository extends BaseRepository<User, String> {
}
```

4) Have your service interfaces extend GenericService instead of RESTHub's CrudService:

```java
import gr.abiss.calipso.jpasearch.service.GenericService;

public interface UserService extends GenericService<User, String> {
}
```

5) Similarly, have your service implementations extend GenericServiceImpl:

```java
import gr.abiss.calipso.jpasearch.service.impl.GenericServiceImpl;

@Named("userService")
public class UserServiceImpl 
	extends GenericServiceImpl<User, String, UserRepository> 
	implements UserService {
}
```

6) Have the appropriate controllers extend gr.abiss.calipso.jpasearch.controller.AbstractServiceBasedRestController
instead of RESTHub's ServiceBasedRestController:

```java
import gr.abiss.calipso.jpasearch.controller.AbstractServiceBasedRestController;

@Controller
@RequestMapping(value = "/api/user", produces = { "application/json", "application/xml" })
public class UserController 
	extends AbstractServiceBasedRestController<User, String, UserService> {
}
```

## Access the search
Search is available at the usual RESTHub path:

	http://localhost:8080/api/[entity, e.g. user]?[params]
	
e.g.:

	http://localhost:8080/api/user?firstName=Manos

## Dynamic form schemas

Form schemata is available at 

	http://localhost:8080/api/[entity, e.g. user]/form-schema?mode=[one of create, update, search]

The form schemas produced are annotation based (see gr.abiss.calipso.jpasearch.annotation.FormSchemaEntry) 
and follow the format described at [2].

[1] https://github.com/powmedia/backbone-forms
[2] http://static.springsource.org/spring-data/commons/docs/current/reference/html/repositories.html
