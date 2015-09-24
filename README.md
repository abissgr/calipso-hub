
## Overview

The Maven modules of this project produce a WAR that can be used as an overlay in your project. The artifact makes it easy to quickly prototype the full stack of a Spring application and provides many ready to use features such as:

 - Authentication and authorization services, including RESTful Single Sign On
 - OAuth integration with social netorks (facebook, linkedin, google+ etc.), including implicit sign-in and transparent user registration
 - Email services with Thymeleaf templates for email verification, password reset etc.
 - CRUD and Search RESTful services for arbitrary Entity Model classes
 - Similarly domain-driven single-page client based on backbone.marionette
 - JPA and NoSQL pesristance

## Contents

- [Checkout and run](#checkout-and-run)
    - [Checkout using Eclipse](#checkout-using-eclipse)
    - [Checkout using the Command Line](#checkout-using-the-command-line)
    - [Maven build](#maven-build)
    - [Browse Calipso](#browse-calipso)
    - [Browse the Database Console](#browse-the-database-console)
    - [Use a custom database](#use-a-custom-database)
- [Developer Guide](#developer-guide)
    - [Modules](#modules)
    - [Front-end Client](#front-end-client)
        - [Available APIs](#available-apis)
    - [Service Back-end](#service-back-end)
        - [Create a RESTful service](#create-a-restful-service)


## Checkout and run

To build Calipso you need a Java Development Kit version (1.)7, Git and Apache Maven. Eclipse (Java or JEE) IDE users need EGit and m2eclipse plugins, those are included in Eclipse Java/JEE Luna and above. Read bellow for instructions to checkout the source using eclipse or the command line.

### Checkout using Eclipse

![alt tag](https://github.com/abissgr/calipso-hub/blob/master/src/main/site/img/readme/010_import_dialog.png)

Right-click in Eclipse's Package/Project Explorer and click Import, then choose Git > Projects from Git

![alt tag](https://github.com/abissgr/calipso-hub/blob/master/src/main/site/img/readme/011_clone_uri.png)

Select the "Clone URI" option, then click "Next"

![alt tag](https://github.com/abissgr/calipso-hub/blob/master/src/main/site/img/readme/012_import_from_git.png)

Enter https://github.com/abissgr/calipso-hub.git as the clone URI

![alt tag](https://github.com/abissgr/calipso-hub/blob/master/src/main/site/img/readme/014_local_dest.png)

Select your local files location (independent to your Eclipse workspace)

![alt tag](https://github.com/abissgr/calipso-hub/blob/master/src/main/site/img/readme/015_wizard.png)

Select the "Import as general project" wizard

![alt tag](https://github.com/abissgr/calipso-hub/blob/master/src/main/site/img/readme/016_project_name.png)

Use "calipso-hub" as the project name

![alt tag](https://github.com/abissgr/calipso-hub/blob/master/src/main/site/img/readme/017_import_existing_maven.png)

Right click on the "calipso-hub" then click on "Import". Choose "Existing Maven projects". Browse to the project folder for eclipse to discover the maven submodules, then click "Finish".


### Checkout using the Command Line

See https://help.github.com/articles/importing-a-git-repository-using-the-command-line/
 
### Maven build 

[![Build Status](https://travis-ci.org/abissgr/calipso-hub.png?branch=master)](https://travis-ci.org/abissgr/calipso-hub)

To build Calipso you need a Java Development Kit(JDK7 ) and Apache Maven installed. 

1) Change to the root project directory:

    $ cd calipso-hub

2) Rename HOWTO.txt to dev.properties and (optionally) edit it: 

    $ mv HOWTO.txt dev.properties

3) Build the project 

    $ mvn clean install

4) Change to webapp module directory 

    $ cd calipso-hub-webapp

5) Start the Jetty server 

    $ mvn clean install jetty:run
    
Note: If you get a <code>[ERROR] No plugin found for prefix 'jetty' in the current project and in the plugin groups </code> you probably forgot step 4 above.

    
## Browse Calipso

After you complete the steps above, you can access Calipso using your web browser: 

    http://localhost:8080/calipso
    
## Browse the Database Console 

By default, Calipso uses H2, an in-memory database that works great for development.
A fresh copy is created with test data each time you start Jetty using mvn:run. 
The database console is available at: 

	http://localhost:8080/calipso/console/database/
	

## Use a custom database 

You can use a database like MySQL by commenting out the H2 database section in your dev.properties, 
then uncommenting the MySQL section. Other databases can be also be used.  
    
# Developer Guide

## Modules
- calipso-hub (this one)
    - [calipso-hub-framework]: development framework components
    - [calipso-hub-utilities]: utility components
    - [calipso-hub-webapp]: deployable web application (WAR)


## Front-end Client

TThe browser client provides a UI to interact with RESTful services using JSON(P) over HTTP

### Available APIs

The calipso-hub-webapp module provides components like models, views, controllers and UI elements based on [Backbone], [Marionette] and [Bootstrap]. Dependencies are managed using [RequireJS]. The code is available in the folders under src/main/webapp.

## Service Back-end 

### Create a RESTful service

1) Create a domain/entity class, something like:

    https://github.com/abissgr/calipso-hub/blob/master/calipso-hub-framework/src/main/java/gr/abiss/calipso/model/Role.java

2) Create a repository class, something like:

    https://github.com/abissgr/calipso-hub/blob/master/calipso-hub-framework/src/main/java/gr/abiss/calipso/repository/RoleRepository.java
    
3) Create a service interface, something like:

    https://github.com/abissgr/calipso-hub/blob/master/calipso-hub-framework/src/main/java/gr/abiss/calipso/service/RoleService.java    
    
   
4) Create a service implementation, something like:

    https://github.com/abissgr/calipso-hub/blob/master/calipso-hub-framework/src/main/java/gr/abiss/calipso/service/impl/RoleServiceImpl.java
    
    
[calipso-hub-framework]:calipso-hub-framework
[calipso-hub-utilities]:calipso-hub-utilities
[calipso-hub-webapp]:calipso-hub-webapp
[Backbone]:http://backbonejs.org
[Marionette]:http://marionettejs.com
[Bootstrap]:http://getbootstrap.com
[RequireJS]:http://requirejs.org
    
