package com.getxinfo.message;

import java.io.UnsupportedEncodingException;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class EmailService implements MessageService {
    private final Log logger = LogFactory.getLog(getClass());

    private JavaMailSender mailSender;
    private final String loginUrl;
    private final String fromAddress;

    @Autowired
    public EmailService(JavaMailSender mailSender, @Value("{login.url}") String loginUrl, @Value("{smtp.from_address}") String fromAddress) {
        this.mailSender = mailSender;
        this.loginUrl = loginUrl;

        // if we are provided a from address use that, if not fallback to default based on loginUrl
        if (fromAddress != null && !fromAddress.isEmpty()) {
            this.fromAddress = fromAddress;
        } else {
            String host = UriComponentsBuilder.fromHttpUrl(loginUrl).build().getHost();
            this.fromAddress = "admin@" + host;
        }

    }

    public String getFromAddress() {
        return fromAddress;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private Address[] getSenderAddresses() throws AddressException, UnsupportedEncodingException {
        String name = null;

        return new Address[]{new InternetAddress(fromAddress, name)};
    }

    @Override
    public void sendMessage(String email, MessageType messageType, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.addFrom(getSenderAddresses());
            message.addRecipients(Message.RecipientType.TO, email);
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html");
        } catch (MessagingException e) {
            logger.error("Exception raised while sending message to " + email, e);
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception raised while sending message to " + email, e);
        }

        mailSender.send(message);
    }
}
