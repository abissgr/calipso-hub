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
		<link href="${basePath}/css/bootstrap-markdown.css" rel="stylesheet" />
		<link href="${basePath}/css/calipso.css" rel="stylesheet" />
		<!--[if lt IE 9]>
      	<script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    	<![endif]-->
	</head>
	<body id="calipso-client">
		
	    <div id="wrap">
	    	<div id="calipsoHeaderRegion">
				<!-- -->
			</div>
	    	<div id="calipsoMainContentRegion">
				<!-- -->
			</div>
	    </div>
			
		<div id="calipsoFooterRegion">
			<!-- -->
		</div>
	    <div id="calipsoRegionModal" class="modal hide fade"></div>
	    
		<!-- Placed at the end of the document so the pages load faster -->
		<script data-main="/js/config" src="/js/lib/require.js">
		<!-- -->
		</script>
	</body>
</html>
</jsp:root>
