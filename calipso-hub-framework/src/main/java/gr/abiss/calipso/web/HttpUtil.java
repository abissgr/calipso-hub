package gr.abiss.calipso.web;

import gr.abiss.calipso.utils.Constants;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;


public class HttpUtil {


    public static String setBaseUrl(ServletRequest req) {
        HttpServletRequest request = (HttpServletRequest) req;
        String baseUrl = (String) request.getAttribute(Constants.BASE_URL_KEY);
        if (StringUtils.isBlank(baseUrl)) {
            StringBuffer url = request.getRequestURL();
            String uri = request.getRequestURI();
            String ctx = request.getContextPath();
            baseUrl = url.substring(0, url.length() - uri.length() + ctx.length());
            request.setAttribute(Constants.BASE_URL_KEY, baseUrl);
            request.setAttribute(Constants.DOMAIN_KEY, request.getServerName());
        }
        return baseUrl;
    }
}
