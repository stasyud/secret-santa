package com.lvivsoft

import com.lvivsoft.model.IndexContainer
import com.lvivsoft.model.NotificationSettings
import com.lvivsoft.service.MailService
import com.lvivsoft.service.SecretSanta
import org.apache.commons.beanutils.BeanUtils
import org.eclipse.jetty.util.MultiMap
import org.eclipse.jetty.util.UrlEncoded
import spark.ModelAndView
import spark.template.freemarker.FreeMarkerEngine
import spark.utils.StringUtils

import static spark.Spark.*

/**
 * @author: SYudenkov
 * Date: 11/4/2015
 * Time: 11:27 AM
 */
class Application {
    public static void main(String[] args) {
        def mailService = MailService.getInstance()
        def santaService = new SecretSanta()

        staticFileLocation '/public'
        port 8090

        get "/", { request, response ->
            return new ModelAndView([container: new IndexContainer()], "index.html")
        }, new FreeMarkerEngine()


        get "/settings", { request, response ->
            return new ModelAndView([container: mailService.mailSettings, template: mailService.template], "settings.html")
        }, new FreeMarkerEngine()

        post "/settings/mail", { req, res ->
            Map map = [:]
            NotificationSettings mailSettings = new NotificationSettings()
            try {
                MultiMap<String> params = new MultiMap<String>()
                UrlEncoded.decodeTo(req.body(), params, "UTF-8")
                BeanUtils.populate(mailSettings, params)
                mailService.setMailSettings(mailSettings)
                map.put("success", true)
            } catch (Exception e) {
                map.put("error", "Error while saving mail settings")
            }

            map.put("container", mailSettings)
            map.put("template", mailService.template)
            return new ModelAndView(map, "settings.html")
        }, new FreeMarkerEngine()


        post "/settings/template", { req, res ->
            Map map = [:]
            NotificationSettings mailSettings = new NotificationSettings()
            try {
                MultiMap<String> params = new MultiMap<String>()
                UrlEncoded.decodeTo(req.body(), params, "UTF-8")
                BeanUtils.populate(mailSettings, params)
                mailService.setTemplate(mailSettings.template)
                if ("true".equals(mailSettings.testMailEnabled) && !StringUtils.isEmpty(mailSettings.testEmail)) {
                    mailService.sendMessage(
                            mailSettings.template.replaceAll("USERNAME", "John Doe").replaceAll("USEREMAIL", "john@doe"),
                            mailSettings.testEmail)
                }
                map.put("success", true)
            } catch (Exception e) {
                String error = "Error while saving template settings: " + e.getMessage()
                map.put("error", error.replaceAll("('|\"|\\n|\\r)", ""))
            }

            map.put("container", mailService.mailSettings)
            map.put("template", mailService.template)
            return new ModelAndView(map, "settings.html")
        }, new FreeMarkerEngine()

        post "/process", { req, res ->
            Map map = [:]
            IndexContainer container = new IndexContainer()
            List<String> results
            try {
                MultiMap<String> params = new MultiMap<String>()
                UrlEncoded.decodeTo(req.body(), params, "UTF-8")
                BeanUtils.populate(container, params)
                results = santaService.processRecipients(container.recipients, "true".equals(container.showResultsEnabled))
            } catch (Exception e) {
                map.put("container", container)
                return processErrorMessage(e, map, "index.html", "Error while processing recipients: ")
            }
            map.put("results", results)
            return new ModelAndView(map, "load.html")
        }, new FreeMarkerEngine()

        post "/notify", { req, res ->
            Map map = [:]
            List<String> results
            try {
                results = santaService.notifyRecipients()
            } catch (Exception e) {
                return processErrorMessage(e, map, "index.html", "Error while notifying recipients: ")
            }

            map.put("success", "Notification process successfully finished")
            map.put("results", results)
            return new ModelAndView(map, "results.html")
        }, new FreeMarkerEngine()
    }

    private static ModelAndView processErrorMessage(Exception e, Map map, String page, String language) {
        String error = language + e.getMessage()
        map.put("error", error.replaceAll("('|\"|\\n|\\r)", ""))
        return new ModelAndView(map, page)
    }
}
