
## Overview

High level framework for rapid development of maintainable and scalable enterprise applications. 

The SSPA JS client provides a responsive UI based on Bootstrap and Backbone.Marionette. The APIs abstract a number of other libraries and allow declarative definitions of view hierarchies described as use cases in JSON notation. Dynamic views and components include DD forms, grids and other UI components, while new code is naturally introduced as components that are reusable and maintainable.

The stateless back-end is build on top of the [Spring Framework](https://projects.spring.io/spring-framework/) and provides dynamic, model driven RESTful services for your entities, including complete coverage iof SCRUD usecases.

Relational databases are supported by JPA. NoSQL stores are supported as well, while application instances also contain their own clusterable ElasticSearch node.

The Maven modules of this project produce a WAR that can be used as an overlay in your project. The artifact makes it easy to quickly prototype the full stack of a Spring application and provides many ready to use features such as:

 - Authentication and authorization services, including RESTful Single Sign On
 - OAuth integration with social netorks (facebook, linkedin, google+ etc.), including implicit sign-in and transparent user registration
 - Email services with Thymeleaf templates for email verification, password reset etc.
 - CRUD, View and Search RESTful services for your Entity Model classes
 - Similarly domain-driven single-page client based on backbone.marionette
 - JPA and NoSQL persistence

## Documentation

Check out the [docs folder](docs).
