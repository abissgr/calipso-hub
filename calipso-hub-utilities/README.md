
- [Installation](#installation)
- [Elasticsearch integration](#elasticsearch-integration)
    - [Spring configuration](#spring-configuration)
    - [Servlet configuration](#servlet-configuration)

Provides generic utilities such as configuration access, elasticsearch integration and more. 
The calipso-hub-utilities module is part of [calipso-hub][calipso-hub]. 

## Installation

Include the dependency in your project's Maven POM:

```xml
    <!-- add in dependencies -->
    <dependency>
        <groupId>gr.abiss.calipso</groupId>
        <artifactId>calipso-hub-utilities</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>


    <!--  add in repositories -->
    <repository>
        <id>sonatype-snapshot</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>sonatype-release</id>
        <url>https://oss.sonatype.org/content/repositories/releases</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
```

## Elasticsearch integration

Calipso uses [spring-data-elasticsearch] for spring/elasticsearch integration and [elasticsearch-transport-wares] 
to directly expose REST services of an embedded elasticsearch node. To calipso-hub-utilities module provides some 
classes to make the two use the same local elastic search Node. 

### Spring configuration

The following configuration creates a local elasticsearch Node and passes a client for it to spring-data-elasticsearch:

```xml
    <!-- create a local node, loads elasticsearch/elasticsearch.yml from the classpath -->
    <bean id="localElasticsearchNode" class="gr.abiss.calipso.utils.elasticsearch.spring.LocalNodeFactoryBean" />
    
    <!-- create a node client for the above local node -->
    <bean id="localElasticsearchNodeClient" class="gr.abiss.calipso.utils.elasticsearch.spring.LocalNodeClientFactoryBean" />
    
    <!-- spring-data-elasticsearch repositories -->
    <elasticsearch:repositories base-package="com.civicuk.ffora">
        <repository:include-filter type="regex" expression=".*IndexRepository" />
    </elasticsearch:repositories>
    
    <!-- make spring-data-elasticsearch use our local node client -->
    <bean name="elasticsearchTemplate" class="org.springframework.data.elasticsearch.core.ElasticsearchTemplate">
        <constructor-arg name="client" ref="localElasticsearchNodeClient" />
    </bean>
```

### Servlet configuration

The following web.xml configuration creates a servlet used to dispatch requests to the local elasticsearch Node:

```xml
    <!-- servlet definition -->
    <servlet>   
        <servlet-name>elasticsearch-servlet</servlet-name>
        <servlet-class>gr.abiss.calipso.utils.elasticsearch.NodeServlet</servlet-class>
    </servlet>
    
    
    <!-- servlet mapping definition -->
    <servlet-mapping>
        <servlet-name>elasticsearch-servlet</servlet-name>
        <url-pattern>/MY/URL/MAPPING/*</url-pattern>
    </servlet-mapping>
```


[calipso-hub]:../
[elasticsearch]:http://www.elasticsearch.org
[spring-data-elasticsearch]:https://github.com/spring-projects/spring-data-elasticsearch
[Elastic HQ Plugin]:http://www.elastichq.org/support_plugin.html
[elasticsearch-transport-wares]:https://github.com/elasticsearch/elasticsearch-transport-wares