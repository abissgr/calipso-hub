
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
    String basePath = gr.abiss.calipso.utils.ConfigurationFactory
            .getConfiguration()
            .getString(
                    gr.abiss.calipso.utils.ConfigurationFactory.BASE_URL);
    pageContext.setAttribute("basePath", basePath);
%>
    <meta charset="utf-8" />
    <title>Calipso-hub</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <meta name="description" content="" />
    <meta name="author" content="" />
    <link type="text/css" href="${basePath}/css/main.css" rel="stylesheet" />
    <!--[if lt IE 9]>
        <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->