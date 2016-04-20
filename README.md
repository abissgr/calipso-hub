
## Overview

High level framework for rapid development of maintainable enterprise applications. Back-end SCRUD services are very model driven, while the SPA webapp framework allows your view structures to be defined declaratively as usecases described in JSON. 

The back-end uses Spring framework components. Relational databases are supported by JPA. NoSQL stores are supported as well, while application instances also contain their own clusterable ElasticSearch node.

The Maven modules of this project produce a WAR that can be used as an overlay in your project. The artifact makes it easy to quickly prototype the full stack of a Spring application and provides many ready to use features such as:

 - Authentication and authorization services, including RESTful Single Sign On
 - OAuth integration with social netorks (facebook, linkedin, google+ etc.), including implicit sign-in and transparent user registration
 - Email services with Thymeleaf templates for email verification, password reset etc.
 - CRUD, View and Search RESTful services for your Entity Model classes
 - Similarly domain-driven single-page client based on backbone.marionette
 - JPA and NoSQL persistence

## Documentation

Check out the [docs folder](docs).
