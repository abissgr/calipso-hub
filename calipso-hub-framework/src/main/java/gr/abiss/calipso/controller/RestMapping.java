package gr.abiss.calipso.controller;

import java.io.Serializable;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

public class RestMapping implements Serializable{
	
	private RequestMappingInfo requestMappingInfo;
	private HandlerMethod handlerMethod;

	public RestMapping(){
		
	}
	public RestMapping(RequestMappingInfo requestMappingInfo, HandlerMethod handlerMethod){
		this.requestMappingInfo = requestMappingInfo;
		this.handlerMethod = handlerMethod;
	}
	
	public RequestMappingInfo getRequestMappingInfo() {
		return requestMappingInfo;
	}

	public void setRequestMappingInfo(RequestMappingInfo requestMappingInfo) {
		this.requestMappingInfo = requestMappingInfo;
	}

	public HandlerMethod getHandlerMethod() {
		return handlerMethod;
	}

	public void setHandlerMethod(HandlerMethod handlerMethod) {
		this.handlerMethod = handlerMethod;
	}

}
