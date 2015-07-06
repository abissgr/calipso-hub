/**
 *
 *
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gr.abiss.calipso.controller;


import gr.abiss.calipso.jpasearch.annotation.ApplyCurrentPrincipal;
import gr.abiss.calipso.jpasearch.annotation.CurrentPrincipalIdPredicate;
import gr.abiss.calipso.jpasearch.data.ParameterMapBackedPageRequest;
import gr.abiss.calipso.jpasearch.data.RestrictionBackedPageRequest;
import gr.abiss.calipso.jpasearch.model.FormSchema;
import gr.abiss.calipso.jpasearch.model.structuredquery.Restriction;
import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.dto.MetadatumDTO;
import gr.abiss.calipso.model.dto.ReportDataSet;
import gr.abiss.calipso.model.entities.FormSchemaAware;
import gr.abiss.calipso.model.types.TimeUnit;
import gr.abiss.calipso.service.GenericEntityService;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.userDetails.util.SecurityUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.lucene.index.Fields;
import org.resthub.common.exception.NotFoundException;
import org.resthub.web.controller.ServiceBasedRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Controller
@RequestMapping(produces = { "application/json", "application/xml" })
@Api(description = "All generic operations for entities", value = "")
public abstract class AbstractServiceBasedRestController<T extends Persistable<ID>, ID extends Serializable, S extends GenericEntityService<T, ID>>
		extends ServiceBasedRestController<T, ID, S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceBasedRestController.class);
 
	@Autowired
	protected HttpServletRequest request;

	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	/**
	 * Find all resources matching the given criteria and return a paginated
	 * collection<br/>
	 * REST webservice published : GET
	 * /search?page=0&size=20&properties=sortPropertyName&direction=asc
	 * 
	 * @param page
	 *            Page number starting from 0 (default)
	 * @param size
	 *            Number of resources by pages. default to 10
	 * @return OK http status code if the request has been correctly processed,
	 *         with the a paginated collection of all resource enclosed in the
	 *         body.
	 */
	@Override
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "find (paginated)", notes = "Find all resources matching the given criteria and return a paginated collection", httpMethod = "GET") 
	public Page<T> findPaginated(
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "properties", required = false, defaultValue = "id") String sort,
			@RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction) {
		boolean applyCurrentPrincipalIdPredicate = true;
		
		if(BooleanUtils.toBoolean(request.getParameter("skipCurrentPrincipalIdPredicate"))){
			applyCurrentPrincipalIdPredicate = false;
			LOGGER.debug("Skipping CurrentPrincipalIdPredicate");
		}
		
		return findPaginated(page, size, sort, direction, request.getParameterMap(), applyCurrentPrincipalIdPredicate);
	}



	protected Page<T> findPaginated(Integer page, Integer size, String sort,
			String direction, Map<String, String[]> paramsMap, boolean applyImplicitPredicates) {

		// add implicit criteria?
		Map<String, String[]> parameters = null;
		if(applyImplicitPredicates){
			LOGGER.info("Adding implicit predicates");
			parameters = new HashMap<String, String[]>();
			parameters.putAll(paramsMap);
			CurrentPrincipalIdPredicate predicate = this.service.getDomainClass().getAnnotation(CurrentPrincipalIdPredicate.class);
			if(predicate != null){
				ICalipsoUserDetails principal = this.service.getPrincipal();
				String[] excludeRoles = predicate.ignoreforRoles();
				boolean skipPredicate = this.hasAnyRoles(predicate.ignoreforRoles());
				if(!skipPredicate){
					String id = principal != null ? principal.getId() : "ANONYMOUS";
					String[] val = {id};
					LOGGER.info("Adding implicit predicate, name: " + predicate.path() + ", value: " + id);
					parameters.put(predicate.path(), val);
				}
				else{
					LOGGER.info("Skipping implicit predicate, name: " + predicate.path());
				}
				
			}
		}
		else{
			LOGGER.info("Skipping implicit predicates");
			parameters = paramsMap;
		}
		
		Pageable pageable = buildPageable(page, size, sort, direction, parameters);
		return this.service.findAll(pageable);
				
	}



	protected boolean hasAnyRoles(String[] roles) {
		boolean skipPredicate = false;
		for(int i = 0; i < roles.length; i++){
			if(request.isUserInRole(roles[i])){
				skipPredicate = true; 
				break;
			}
		}
		return skipPredicate;
	}



	protected Pageable buildPageable(Integer page, Integer size, String sort,
			String direction, Map<String, String[]> paramsMap) {
		Assert.isTrue(page >= 0, "Page index must be greater than, or equal to, 0");
		Order order = new Order(
				direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC
						: Sort.Direction.DESC, sort);
		List<Order> orders = new ArrayList<Order>(1);
		orders.add(order);
		Pageable pageable = new ParameterMapBackedPageRequest(paramsMap, page /*- 1*/, size, new Sort(orders));
		return pageable;
	}
	
	

    /**
     * {@inheritDoc}
     */
	@Override
	@RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiOperation(value = "create", notes = "Create a new resource", httpMethod = "POST")
	////@ApiResponse(code = 201, message = "created")
	public T create(@RequestBody T resource) {
		applyCurrentPrincipal(resource);
		return super.create(resource);
	}



	protected void applyCurrentPrincipal(T resource) {
		Field[] fields = FieldUtils.getFieldsWithAnnotation(this.service.getDomainClass(), ApplyCurrentPrincipal.class);
		//ApplyPrincipalUse predicate = this.service.getDomainClass().getAnnotation(CurrentPrincipalIdPredicate.class);
		if(fields.length > 0){
			ICalipsoUserDetails principal = this.service.getPrincipal();
			for(int i = 0; i < fields.length; i++){
				Field field = fields[i];
				ApplyCurrentPrincipal applyRule = field.getAnnotation(ApplyCurrentPrincipal.class);
				
				// if property is not already set
				try {
					if(field.get(resource) == null){
						boolean skipApply = this.hasAnyRoles(applyRule.ignoreforRoles());
						// if role is not ignored
						if(!skipApply){
							String id = principal != null ? principal.getId() : null;
							if(id != null){
								User user = new User();
								user.setId(id);
								LOGGER.info("Applying principal to field: " + field.getName() + ", value: " + id);
								field.set(resource, user);
							}
							else{
								LOGGER.warn("User is anonymous, cannot apply principal to field: " + field.getName());
							}
						}
						else{
							LOGGER.info("Skipping setting principal to field: " + field.getName());
						}
					}
				} catch (Exception e) {
					throw new RuntimeException("Failed to apply ApplyCurrentPrincipal annotation", e);
				}
				
			}
			
			
		}
	}



    /**
     * {@inheritDoc}
     */
	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "update", notes = "Update a resource", httpMethod = "PUT")
	//@ApiResponse(code = 200, message = "OK")
	public T update(@ApiParam(name = "id", required = true, value = "string") @PathVariable ID id, @RequestBody T resource) {
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("update, resource: "+resource);
		}
		applyCurrentPrincipal(resource);
		return super.update(id, resource);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@RequestMapping(method = RequestMethod.GET, params="page=no", produces="application/json")
    @ResponseBody
    @ApiOperation(value = "find all", notes = "Find all resources, and return the full collection (i.e. VS a page of the total results)", httpMethod = "GET")
	//@ApiResponse(code = 200, message = "OK")
	public Iterable<T> findAll() {
		return super.findAll();
	}



	/**
     * Find a resource by its identifier, include it's schema in the response if available
     *
     * @param id The identifier of the resouce to find
     * @return OK http status code if the request has been correctly processed, with resource found enclosed in the body
     * @throws NotFoundException
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    //@ResponseView(AbstractPersistable.FormSchemaAwareView.class)
    @ResponseBody
    @ApiOperation(value = "find by id", notes = "Find a resource by it's identifier", httpMethod = "GET")
	public T findById(@ApiParam(name = "id", required = true, value = "string") @PathVariable ID id) {
    	T resource = null;
    	
    	return super.findById(id);
	}

	/**
     * Obtain a newly created resource instance.
     * @return OK http status code if the request has been correctly processed, with resource found enclosed in the body
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    //@ResponseView(AbstractPersistable.FormSchemaAwareView.class)
    @ResponseBody
    @ApiOperation(value = "obtain new unpersisted instance", notes = "Instantiates and returns a new reszource object", httpMethod = "GET")
	public T getSchemaWrapperInstance() {
    	T resource = null;
    	try {
			resource = this.service.getDomainClass().newInstance();
			if(FormSchemaAware.class.isAssignableFrom(resource.getClass())){
				FormSchema.setToInstance(((FormSchemaAware) resource));
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed creating new resource instance", e);
		}
    	return resource;
	}



    /**
     * Delete a resource by its identifier. 
     * Return No Content http status code if the request has been correctly processed
     *
     * @param id The identifier of the resource to delete
     * @throws NotFoundException
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "delete", notes = "Delete a resource by its identifier. ", httpMethod = "DELETE")
	public void delete(@ApiParam(name = "id", required = true, value = "string") @PathVariable ID id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}



	/**
	 * Find all resources matching the given criteria and return a paginated
	 * collection<br/>
	 * REST webservice published : GET
	 * /search?page=0&size=20&properties=sortPropertyName&direction=asc
	 * 
	 * @param restriction
	 *            the structured query as a Restriction instance
	 * @return OK http status code if the request has been correctly processed,
	 *         with the a paginated collection of all resource enclosed in the
	 *         body.
	 */
	@RequestMapping(value = "query", produces = { "application/json" }, method = RequestMethod.POST)
	@ResponseBody
	@Deprecated
	@ApiOperation(value = "deprecated: find paginated with restrictions", httpMethod = "POST")
	public Page<T> findPaginatedWithRestrictions(
			@RequestBody Restriction restriction) {
		return this.service.findAll(new RestrictionBackedPageRequest(restriction));
	}
