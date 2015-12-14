package com.lvivsoft.service

import com.lvivsoft.model.NotificationSettings

import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

/**
 * @author: SYudenkov
 * Date: 11/4/2015
 * Time: 2:11 PM
 */
class MailService {
    private ResourceBundle props
    def NotificationSettings mailSettings
    def String template

    static final MailService instance = new MailService();

    /**
     * Initializes Mail service with the default values from the property files
     */
    private def init() {
        props = ResourceBundle.getBundle("email")
        mailSettings = new NotificationSettings()
        mailSettings.setFromEmailAddress(props.getString("mail.from.address"))
        mailSettings.setFromName(props.getString("mail.from.name"))
        mailSettings.setSubject(props.getString("mail.subject"))

        mailSettings.setSmtpAuthEnabled(props.getString("mail.smtp.auth"))
        mailSettings.setSmtpHost(props.getString("mail.smtp.host"))
        mailSettings.setSmtpLogin(props.getString("mail.login"))
        mailSettings.setSmtpPassword(props.getString("mail.password"))
        mailSettings.setSmtpPort(props.getString("mail.smtp.port"))
        mailSettings.setSmtpStartTLSEnabled(props.getString("mail.smtp.starttls.enable"))

        template = this.getClass().getClassLoader().getResourceAsStream("subject.txt").getText("UTF-8")
    }

    static MailService getInstance() {
        return instance
    }

    private MailService() {
        init()
    }

    /**
     * Method that is used to notify given recipient with the provided message
     * @param body - body of email to send
     * @param recipient - email address to notify
     */
    def sendMessage(String body, String recipient) {
        Properties mailServerProperties = new Properties()
        mailServerProperties.put("mail.smtp.port", mailSettings.smtpPort)
        mailServerProperties.put("mail.smtp.auth", mailSettings.smtpAuthEnabled)
        mailServerProperties.put("mail.smtp.starttls.enable", mailSettings.smtpStartTLSEnabled)

        Session session = Session.getDefaultInstance(mailServerProperties, null)
        session.setDebug(true)
        MimeMessage message = new MimeMessage(session)
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient))
        message.setFrom(new InternetAddress(mailSettings.fromEmailAddress, mailSettings.fromName))
        message.setSubject(mailSettings.subject, "UTF-8")
        message.setContent(body, "text/html; charset=UTF-8")

        Transport transport = session.getTransport("smtp")

        transport.connect(mailSettings.smtpHost, Integer.parseInt(mailSettings.smtpPort),
                mailSettings.smtpLogin, mailSettings.smtpPassword)
        transport.sendMessage(message, message.getAllRecipients())
        transport.close()
    }


}
