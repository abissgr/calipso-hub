package gr.abiss.calipso.controller.cms;

import gr.abiss.calipso.model.cms.BinaryFile;
import gr.abiss.calipso.service.cms.BinaryFileService;
import gr.abiss.calipso.utils.ConfigurationFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 *
 * @author jdmr
 */
@Controller
@RequestMapping(value = "/api/rest/binaryFiles")
public class UploadsController  {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadsController.class);
	private BinaryFileService service;

//	@Override
	@Inject
	@Qualifier("binaryFileService") // somehow required for CDI to work on 64bit JDK?
	public void setService(BinaryFileService service) {
		this.service = service;
	}
	
//
//    @RequestMapping
//    public String index() {
//        LOGGER.info("BinaryFileController home");
//        return "file/index";
//    }
    

    
    
}
