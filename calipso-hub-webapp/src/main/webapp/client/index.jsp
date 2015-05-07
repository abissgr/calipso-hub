<!DOCTYPE html>
<%@page contentType="text/html;charset=UTF-8" %>
<html lang="en">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
String basePath = gr.abiss.calipso.utils.ConfigurationFactory
.getConfiguration()
.getString(gr.abiss.calipso.utils.ConfigurationFactory.BASE_URL);
pageContext.setAttribute("basePath", basePath);
 %>
	<head>
		<meta charset="utf-8" />
		<title>Calipso</title>
		<meta name="viewport" content="width=device-width, initial-scale=1" />

		<meta name="description" content="" />
		<meta name="author" content="" />
		<link href="${basePath}/css/bootstrap.css" rel="stylesheet" />
		<link href="${basePath}/css/font-awesome/css/font-awesome.css" rel="stylesheet" />
        <!-- SB Admin -->
	    <link href="${basePath}/css/plugins/morris/morris-0.4.3.min.css" rel="stylesheet" />
	    <link href="${basePath}/css/plugins/timeline/timeline.css" rel="stylesheet" />
	    <!-- SB Admin CSS - Include with every page -->
	    <link href="${basePath}/css/sb-admin.css" rel="stylesheet" />
		
        <link href="${basePath}/css/bootstrap-markdown.css" rel="stylesheet" />
        <link href="${basePath}/css/select2.css" rel="stylesheet" />
        <link href="${basePath}/css/bootstrap-select2.css" rel="stylesheet" />
		<link href="${basePath}/css/calipso.css" rel="stylesheet" />
		<link href="${basePath}/css/style.css" rel="stylesheet" />
        <link href="${basePath}/css/bootstrap-datetimepicker.css" rel="stylesheet" />
		<!--[if lt IE 9]>
      	<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    	<![endif]-->
	</head>
	<body class="page-sidebar-fixed page-footer-fixed">
        <div id="container">

	    	<nav id="calipsoHeaderRegion" style="margin-bottom: 0" role="navigation" class="navbar navbar-default navbar-fixed-top">
				<!-- -->
			</nav>
			 <div id="page-wrapper">
	    	<div id="calipsoMainContentRegion">
				<!-- -->
			</div>
			</div>
	    </div>
			
		<div id="calipsoFooterRegion">
			<!-- -->
		</div>
	    <div id="calipsoModalRegion" class="modal hide fade"></div>
	    
		<!-- Placed at the end of the document so the pages load faster 
		 data-main="${basePath}/js/config"
		 -->
        <script id="calipso-script-main" src="${basePath}/js/lib/require.js">
        <!-- -->
        </script>
        <script>
        // load common shim etc. see github.com/requirejs/example-multipage-shim
        require(['${basePath}/js/config.js'], function(config){
      	  // load main app
      	  require(['${basePath}/js/main.js']);
        });
        </script>
	</body>
</html>
