<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    version="2.0">
    
    <jsp:directive.page contentType="text/html" pageEncoding="UTF-8" />
    <jsp:output omit-xml-declaration="true" doctype-root-element="html" doctype-public="" doctype-system="" />
<!-- 
<jsp:directive.taglib uri="http://www.springframework.org/tags" prefix="s" />
<jsp:directive.taglib uri="http://www.springframework.org/spring-social/social/tags" prefix="social" />
 -->
<html lang="en">
<head>
    <c:set var="contextPath" value="${pageContext.request.contextPath}" />
    <meta charset="utf-8" />
    <title>Calipso</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta name="description" content="" />
    <meta name="author" content="" />

    <link href="${contextPath}/css/bootstrap.css" rel="stylesheet" />
    <style>
      body {
        padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
      }
    </style>
    <link href="${contextPath}/css/bootstrap-responsive.css" rel="stylesheet" />
 	<link href="${contextPath}/css/calipso.css" rel="stylesheet" />
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

  </head>

  <body>

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="brand" href="#"><!-- resthub-jpa-backbonejs-multi-archetype --></a>
          <div class="nav-collapse">
            <ul class="nav">
              <li class="active"><a href="home">Home</a></li>
              <li><a href="users">Users</a></li>
              <li><a href="about">About</a></li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <div class="container">
      <h1>Calipso NG</h1>
      
      <div id="main"> 
      <!-- -->
      </div>
    </div>
      
    <!-- Placed at the end of the document so the pages load faster -->
    <script data-main="http://localhost:8080/js/main" src="http://localhost:8080/js/lib/require.js">
    <!-- -->
    </script>
  </body>
</html>
</jsp:root>
