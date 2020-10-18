package com.isu.ifw.service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.isu.ifw.common.service.TenantConfigManagerService;

@Service
public class MailSenderService {
	private final Logger logger = LoggerFactory.getLogger("ifwFileLog");
	
	@Autowired
	TenantConfigManagerService tcms;
	
	public boolean sendMail(Long tenantId, String to, String from, String subject, String content) {
		
		String mailSendMode = tcms.getConfigValue(tenantId, "MAIL.SEND_MODE", true, "");
		
		if("AWS".equals(mailSendMode)) {
			/*
			try {
				AwsMailSender sender = new AwsMailSender(from, from, to, subject, content, null);
				sender.sendMessage();
				return true;
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			*/
		} else if("SMTP".equals(mailSendMode)) {
		
			MimeMessagePreparator preparator = new MimeMessagePreparator() {
				
				@Override
				public void prepare(MimeMessage mm) throws Exception {
					mm.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
					mm.setFrom(new InternetAddress(from));
					mm.setSubject(subject);
					mm.setContent(content, "text/html;charset=UTF-8");
					//mm.setText(content);
				}
			};
			
			try {
				
				String host = tcms.getConfigValue(tenantId, "MAIL.SMTP.HOST", true, "");
				String username = tcms.getConfigValue(tenantId, "MAIL.SMTP.ID", true, "");
				String password = tcms.getConfigValue(tenantId, "MAIL.SMTP.PW", true, "");
				
				JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
				mailSender.setUsername(username);
				mailSender.setPassword(password);
				mailSender.setHost(host);
				mailSender.setDefaultEncoding("UTF-8");
				mailSender.setJavaMailProperties(getMailProperties());
				mailSender.send(preparator);
				return true;
				
			}catch(MailException me) {
				me.printStackTrace();
				logger.error("MailException", me);
				return false;
			}catch(Exception e) {
				e.printStackTrace();
				return false;
			}
			
		}
		
		return false;
	}
		
	private Properties getMailProperties()
	{
		Properties properties = new Properties();
		//properties.setProperty("mail.transport.protocol", "smtp");
		//properties.setProperty("mail.smtp.starttls.enable", "true");
		//properties.setProperty("mail.smtp.ssl.trust", host);
		//properties.setProperty("mail.smtp.host", host);
		//properties.setProperty("mail.smtp.auth", "true");
		//properties.setProperty("mail.smtp.port", port);
		//properties.setProperty("mail.smtp.socketFactory.port", port);
		//properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		return properties;
	}
}