//    
//	// TODO: refactor to OPTIONS on base path?
//	@RequestMapping(value = "form-schema", produces = { "application/json" }, method = RequestMethod.GET)
//	@ResponseBody
//    @ApiOperation(value = "get form schema", notes = "Get a form achema for the controller entity type", httpMethod = "GET")
//	public FormSchema getSchema(
//			@RequestParam(value = "mode", required = false, defaultValue = "search") String mode) {
//		mode = mode.toUpperCase();
//		try {
//			Class domainClass = ((GenericService<Persistable<ID>, ID>) this.service).getDomainClass();
//			if(FormSchemaAware.class.isAssignableFrom(domainClass)){
//				FormSchema.setToInstance(((FormSchemaAware) resource));
//			}
//			FormSchema schema = new FormSchema();
//			schema.setDomainClass(
//					();
//			schema.setAction(mode);
//			return schema;
//		} catch (Exception e) {
//			throw new NotFoundException();
//		}
//	}

//	@RequestMapping(produces = { "application/json" }, method = RequestMethod.OPTIONS)
//	@ResponseBody
//    @ApiOperation(value = "get form schema", notes = "Get a form achema for the controller entity type", httpMethod = "OPTIONS")
//	public FormSchema getSchemas(
//			@RequestParam(value = "mode", required = false, defaultValue = "search") String mode) {
//		return this.getSchema(mode);
//	}

