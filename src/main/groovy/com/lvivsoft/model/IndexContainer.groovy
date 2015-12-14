package com.lvivsoft.model

/**
 * @author: SYudenkov
 * Date: 12/13/2015
 * Time: 7:09 PM
 */
class IndexContainer {
    String recipients = ""
    String showResultsEnabled ="false"

    String getShowResultsEnabled() {
        return showResultsEnabled
    }

    void setShowResultsEnabled(String showResultsEnabled) {
        if (showResultsEnabled != null && "on".equals(showResultsEnabled)) showResultsEnabled = "true"
        this.showResultsEnabled = showResultsEnabled
    }
}
