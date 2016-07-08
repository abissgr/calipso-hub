/**
 * calipso-hub-framework - A full stack, high level framework for lazy application hackers.
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package gr.abiss.calipso.tiers.controller;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.imgscalr.Scalr;
import org.resthub.common.exception.NotFoundException;
import org.resthub.web.controller.ServiceBasedRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.annotation.JsonView;

import gr.abiss.calipso.model.User;
import gr.abiss.calipso.model.base.AbstractSystemUuidPersistable;
import gr.abiss.calipso.model.base.PartiallyUpdateable;
import gr.abiss.calipso.model.cms.BinaryFile;
import gr.abiss.calipso.model.dto.MetadatumDTO;
import gr.abiss.calipso.service.cms.BinaryFileService;
import gr.abiss.calipso.tiers.annotation.CurrentPrincipal;
import gr.abiss.calipso.tiers.annotation.CurrentPrincipalField;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.tiers.specifications.GenericSpecifications;
import gr.abiss.calipso.uischema.model.UiSchema;
import gr.abiss.calipso.userDetails.model.ICalipsoUserDetails;
import gr.abiss.calipso.utils.ConfigurationFactory;
import gr.abiss.calipso.web.spring.ParameterMapBackedPageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

public abstract class AbstractModelController<T extends Persistable<ID>, ID extends Serializable, S extends ModelService<T, ID>>
		extends ServiceBasedRestController<T, ID, S> implements ModelController<T, ID, S>{

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelController.class);
 
	private BinaryFileService binaryFileService;
	
	@Autowired
	protected HttpServletRequest request;

	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;

	@Override
	@Inject
	public void setService(S service) {
		this.service = service;
	}
	
	@Inject
	@Qualifier("binaryFileService")
	public void setService(BinaryFileService binaryFileService) {
		this.binaryFileService = binaryFileService;
	}
	

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
	@ApiOperation(value = "Search for resources (paginated).", notes = "Find all resources matching the given criteria and return a paginated collection. Besides the predefinedpaging properties (page, size, properties, direction) all serialized member names of the resource are supported as search parameters/criteria.") 
	public Page<T> findPaginated(
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
			@RequestParam(value = "properties", required = false, defaultValue = "id") String sort,
			@RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction) {
		boolean applyCurrentPrincipalIdPredicate = true;
		
		if(BooleanUtils.toBoolean(request.getParameter("skipCurrentPrincipalIdPredicate"))){
			applyCurrentPrincipalIdPredicate = false;
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("Skipping CurrentPrincipalField");
			}
		}
		
		return findPaginated(page, size, sort, direction, request.getParameterMap(), applyCurrentPrincipalIdPredicate);
	}



	protected Page<T> findPaginated(Integer page, Integer size, String sort,
			String direction, Map<String, String[]> paramsMap, boolean applyImplicitPredicates) {

		// add implicit criteria?
		Map<String, String[]> parameters = null;
		if(applyImplicitPredicates){
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("Adding implicit predicates");
			}
			parameters = new HashMap<String, String[]>();
			parameters.putAll(paramsMap);
			CurrentPrincipalField predicate = this.service.getDomainClass().getAnnotation(CurrentPrincipalField.class);
			if(predicate != null){
				ICalipsoUserDetails principal = this.service.getPrincipal();
				String[] excludeRoles = predicate.ignoreforRoles();
				boolean skipPredicate = this.hasAnyRoles(predicate.ignoreforRoles());
				if(!skipPredicate){
					String id = principal != null ? principal.getId() : "ANONYMOUS";
					String[] val = {id};
					if(LOGGER.isDebugEnabled()){
						LOGGER.debug("Adding implicit predicate, name: " + predicate.value() + ", value: " + id);
					}
					parameters.put(predicate.value(), val);
				}
				
			}
		}
		else{
			if(LOGGER.isDebugEnabled()){
				LOGGER.debug("Skipping implicit predicates");
			}
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

		List<Order> orders = null;
		Sort pageableSort = null;
		if(sort != null && direction != null){
			Order order = new Order(
					direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC
							: Sort.Direction.DESC, sort);
			orders = new ArrayList<Order>(1);
			orders.add(order);
			pageableSort = new Sort(orders);
		}
		Pageable pageable = new ParameterMapBackedPageRequest(paramsMap, page /*- 1*/, size, pageableSort);
		return pageable;
	}
	
	

    /**
     * {@inheritDoc}
     */
	@Override
	@RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @ApiOperation(value = "Create a new resource")
	@JsonView(AbstractSystemUuidPersistable.ItemView.class) 
	public T create(@RequestBody T resource) {
		applyCurrentPrincipal(resource);
		return super.create(resource);
	}



	protected void applyCurrentPrincipal(T resource) {
		Field[] fields = FieldUtils.getFieldsWithAnnotation(this.service.getDomainClass(), CurrentPrincipal.class);
		//ApplyPrincipalUse predicate = this.service.getDomainClass().getAnnotation(CurrentPrincipalField.class);
		if(fields.length > 0){
			ICalipsoUserDetails principal = this.service.getPrincipal();
			for(int i = 0; i < fields.length; i++){
				Field field = fields[i];
				CurrentPrincipal applyRule = field.getAnnotation(CurrentPrincipal.class);
				
				// if property is not already set
				try {
					if(PropertyUtils.getProperty(resource, field.getName()) == null){
						boolean skipApply = this.hasAnyRoles(applyRule.ignoreforRoles());
						// if role is not ignored
						if(!skipApply){
							String id = principal != null ? principal.getId() : null;
							if(id != null){
								User user = new User();
								user.setId(id);
								LOGGER.info("Applying principal to field: " + field.getName() + ", value: " + id);
								PropertyUtils.setProperty(resource, field.getName(), user);
							}
						}
					}
				} catch (Exception e) {
					throw new RuntimeException("Failed to apply CurrentPrincipal annotation", e);
				}
				
			}
			
			
		}
	}
	
	protected void copyBeanProperties(
		    final Object source,
		    final Object target,
		    final Iterable<String> properties){

		    final BeanWrapper src = new BeanWrapperImpl(source);
		    final BeanWrapper trg = new BeanWrapperImpl(target);

		    for(final String propertyName : properties){
		        trg.setPropertyValue(
		            propertyName,
		            src.getPropertyValue(propertyName)
		        );
		    }

		}


    /**
     * {@inheritDoc}
     */
	@Override
	@RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "Update a resource")
	@JsonView(AbstractSystemUuidPersistable.ItemView.class) 
	public T update(/*@ApiParam(name = "id", required = true, value = "string")*/ @PathVariable ID id, @RequestBody T resource) {
		applyCurrentPrincipal(resource);
		// handle partial updates
		if(PartiallyUpdateable.class.isAssignableFrom(resource.getClass())){
			List<String> changedAttributes = ((PartiallyUpdateable) resource ).getChangedAttributes();
			if(CollectionUtils.isNotEmpty(changedAttributes)){
				T persisted = this.service.findById(id);
				this.copyBeanProperties(resource, persisted, changedAttributes);
				resource = persisted;
			}
		}
		return super.update(id, resource);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	@RequestMapping(method = RequestMethod.GET, params="page=no", produces="application/json")
    @ResponseBody
    @ApiOperation(value = "Get the full collection of resources (no paging or criteria)", notes = "Find all resources, and return the full collection (i.e. VS a page of the total results)")
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
	@Override
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Find by id", notes = "Find a resource by it's identifier")
	@JsonView(AbstractSystemUuidPersistable.ItemView.class) 
	public T findById(@ApiParam(name = "id", required = true, value = "string") @PathVariable ID id) {
    	return super.findById(id);
	}
	
	/**
     * Find the set of resources matching the given identifiers.
     *
     * @param id The identifier of the resouce to find
     * @return OK http status code if the request has been correctly processed, with resource found enclosed in the body
     * @throws NotFoundException
     */
	@Override
    @RequestMapping(params = "ids", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Search by ids", notes = "Find the set of resources matching the given identifiers.")
    public Iterable<T> findByIds(@RequestParam(value = "ids[]") Set<ID> ids) {
		return super.findByIds(ids);
	}

	/**
     * Obtain a newly created resource instance.
     * @return OK http status code if the request has been correctly processed, with resource found enclosed in the body
     */
    @RequestMapping(value = "new", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Obtain new transient instance, including UI metadata", notes = "Instantiates and returns a new reszource object")
	public T getSchemaWrapperInstance() {
    	T resource = null;
    	try {
			resource = this.service.getDomainClass().newInstance();
//			
//			// TODO: update to use cases, fields etc. format
//			if(FormSchemaAware.class.isAssignableFrom(resource.getClass())){
//				FormSchema.setToInstance(((FormSchemaAware) resource));
//			}
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
	@Override
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
	@ApiOperation(value = "Delete a resource", notes = "Delete a resource by its identifier. ", httpMethod = "DELETE")
	public void delete(@ApiParam(name = "id", required = true, value = "string") @PathVariable ID id) {
		super.delete(id);
	}

	@Override
    @RequestMapping(method = RequestMethod.DELETE)
	@ApiOperation(value = "Delete all resources")
	public void delete() {
		super.delete();
	}
	
	@RequestMapping(value = "uischema", produces = { "application/json" }, method = RequestMethod.GET)
	@ResponseBody
    @ApiOperation(value = "Get UI schema", notes = "Get the UI achema for the controller entity type, including fields, use-cases etc.")
	public UiSchema getSchema() {
		UiSchema schema = new UiSchema(this.service.getDomainClass());
		return schema;
	}
	
	@RequestMapping(produces = { "application/json" }, method = RequestMethod.OPTIONS)
	@ResponseBody
	@ApiOperation(value = "Get UI schema", notes = "Get the UI achema for the controller entity type, including fields, use-cases etc.")
	public UiSchema getSchemas() {
		return this.getSchema();
	}

	@RequestMapping(value = "{subjectId}/metadata", method = RequestMethod.PUT)
	@ResponseBody
    @ApiOperation(value = "Add metadatum", notes = "Add or updated a resource metadatum")
	public void addMetadatum(@PathVariable ID subjectId,
			@RequestBody MetadatumDTO dto) {
		service.addMetadatum(subjectId, dto);
	}

	@RequestMapping(value = "{subjectId}/metadata/{predicate}", method = RequestMethod.DELETE)
	@ResponseBody
    @ApiOperation(value = "Remove metadatum", notes = "Remove a resource metadatum if it exists")
	public void removeMetadatum(@PathVariable ID subjectId,
			@PathVariable String predicate) {
		service.removeMetadatum(subjectId, predicate);
	}
	
    @RequestMapping(value = "{subjectId}/uploads/{propertyName}", method = RequestMethod.GET)
    @ApiOperation(value = "Get file uploads by property")
    public @ResponseBody List<BinaryFile> getUploadsByProperty(@PathVariable ID subjectId, @PathVariable String propertyName) {
        LOGGER.info("uploadGet called");
        List<BinaryFile> uploads = null;
        // attach file
        Field fileField = GenericSpecifications.getField(this.service.getDomainClass(), propertyName);
        Class clazz = fileField.getType();
        if(BinaryFile.class.isAssignableFrom(clazz)){
        	uploads = this.service.getUploadsForProperty(subjectId, propertyName);
        }
        return uploads;
    }

    @RequestMapping(value = "{subjectId}/uploads/{propertyName}/thumbs/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get file thumb/preview image of a file upload	")
    public void thumbnail(HttpServletResponse response, @PathVariable String subjectId, @PathVariable String propertyName, @PathVariable String id) {
		Configuration config = ConfigurationFactory.getConfiguration();
		String fileUploadDirectory = config.getString(ConfigurationFactory.FILES_DIR);
        BinaryFile file = binaryFileService.findById(id);
        File fileFile = new File(fileUploadDirectory + file.getParentPath() + "/"+file.getThumbnailFilename());
        response.setContentType(file.getContentType());
        response.setContentLength(file.getThumbnailSize().intValue());
        try {
            InputStream is = new FileInputStream(fileFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch(IOException e) {
            LOGGER.error("Could not show thumbnail "+id, e);
        }
    }

    @ApiOperation(value = "Get an uploaded file")
    @RequestMapping(value = "{subjectId}/uploads/{propertyName}/files/{id}", method = RequestMethod.GET)
    public void getFile(HttpServletResponse response, @PathVariable String subjectId, @PathVariable String propertyName, @PathVariable String id) {
		Configuration config = ConfigurationFactory.getConfiguration();
		String fileUploadDirectory = config.getString(ConfigurationFactory.FILES_DIR);
        BinaryFile file = binaryFileService.findById(id);
        File fileFile = new File(fileUploadDirectory + file.getParentPath() + "/"+file.getNewFilename());
        response.setContentType(file.getContentType());
        response.setContentLength(file.getSize().intValue());
        try {
            InputStream is = new FileInputStream(fileFile);
            IOUtils.copy(is, response.getOutputStream());
        } catch(IOException e) {
            LOGGER.error("Could not show picture "+id, e);
        }
    }
    

    

    @ApiOperation(value = "Delete an uploaded file")
    @RequestMapping(value = "{subjectId}/uploads/{propertyName}/{id}", method = RequestMethod.DELETE)
    public @ResponseBody List deleteById(@PathVariable String subjectId, @PathVariable String propertyName, @PathVariable String id) {
		Configuration config = ConfigurationFactory.getConfiguration();
		String fileUploadDirectory = config.getString(ConfigurationFactory.FILES_DIR);
        BinaryFile file = binaryFileService.findById(id);
        File fileFile = new File(fileUploadDirectory+"/"+file.getNewFilename());
        fileFile.delete();
        File thumbnailFile = new File(fileUploadDirectory+"/"+file.getThumbnailFilename());
        thumbnailFile.delete();
        binaryFileService.delete(file);
        List<Map<String, Object>> results = new ArrayList();
        Map<String, Object> success = new HashMap();
        success.put("success", true);
        results.add(success);
        return results;
    }

    @ApiOperation(value = "Add a file uploads to property")
    @RequestMapping(value = "{subjectId}/uploads/{propertyName}", method = {RequestMethod.POST, RequestMethod.PUT}, consumes = {})
    public @ResponseBody BinaryFile  addUploadsToProperty( @PathVariable ID subjectId, @PathVariable String propertyName, MultipartHttpServletRequest request, HttpServletResponse response) {
        LOGGER.info("uploadPost called");

		Configuration config = ConfigurationFactory.getConfiguration();
		String fileUploadDirectory = config.getString(ConfigurationFactory.FILES_DIR);
		String baseUrl = config.getString("calipso.baseurl");
		
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf;
        BinaryFile bf = new BinaryFile();
        try{
        	 if (itr.hasNext()){
	        
	        
	            mpf = request.getFile(itr.next());
	            LOGGER.info("Uploading {}", mpf.getOriginalFilename());
	            
	
	            bf.setName(mpf.getOriginalFilename());
	            bf.setFileNameExtention(mpf.getOriginalFilename().substring(mpf.getOriginalFilename().lastIndexOf(".")+1));
	            
	            bf.setContentType(mpf.getContentType());
	            bf.setSize(mpf.getSize());
	            
	            // request targets specific path?
	            StringBuffer uploadsPath = new StringBuffer('/')
	            	.append(this.service.getDomainClass().getDeclaredField("PATH_FRAGMENT").get(String.class))
	            	.append('/')
	            	.append(subjectId)
	            	.append("/uploads/")
	            	.append(propertyName);
	            bf.setParentPath(uploadsPath.toString());
	            LOGGER.info("Saving image entity with path: " + bf.getParentPath());
	            bf = binaryFileService.create(bf);
	
	            LOGGER.info("file name: {}", bf.getNewFilename());
	            bf = binaryFileService.findById(bf.getId());
	            LOGGER.info("file name: {}", bf.getNewFilename());
	
	            File storageDirectory = new File(fileUploadDirectory + bf.getParentPath());
	
	            if(!storageDirectory.exists()){
	            	storageDirectory.mkdirs();
	            }
	
	            LOGGER.info("storageDirectory: {}", storageDirectory.getAbsolutePath());
	            LOGGER.info("file name: {}", bf.getNewFilename());
	            

                File newFile = new File(storageDirectory, bf.getNewFilename());
                newFile.createNewFile();
                LOGGER.info("newFile path: {}", newFile.getAbsolutePath());
                Files.copy(mpf.getInputStream(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                BufferedImage thumbnail = Scalr.resize(ImageIO.read(newFile), 290);
                File thumbnailFile = new File(storageDirectory, bf.getThumbnailFilename());
                ImageIO.write(thumbnail, "png", thumbnailFile);
                bf.setThumbnailSize(thumbnailFile.length());
                
                bf = binaryFileService.update(bf);
                
                // attach file
                // TODO: add/update to collection
                Field fileField = GenericSpecifications.getField(this.service.getDomainClass(), propertyName);
                Class clazz = fileField.getType();
                if(BinaryFile.class.isAssignableFrom(clazz)){
                	T target = this.service.findById(subjectId);
                	BeanUtils.setProperty(target, propertyName, bf);
                	this.service.update(target);
                }
                
                bf.setUrl(baseUrl+"/api/rest/" + bf.getParentPath() + "/files/" + bf.getId());
                bf.setThumbnailUrl(baseUrl+"/api/rest/" + bf.getParentPath() + "/thumbs/" + bf.getId());
                bf.setDeleteUrl(baseUrl+"/api/rest/" + bf.getParentPath() + "/" + bf.getId());
                bf.setDeleteType("DELETE");
                bf.addInitialPreview("<img src=\"" + bf.getThumbnailUrl() + "\" class=\"file-preview-image\" />");
                
            } 

        }catch(Exception e) {
            LOGGER.error("Could not upload file(s) ", e);
		}

        return bf;
    }
    
}
