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


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Persistable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import gr.abiss.calipso.fs.FilePersistence;
import gr.abiss.calipso.fs.FilePersistenceService;
import gr.abiss.calipso.tiers.service.ModelService;
import gr.abiss.calipso.tiers.specifications.GenericSpecifications;
import io.swagger.annotations.ApiOperation;

/**
 * Adds file uploading capabilities to ModelControllers.
 * To use, simply add <code> implements IFilesModelController<MyEntity, MyId, MyService></code>
 * to your controller, inject a {@link gr.abiss.calipso.fs.FilePersistenceService} and make it accessible 
 * by implementing {@link #getFilePersistenceService()}. No other coding is needed.
 */
public interface IFilesModelController<T extends Persistable<ID>, ID extends Serializable, S extends ModelService<T, ID>> 
 extends ModelController<T, ID, S>{

	public FilePersistenceService getFilePersistenceService();
	
	@ApiOperation(value = "Update files", 
			notes = "The files are saved using the parameter names of the multipart files contained in this request. "
					+ "These are the field names of the form (like with normal parameters), not the original file names.")
	@RequestMapping(value = "{id}/files", 
		method = { RequestMethod.POST}, 
		headers=("content-type=multipart/*"), 
		produces = { "application/json", "application/xml" })
	public default @ResponseBody T updateFiles(@PathVariable ID id,
			MultipartHttpServletRequest request, HttpServletResponse response) {
		Logger logger = LoggerFactory.getLogger(IFilesModelController.class);

		T entity = this.getService().findById(id);
		try {
			String basePath = new StringBuffer(this.getService().getDomainClass().getSimpleName())
					.append('/').append(id).append('/').toString();
			String propertyName;
			for (Iterator<String> iterator = request.getFileNames(); iterator.hasNext();) {
				// get the property name
				propertyName = iterator.next();

				// verify the property exists
				Field fileField = GenericSpecifications.getField(this.getService().getDomainClass(), propertyName);
				if (fileField == null || !fileField.isAnnotationPresent(FilePersistence.class)) {
					throw new IllegalArgumentException("No FilePersistence annotation found for member: " + propertyName);
				}

				// store the file and update the property URL
				String url = this.getFilePersistenceService().saveFile(fileField, request.getFile(propertyName), basePath + propertyName);
				BeanUtils.setProperty(entity, propertyName, url);

			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to update files", e);
		}
		// return the updated entity
		return this.getService().update(entity);
	}
}