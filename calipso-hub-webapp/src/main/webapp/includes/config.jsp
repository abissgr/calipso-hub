<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.restdude.util.HttpUtil" %>
<%@ page import="gr.abiss.calipso.utils.ConfigurationFactory" %>
<%@ page import="org.apache.commons.configuration.Configuration" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%

	String basePath = HttpUtil.setBaseUrl(pageContext.getRequest());
	System.out.println("config.jsp, basePath: " + basePath);
	Configuration configuration = ConfigurationFactory.getConfiguration();
	String appName = configuration.getString(ConfigurationFactory.APP_NAME);
	String appVersion = configuration.getString(ConfigurationFactory.APP_VERSION);

    pageContext.setAttribute("basePath", basePath);
    pageContext.setAttribute("appName", appName);
    pageContext.setAttribute("appVersion", appVersion);
    
%>