/**
 * Copyright (c) 2007 - 2013 www.Abiss.gr
 *
 * This file is part of Calipso, a software platform by www.Abiss.gr.
 *
 * Calipso is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Calipso is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Calipso. If not, see http://www.gnu.org/licenses/agpl.html
 */
package gr.abiss.calipso.service;


import gr.abiss.calipso.model.User;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
			
	@Value("${mail.server.from}")
	private String defaultMailFrom;

	@Value("${calipso.baseurl}")
	private String baseUrl;

    @Autowired 
    private JavaMailSender mailSender;
    
    @Autowired 
    private TemplateEngine templateEngine;


	/**
	 * Send an Account Confirmation email to the given user
	 * @param user
	 * @throws MessagingException
	 */
	public void sendAccountConfirmation(final User user){
		final String subject = "Account Confirmation";
		final String templateName = "email-account-confirmation.html";
		String emailTo = user.getEmail();
		String emailFrom = defaultMailFrom;
		
		// Prepare the evaluation context
		final Context ctx = new Context(new Locale(user.getLocale()));
		ctx.setVariable("user", user);

		sendEmail(subject, templateName, emailTo, emailFrom, ctx);
    }
	
	/**
	 * Send an test email to verify outgoing email settings
	 * @param user the user to send the email to
	 * @throws MessagingException
	 */
	public void sendTest(final User user) {
		final String subject = "[ignore] Email Server Test";
		final String templateName = "email-test.html";
		String emailTo = user.getEmail();
		String emailFrom = defaultMailFrom;
		
		// Prepare the evaluation context
		final Context ctx = new Context(new Locale(user.getLocale()));
		ctx.setVariable("user", user);

		sendEmail(subject, templateName, emailTo, emailFrom, ctx);
    }
	
	/**
	 * Send a password reset email to the given user
	 * @param user
	 * @throws MessagingException
	 */

	public void sendPasswordResetLink(final User user)  {
		final String subject = "Password Reset Request";
		final String templateName = "email-password-reset.html";
		String emailTo = user.getEmail();
		String emailFrom = defaultMailFrom;

		// Prepare the evaluation context
		final Context ctx = new Context(new Locale(user.getLocale()));
		ctx.setVariable("user", user);

		sendEmail(subject, templateName, emailTo, emailFrom, ctx);
	}

	@Async
	public void sendEmail(final String subject, final String templateName, String emailTo, String emailFrom, final Context ctx){
		try {
			// Prepare message using a Spring helper
			final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			message.setSubject(subject);
				message.setFrom(emailFrom);
			message.setTo(emailTo);
			ctx.setVariable("baseUrl", this.baseUrl);
			// Create the HTML body using Thymeleaf
			final String htmlContent = this.templateEngine.process(templateName, ctx);
			message.setText(htmlContent, true /* isHtml */);
	
			// Send email
			this.mailSender.send(mimeMessage);
		} catch (Exception e) {
			LOGGER.error("Failed to send email: ", e);
		}
	}

    
    
    
    /* 
     * Send HTML mail with attachment. 
     */
	@Async
    public void sendMailWithAttachment(
            final String recipientName, final String recipientEmail, final String attachmentFileName, 
            final byte[] attachmentBytes, final String attachmentContentType, final Locale locale) 
            throws MessagingException {
        
        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = 
                new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject("Example HTML email with attachment");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process("email-withattachment.html", ctx);
        message.setText(htmlContent, true /* isHtml */);
        
        // Add the attachment
        final InputStreamSource attachmentSource = new ByteArrayResource(attachmentBytes);
        message.addAttachment(
                attachmentFileName, attachmentSource, attachmentContentType);
        
        // Send mail
        this.mailSender.send(mimeMessage);
        
    }

    
    
    /* 
     * Send HTML mail with inline image
     */
	@Async
    public void sendMailWithInline(
            final String recipientName, final String recipientEmail, final String imageResourceName, 
            final byte[] imageBytes, final String imageContentType, final Locale locale)
            throws MessagingException {
        
        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        ctx.setVariable("imageResourceName", imageResourceName); // so that we can reference it from HTML
        
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = 
                new MimeMessageHelper(mimeMessage, true /* multipart */, "UTF-8");
        message.setSubject("Example HTML email with inline image");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        final String htmlContent = this.templateEngine.process("email-inlineimage.html", ctx);
        message.setText(htmlContent, true /* isHtml */);
        
        // Add the inline image, referenced from the HTML code as "cid:${imageResourceName}"
        final InputStreamSource imageSource = new ByteArrayResource(imageBytes);
        message.addInline(imageResourceName, imageSource, imageContentType);
        
        // Send mail
        this.mailSender.send(mimeMessage);
        
    }


}