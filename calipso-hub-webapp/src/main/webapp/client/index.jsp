<!DOCTYPE html>
<%@page contentType="text/html;charset=UTF-8"%>
<html lang="en">
<head>
<%@ include file="../includes/head.jsp" %>
</head>
<body class="page-sidebar-fixed page-footer-fixed">
    <nav id="calipsoHeaderRegion" role="navigation" class="navbar navbar-inverse navbar-fixed-top">
        <!-- -->
    </nav>
    <div id="full-height-wrapper">
	    <div id="calipsoMainContentRegion">
	        <!-- -->
	    </div>
	    <div id="calipsoModalRegion" class="modal fade" tabindex='-1'></div>
	    <div id="hiddenWrapper" style="display: none"></div>
	    
	    <div class="social-form-container">
	    </div>
	    
    </div>
    
    <div id="calipsoFooterRegion">
        <!-- -->
    </div>
    <!-- Placed at the end of the document so the pages load faster -->
    <script data-main="${basePath}/js/@scriptmain@" id="calipso-script-main" src="${basePath}/js/lib/require.js">
    </script>
</body>
</html>
