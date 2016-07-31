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
package gr.abiss.calipso.service.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import gr.abiss.calipso.service.FilePersistenceService;

@Service
public class DummyPersistenceService implements FilePersistenceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(DummyPersistenceService.class);



	/**
	 * {@inheritDoc}}
	 * @see gr.abiss.calipso.service.FilePersistenceService#saveFile(org.springframework.web.multipart.MultipartFile, java.lang.String)
	 */
	public String saveFile(MultipartFile multipartFile, String filename) {
		String url = filename;
		LOGGER.warn("=======================================================================================");
		LOGGER.warn("SAVE: saveFile: " + multipartFile.getOriginalFilename() + ", as: " + filename + ", size: " + multipartFile.getSize() + ", contentType: " + multipartFile.getContentType());
		LOGGER.warn("=======================================================================================");
		return url;
	}

}