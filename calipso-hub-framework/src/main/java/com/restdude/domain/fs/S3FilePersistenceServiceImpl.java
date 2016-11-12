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
package com.restdude.domain.fs;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.InputStream;

/**
 * An implementation of {@link FilePersistenceService} that uses Amazon S3 for
 * file storage. Configuration properties from dev.properties:
 * 
 * build.aws_access_key_id=
 * build.aws_secret_access_key=
 * build.aws_namecard_bucket=:
 * 
 * from bean config: awsAccessKey, awsSecretAccessKey, nameCardBucket
 * 
 */
public class S3FilePersistenceServiceImpl implements FilePersistenceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(S3FilePersistenceServiceImpl.class);

	@Value("${aws_namecard_bucket}")
	private String nameCardBucket;

	@Value("${aws_access_key_id}")
	private String awsAccessKey;

	@Value("${aws_secret_access_key}")
	private String awsSecretAccessKey;

	private AmazonS3Client s3Client;

	@PostConstruct
	public void postConstruct() {
		// create S3 credentials
		BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretAccessKey);
		// setup client
		this.s3Client = new AmazonS3Client(credentials);
		LOGGER.debug("Created S3 client");
	}


	/**
	 * Save file in S3
	 * @see FilePersistenceService#saveFile(java.io.InputStream, long, java.lang.String, java.lang.String)
	 */
	@Override
	public String saveFile(InputStream in, long contentLength, String contentType, String path) {
		String url;
		// create metadata
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(contentLength);
		meta.setContentType(contentType);

		// save to bucket
		s3Client.putObject(new PutObjectRequest(nameCardBucket, path, in, meta)
				.withCannedAcl(CannedAccessControlList.PublicRead));
		// set the URL to return
		url = s3Client.getUrl(nameCardBucket, path).toString();
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("File saved, url: " + url + ", path: " + path + ", size: " + contentLength + ", contentType: " + contentType);
		}
		return url;
	}

	/**
	 * Delete files from S3
	 *
	 * @see FilePersistenceService#deleteFiles(String...)
	 */
	@Override
	public void deleteFiles(String... paths) {
		// delete from bucket
		s3Client.deleteObjects(new DeleteObjectsRequest(nameCardBucket).withKeys(paths));
	}

}