//	@RequestMapping(value = "apidoc", produces = { "application/json" }, method = {
//			RequestMethod.GET, RequestMethod.OPTIONS })
//	@ResponseBody
//	public List<RestMapping> getRequestMappings() {
//		List<RestMapping> mappings = new LinkedList<RestMapping>();
//	    Map<RequestMappingInfo, HandlerMethod> handlerMethods =
//	                              this.requestMappingHandlerMapping.	getHandlerMethods();
//
//	    for(Entry<RequestMappingInfo, HandlerMethod> item : handlerMethods.entrySet()) {
//	        RequestMappingInfo mapping = item.getKey();
//	        HandlerMethod method = item.getValue();
//	        mappings.add(new RestMapping(mapping, method));
//
//	        for (String urlPattern : mapping.getPatternsCondition().getPatterns()) {
//	            System.out.println(
//	                 method.getBeanType().getName() + "#" + method.getMethod().getName() +
//	                 " <-- " + urlPattern);
//
//	            if (urlPattern.equals("some specific url")) {
//	               //add to list of matching METHODS
//	            }
//	        }
//	    }       
//	    return mappings;
//	}

	// @Secured("ROLE_ADMIN")
	@RequestMapping(value = "{subjectId}/metadata", method = RequestMethod.PUT)
	@ResponseBody
    @ApiOperation(value = "add metadatum", notes = "Add or updated a resource metadatum", httpMethod = "GET")
	public void addMetadatum(@PathVariable ID subjectId,
			@RequestBody MetadatumDTO dto) {
		service.addMetadatum(subjectId, dto);
	}

	// @Secured("ROLE_ADMIN")
	@RequestMapping(value = "{subjectId}/metadata/{predicate}", method = RequestMethod.DELETE)
	@ResponseBody
    @ApiOperation(value = "remove metadatum", notes = "Remove a resource metadatum if it exists", httpMethod = "DELETE")
	public void removeMetadatum(@PathVariable ID subjectId,
			@PathVariable String predicate) {
		service.removeMetadatum(subjectId, predicate);
	}
	

	@RequestMapping(value = "reports", produces = { "application/json" }, method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "reports", httpMethod = "GET")
	public Page<ReportDataSet> getReportDatasets(

			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "properties", required = false, defaultValue = "id") String sort,
			@RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction,
			
			@RequestParam(value = "timeUnit", required = false, defaultValue = "DAY") TimeUnit timeUnit,
			@RequestParam(value = "dateField", required = false, defaultValue = "createdDate") String dateField,
			@RequestParam(value = "dateFrom", required = false) Date dateFrom,
			@RequestParam(value = "dateTo", required = false) Date dateTo,
			@RequestParam(value = "reportType", required = false) String reportType) {
		// default date region is the current day
		//Date now = new Date();

		//if(dateFrom == null){
			GregorianCalendar start = new GregorianCalendar();
			start.set(Calendar.MONTH, start.get(Calendar.MONTH) - 1);
			dateFrom = start.getTime();
		//}
		dateFrom = DateUtils.truncate(dateFrom, Calendar.DATE);
		//if(dateTo == null){
			dateTo = new Date();
		//}
		dateTo = DateUtils.addMilliseconds(DateUtils.ceiling(dateTo, Calendar.DATE), -1);
		
		Map<String, String[]> paramsMap = request.getParameterMap();
		LOGGER.info("getReportDatasets, timeUnit: " + timeUnit + ", dateField: " + dateField + ", dateFrom: " + dateFrom + ", dateTo: " + dateTo + ", reportName: " + reportType );
		Pageable pageable = buildPageable(page, size, sort, direction, paramsMap);
		Page<ReportDataSet> results = this.service.getReportDatasets(pageable, timeUnit, dateField, dateFrom, dateTo, reportType);
		LOGGER.info("getReportDatasets returning " + results.getTotalElements());
		return results;
	}
	
}
