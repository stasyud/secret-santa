package com.lvivsoft.service


import static com.xlson.groovycsv.CsvParser.parseCsv

/**
 * @author: SYudenkov
 * Date: 11/4/2015
 * Time: 10:41 AM
 */
class SecretSanta {

    def users = [:]
    def userList = []
    def assignedPairs = [:] //email of first user + its recipient

    /**
     * Processes input list of recipients
     * @param recipientsCSV - list of users provided by the user
     * @param isShowResults - flag that determines whether to return information about result recipient pairs
     * @return notes/logs gathered during processing
     */
    def processRecipients(String recipientsCSV, boolean isShowResults) {
        def results = []

        recipientsCSV = "email,name\r\n" + recipientsCSV
        def data = parseCsv(recipientsCSV)

        for (line in data) {
            if (line.values.size() == 1)
                users.put(line.values[0], new Expando(name: "", email: line.values[0]))
            else
                users.put(line.email, new Expando(name: line.name, email: line.email))
            userList.add(line.email)
        }
        results.add("Processed: ${users.size()} users\n")

        if (users.size() % 2 != 0) {
            // case there is odd quantity of users, need to delete last one to avoid frequent stack overflow exceptions
            String keyToDelete = userList.get(userList.size() - 1)
            users.remove(keyToDelete)
            userList.dropRight(1)
            results.add("Removed user: ${keyToDelete} as there is odd number of users\n")
        }

        users.each { key, value ->
            def user = getRandomUser(key)
            assignedPairs.put(key, user)
            userList.remove(user.email)
            if (isShowResults) results.add("$key - $user.email \n")
        }
        return results
    }

    /**
     * Notifies all the available recipients (should be run only after user processing)
     * @return notes/logs gathered during notification process
     */
    def notifyRecipients() {
        def results = []
        int counter = 0
        if (assignedPairs.size() == users.size()) {
            MailService service = MailService.getInstance()

            assignedPairs.each { key, value ->
                try {
                    service.sendMessage(
                            service.template.replaceAll("USERNAME", value.name).replaceAll("USEREMAIL", value.email),
                            key)
                    counter++
                } catch (Exception e) {
                    results.add("Exception for ${key}; " + e.getMessage())
                }
            }
        } else {
            results.add("Collapse, no recipients to deliver")
        }
        results.add("Delivered: $counter emails")
        return results
    }
    /**
     * Generates random number within the input range
     * @param max - max integer to be generated in the range from zero
     */
    static def int getRandom(int max) {
        Random rand = new Random()
        rand.nextInt(max)
    }

    /**
     * Returns random user (different from the given one) from the list of recipients
     * @param currentEmail - email address of current user
     */
    def getRandomUser(currentEmail) {
        int size = userList.size()
        int random = getRandom(size)
        def email = userList.get(random)
        if (currentEmail == email) {
            println("Ops! chosen the same email address, trying one more time")
            return getRandomUser(currentEmail)
        }
        return users[email]
    }

}