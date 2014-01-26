
- [Modules](#modules)
- [Howto Build](#howto-build)
- [Browse Calipso](#browse-calipso)
- [Browse the Database Console](#browse-the-database-console)
- [Use a custom database](#use-a-custom-database)

## Modules
- calipso-hub (this one)
    - [calipso-hub-core]: application-specific components
    - [calipso-hub-framework]: development framework components
    - [calipso-hub-utilities]: utility components
    - [calipso-hub-webapp]: WAR

## Howto Build

To build Calipso you need a Java Development Kit and Apache Maven installed. 

0) To start, checkout the project:

    $ git clone git://github.com/abissgr/calipso-hub.git

1) Change to the project base directory:

    $ cd calipso-hub

2) Rename HOWTO.txt to dev.properties and (optionally) edit it: 

    $ mv HOWTO.txt dev.properties

3) Build the project 

    $ mvn clean install

4) Change to webapp module directory 

    $ cd calipso-hub-webapp

5) Start the Jetty server 

    $ mvn clean install jetty:run
    
## Browse Calipso

After you complete the steps above, you can access Calipso using your web browser: 

    http://localhost:8080/
    
## Browse the Database Console 

By default, Calipso uses H2, an in-memory database that works great for development.
A fresh copy is created with test data each time you start Jetty using mvn:run. 
The database console is available at: 

	http://localhost:8080/console/database/
	

## Use a custom database 

You can use a database like MySQL by commenting out the H2 database section in your dev.properties, 
then uncommenting the MySQL section. Other databases can be also be used.  
    
[calipso-hub-core]:calipso-hub-core
[calipso-hub-framework]:calipso-hub-framework
[calipso-hub-utilities]:calipso-hub-utilities
[calipso-hub-webapp]:calipso-hub-webapp
