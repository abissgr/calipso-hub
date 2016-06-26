<%@page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
    org.apache.commons.configuration.Configuration configuration = 
    	    gr.abiss.calipso.utils.ConfigurationFactory.getConfiguration();

	String basePath = configuration.getString(
	        gr.abiss.calipso.utils.ConfigurationFactory.BASE_URL);
	String appName = configuration.getString(
	        gr.abiss.calipso.utils.ConfigurationFactory.APP_NAME);
	String appVersion = configuration.getString(
	        gr.abiss.calipso.utils.ConfigurationFactory.APP_VERSION);

    pageContext.setAttribute("basePath", basePath);
    pageContext.setAttribute("appName", appName);
    pageContext.setAttribute("appVersion", appVersion);
    
%>