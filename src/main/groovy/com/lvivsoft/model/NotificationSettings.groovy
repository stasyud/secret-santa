package com.lvivsoft.model

/**
 * @author: SYudenkov
 * Date: 12/13/2015
 * Time: 5:11 PM
 */
class NotificationSettings {
    String smtpLogin
    String smtpPassword
    String smtpPort
    String smtpAuthEnabled
    String smtpStartTLSEnabled
    String smtpHost
    String fromEmailAddress
    String fromName
    String template
    String testEmail
    String testMailEnabled
    String subject

    String getSmtpStartTLSEnabled() {
        return smtpStartTLSEnabled
    }

    void setSmtpStartTLSEnabled(String smtpStartTLSEnabled) {
        if (smtpStartTLSEnabled != null && "on".equals(smtpStartTLSEnabled)) smtpStartTLSEnabled = "true"
        this.smtpStartTLSEnabled = smtpStartTLSEnabled
    }

    String getSmtpAuthEnabled() {
        return smtpAuthEnabled
    }

    void setSmtpAuthEnabled(String smtpAuthEnabled) {
        if (smtpAuthEnabled != null && "on".equals(smtpAuthEnabled)) smtpAuthEnabled = "true"
        this.smtpAuthEnabled = smtpAuthEnabled
    }

    String getTestMailEnabled() {
        return testMailEnabled
    }

    void setTestMailEnabled(String testMailEnabled) {
        if (testMailEnabled != null && "on".equals(testMailEnabled)) testMailEnabled = "true"
        this.testMailEnabled = testMailEnabled
    }
}
