
# Overview 


Or "How to implement CRUD, View and Search functionality for a new Entity Model".

This guide will help you create CRUD, View and Search services for an entity, along with the Single Page Application as the browser UI to use them. 


## Contents

- [Service URLs and HTTP methods](#service-urls-and-http-methods)
    - [Create](#create)
    - [View](#view)
    - [Update](#update)
    - [Search](#search)
    - [Delete](#delete)
- [Back-end](#back-end)
    - [Entity class](#entity-class)

## Service URLs and HTTP methods

Calipso exposes CRUD, View and Search services for entity models in a regular way. Consider the RESTful URLs for an entity model representing a book. The <e>Entity URL Fragment</e> for that would be "books". Bellow are the service URLs for that EUF.

HTTP Method   | HTTP URL | Action
------------- | ---------|--------------
POST          | localhost:8080/calipso/books | [Create](#create)
GET           | localhost:8080/calipso/books/someId | [View](#view)
PUT           | localhost:8080/calipso/books/someId | [Update](#update)
GET           | localhost:8080/calipso/books?foo=bar&foo.subFoo=baz | [Search](#search)
DELETE        | localhost:8080/calipso/books/someId | [Delete](#delete)


### Create

```
POST http://localhost:8080/calipso/books
```

Create a new book using the request body and return it in the response.


### View

```
GET http://localhost:8080/calipso/books/someId
```

Find the book having "someId" as the id value and return it in the response. Throw an 404 HTTP error if no match is found.


### Update

```
PUT http://localhost:8080/calipso/books/someId
```

Update the book with id "someId" using the request body and return the result in the response. Partial updates are easily supported, just mark your Java entity class using <code>implements PartiallyUpdateable</code> to support partial updates. No actual implementation is needed. 


### Search

```
PUT http://localhost:8080/calipso/books?foo=bar&foo.subFoo=baz
```

Get a paginated collection of all books matching the given criteria. No actual java implementation is required for your entity models, their properties are dynamically mapped to the HTTP parameters by the default. additionally, the following predefined parameters are supported:

Name       | Required | Default | Description
---------- | -------- | ------- | --------------
page       | false    | 0       | Page number starting from 0 (default)
size       | false    | 10      | Page size, default to 10
properties | false    | "id"    | Ordered list of comma-separeted property names used for sorting results. Default is "id"
direction  | false    | "ASC"   | Optional sort direction, either "ASC" or "DESC". Default is "ASC".


### Delete

```
DELETE http://localhost:8080/calipso/books/someId
```

Delete the book having "someId" as the id value.


## Back-end 

### Entity class

You can implement your entity extending and/or implementing a number of interfaces related to ID type, base your entity in 


```java
package gr.abiss.calipsoexample.model;

import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "book")
public class Book extends AbstractSystemUuidPersistable{
    
    @Column(length = 500, nullable = false)
    private String name;
    
    public Book() {
        super();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}

```

### Repository

You can implement your repository just by extending the BaseRepository<T, ID> interface, with T and ID being the entity and class respectively. 

```java
package gr.abiss.calipsoexample.repository;

import gr.abiss.calipsoexample.model.Book;
import gr.abiss.calipso.jpasearch.repository.BaseRepository;

public interface BookRepository extends BaseRepository<Book, String> {
    // that's all!
}
```
### Service

A service requires both interface and implementation classes. However, no implementation code is actually required.

### Service Interface

Just extend the GenericEntityService<T, ID> interface, with T and ID being the entity and class respectively. 

```java
package gr.abiss.calipsoexample.service;

import gr.abiss.calipsoexample.model.Book;
import gr.abiss.calipso.service.GenericEntityService;

public interface BookService extends GenericEntityService<Book, String> {

}
```

### Service Implementation

```java
package gr.abiss.calipsoexample.service.impl;

import gr.abiss.calipso.service.impl.GenericEntityServiceImpl;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.annotation.Qualifier;

import gr.abiss.calipsoexample.model.Book;
import gr.abiss.calipsoexample.repository.BookRepository;
import gr.abiss.calipsoexample.service.BookService;

@Named("bookService")
public class BookServiceImpl extends GenericEntityServiceImpl<Book, String, BookRepository> 
    implements BookService{
    
    @Override
    @Inject
    @Qualifier("bookRepository")
    public void setRepository(BookRepository repository) {
        super.setRepository(repository);
    }   
}
```
    
### Controller

Just extend the AbstractServiceBasedRestController<T, ID, S> interface, with T, ID and S being the entity, id and service classes respectively. 

*Note* the convention for the controller request mapping being  "/api/rest/books", with "books" being the Entity URL Fragment.

```java
package gr.abiss.calipsoexample.controller;

import gr.abiss.calipso.controller.AbstractServiceBasedRestController;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import gr.abiss.calipsoexample.model.Book;
import gr.abiss.calipsoexample.service.BookService;


@Controller
@RequestMapping(value = "/api/rest/books", produces = { "application/json", "application/xml" })
public class BookController extends AbstractServiceBasedRestController<Book, String, BookService> {

    @Override
    @Inject
    @Qualifier("bookService") // somehow required for CDI to work on 64bit JDK?
    public void setService(BookService service) {
        this.service = service;
    }
    
}
```

    
[calipso-hub-framework]:calipso-hub-framework
[calipso-hub-utilities]:calipso-hub-utilities
[calipso-hub-webapp]:calipso-hub-webapp
[Backbone]:http://backbonejs.org
[Marionette]:http://marionettejs.com
[Bootstrap]:http://getbootstrap.com
[RequireJS]:http://requirejs.org
    
