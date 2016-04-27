
## Overview

Calipso-hub is a high level framework for rapid development of maintainable and scalable enterprise applications. It's features include:

 - Authentication and authorization services, including RESTful Single Sign On
 - OAuth integration with social netorks (facebook, linkedin, google+ etc.), including implicit sign-in and transparent user registration
 - Email services with Thymeleaf templates for email verification, password reset etc.
 - Declarative, use-case-driven development on the front-end
 - CRUD, View and Search RESTful services for your Entity Model classes on the back-end
 - JPA and NoSQL persistence for your data

## Documentation

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

##### Implicit Routes

##### Explicit Routes


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
        // Each use case matches it's own URL route, for example
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

The [stateless](https://en.wikipedia.org/wiki/Stateless_protocol) back-end is build on top of the [Spring Framework](https://projects.spring.io/spring-framework/) and provides dynamic, model driven RESTful services for your entities, including complete coverage of SCRUD use cases.

##### Tiers

##### Service URLs

##### Authentication and Authorization

##### Persistence

Relational databases are supported by JPA (Hibernate is used under the hood). 
NoSQL stores are supported as well, while application instances also contain 
their own clusterable ElasticSearch node.

##### Email

##### Internationalization

#### SCRUD HOWTO

Check out the [SCRUD HOWTO](docs/scrud_howto.md) guide.

