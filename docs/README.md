
## Contents

- [Overview](#overview)
- [Maven Modules](#maven-modules)
- [Guides](#guides)

## Overview

The Maven modules of this project produce a WAR that can be used as an overlay in your project. The artifact makes it easy to quickly prototype the full stack of a Spring application and provides many ready to use features such as:

 - Authentication and authorization services, including RESTful Single Sign On
 - OAuth integration with social netorks (facebook, linkedin, google+ etc.), including implicit sign-in and transparent user registration
 - Email services with Thymeleaf templates for email verification, password reset etc.
 - CRUD and Search RESTful services for arbitrary Entity Model classes
 - Similarly domain-driven single-page client based on backbone.marionette
 - JPA and NoSQL persistence
 
 
### Maven Modules
- calipso-hub (this one)
    - [calipso-hub-framework]: development framework components
    - [calipso-hub-utilities]: utility components
    - [calipso-hub-webapp]: deployable web application (WAR)

TThe browser client in the calipso-hub-webapp module provides WAR that serves a single-page application as the UI to interact with RESTful services using JSON(P) over HTTP. The  module provides components like models, views, controllers and UI elements based on [Backbone], [Marionette] and [Bootstrap]. Dependencies are managed using [RequireJS]. The code is available in the folders under calipso-hub-webapp/src/main/webapp.
 

## Guides

- [Checkout and run](checkout_and_build.md)
- [Create a SCRUD service](scrud_service.md)