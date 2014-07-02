package gr.abiss.calipso.web.filters;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Simple {@link HttpServletRequestWrapper} wrapper that returns the supplied
 * HTTP BAsic authentication token and HTTP method for {@link HttpServletRequest#getMethod()}.
 */
class RestRequestNormalizerRequestWrapper extends
		HttpServletRequestWrapper {

	private final String method;

	private Map<String, String> customHeaderMap = new HashMap<String, String>();

	public void addHeader(String name, String value) {
		this.customHeaderMap.put(name, value);
	}

	public RestRequestNormalizerRequestWrapper(HttpServletRequest request,
			String method, String ssoToken) {
		super(request);
		
		// setup HTTP method
		if(org.apache.commons.lang3.StringUtils.isNotBlank(method)){
			this.method = method.toUpperCase(Locale.ENGLISH);
		}
		else{
			this.method = request.getMethod();
		}
		
		// setup SSO auth token
		if(org.apache.commons.lang3.StringUtils.isNotBlank(ssoToken)){
			this.addHeader("Authorization", "Basic " + ssoToken);
		}			
	}

	@Override
	public String getMethod() {
		return this.method;
	}

	@Override
	public String getHeader(String name) {
        String header = super.getHeader(name);
        if("accept".equalsIgnoreCase(name)){
        	header = "application/json";
        }
		if (header == null) {
			header = this.customHeaderMap.get(name);
		}
        return header; // Note: you can't use getParameterValues() here.
    }

	@Override
	public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        names.add("Accept");
		names.addAll(this.customHeaderMap.keySet());
        return Collections.enumeration(names);
    }
}