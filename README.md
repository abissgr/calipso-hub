
## Overview

Calipso-hub is a high level framework for rapid development of maintainable and scalable enterprise applications. It's features include:

 - Authentication and authorization services, including RESTful Single Sign On
 - OAuth integration with social netorks (facebook, linkedin, google+ etc.), including implicit sign-in and transparent user registration
 - Email services with Thymeleaf templates for email verification, password reset etc.
 - Declarative, use-case-driven development on the front-end
 - CRUD, View and Search RESTful services for your Entity Model classes on the back-end
 - JPA and NoSQL persistence for your data

## Documentation

 - [Installation](#installation)
 - [Javascript Stack](#javascript-stack)
   - [Routes](#routes)
     - [Dynamic Routes](#dynamic-routes)
     - [Implicit Routes](#implicit-routes)
     - [Explicit Routes](#explicit-routes)
   - [Views](#views)
     - [Build-in Views](#build-in-views)
     - [Template Helpers](#template-helpers)
   - [Model Metadata](#model-metadata)
     - [Path Fragment](#path-fragment)
     - [Type Name](#type-name)
     - [Fields](#fields)
     - [Use Cases](#use-cases)
   - [Internationalization](#internationalization)
 - [Spring Stack](#spring-stack)
   - [Architecture](#architecture)
     - [Tiers](#tiers)
     - [Service URLs](#service-urls)
     - [Authentication and Authorization](#authentication-and-authorization)
     - [Persistence](#persistence)
     - [Email](#email)
     - [Internationalization](#internationalization)
   - [SCRUD HOWTO](#scrud-howto)


### Installation

See the [checkout and build](docs/checkout_and_build.md) guide.

### Javascript Stack

The javascript stack provides a [responsive](https://en.wikipedia.org/wiki/Responsive_web_design),
[SPA](http://en.wikipedia.org/wiki/Single-page_application) client framework that is compatible
with the [Spring stack](#spring-stack).

The stack goals are productive developers and maintainable code. It allows you to quickly and
consistently implement use cases or other functional requirements declaratively (via JSON notation),
making it natural for new code to be added in the form of reusable components.

#### Routes

##### Dynamic Routes

Assuming the base webapp URL is `/calipso/client` then dynamic routes in the form
of `/calipso/client/urlFragment/useCaseName` apply, where __urlFragment__ matches a model by the
corresponding static property and __useCaseName__ the use case defined within the model (or a super type)
under the same key. For example, `/calipso/client/books/publish` mathes the following:


```javascript
var BookModel = Calipso.model.GenericModel.extend({

},
// static members
{
    // Use this model for routes starting with "books"
    pathFragment : "books",
    // Define or override the use cases of this model type. See also  
    // the [Use Cases section](#use-cases) for more details.
    useCases : {
        // Each use case matches it's own URL route, for example
        // this one matches "books/publish"
        publish :{
            // use case configuration...
        }
    }
});
```

##### Implicit Routes

In absence of a use case key like `/calipso/client/urlFragment`, the __search__ use case is used, i.e.
`/calipso/client/urlFragment/search`.

##### Explicit Routes

Explicit routes are defined in the usual backbone/marionette way when needed.


#### Views

##### Build-in Views

##### Template Helpers

#### Model Metadata

Calipso defines a metadata profile for it's backbone models. The metadata are used for
dynamically handle URL routes, render fields of a form or grid and more.

Metadata are typically defined/overriden declaratively as static properties (e.g. `pathFragment`)
of a model type. The corresponding getter methods can be used as well (e.g. `getPathFragment`).

The following is a typical example:

```javascript
var BookModel = Calipso.model.GenericModel.extend({

},
// static members
{
    // Use this model for routes starting with "books"
    pathFragment : "books",
    // Define the fields of this model type. See also  
    // the [Fields section](#fields) for more details
    fields : {
        name : {
            "datatype" : "String",
        },
        isbn : {
            // custom field type
            "datatype" : MyIsbnType,
        },
        edit : {
            "datatype" : "Edit",
        },
    },
    // Define or override the use cases of this model type. See also  
    // the [Use Cases section](#use-cases) for more details.
    useCases : {
        // Each use case matches its own URL route, for example
        // this one matches "books/publish"
        publish :{
            // ...
        }
    }
});
```

##### Path Fragment

##### Type Name

##### Fields

##### Use Cases

#### Internationalization

### Spring Stack

#### Architecture

The [stateless](https://en.wikipedia.org/wiki/Stateless_protocol) back-end is build on top of the [Spring Framework](https://projects.spring.io/spring-framework/) and provides dynamic, model driven [RESTful](https://en.wikipedia.org/wiki/Representational_state_transfer) services for your entities, including complete coverage of [SCRUD](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete) use cases.

##### Tiers

##### Service URLs

##### Authentication and Authorization

##### Persistence

Relational databases are supported by [JPA](https://en.wikipedia.org/wiki/Java_Persistence_API) ([Hibernate](http://hibernate.org/) is used under the hood).
[NoSQL](https://en.wikipedia.org/wiki/NoSQL) stores like [MongoDB](https://www.mongodb.org/), [Cassandra](http://cassandra.apache.org/),
[Couchbase](http://www.couchbase.com) and [Neo4j](http://neo4j.com/) are supported as well, while application instances also contain
their own clusterable [ElasticSearch](https://www.elastic.co/) node by default.

##### Email

##### Internationalization

#### SCRUD HOWTO

Check out the [SCRUD HOWTO](docs/scrud_howto.md) guide.
