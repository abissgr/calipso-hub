<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page"
	xmlns:c="http://java.sun.com/jsp/jstl/core"
	xmlns:fn="http://java.sun.com/jsp/jstl/functions" version="2.0">

<html lang="en">
	<jsp:directive.page contentType="text/html" pageEncoding="UTF-8" />
	<jsp:output omit-xml-declaration="true" />
	<jsp:output doctype-root-element="HTML"
		doctype-system="about:legacy-compat" />
	<jsp:scriptlet>
	String basePath = gr.abiss.calipso.utils.ConfigurationFactory
					.getConfiguration()
					.getString(gr.abiss.calipso.utils.ConfigurationFactory.BASE_URL);
	pageContext.setAttribute("basePath", basePath);
	</jsp:scriptlet>
	<head>
		<meta charset="utf-8" />
		<title>Calipso</title>
		<meta name="viewport" content="width=device-width, initial-scale=1" />

		<meta name="description" content="" />
		<meta name="author" content="" />
		<link href="${basePath}/css/bootstrap.css" rel="stylesheet" />
		<link href="${basePath}/css/calipso.css" rel="stylesheet" />
		<!--[if lt IE 9]>
      	<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    	<![endif]-->
	</head>
	<body>
		<div id="wrap">
		<div class="navbar navbar-default navbar-fixed-top" role="navigation">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse"
						data-target=".navbar-ex1-collapse">
						<span class="sr-only">Toggle navigation</span> <span
							class="icon-bar"></span> <span class="icon-bar"></span> <span
							class="icon-bar"></span>
					</button>
					<a href="#" class="navbar-brand">calipso</a>
				</div>
				<div class="collapse navbar-collapse navbar-ex1-collapse">
					<ul class="nav navbar-nav pull-right">
						<li class="active "><a href="home">Home</a></li>
						<li><a href="hosts">Hosts</a></li>
							<li><a href="text">Text</a></li>
							<li><a href="users">Users</a></li>
							<li><a href="about">About</a></li>
					</ul>
				</div>
			</div>
		</div>
		
		<!-- page content -->
		<div class="container">
			<div id="main">
				<!-- -->
			</div>
		</div>
		</div>
		<div id="footer">
	      <div class="container">
			<p class="credit text-muted text-center">
				© Copyright 2010 - 2014 <a title="Powered by Abiss.gr" href="http://abiss.gr">Abiss.gr</a>
			</p>
	      </div>
	    </div>
		<!-- Placed at the end of the document so the pages load faster -->
		<script data-main="/js/main" src="/js/lib/require.js">
		<!-- -->
			
		</script>
	</body>
</html>
</jsp:root>
