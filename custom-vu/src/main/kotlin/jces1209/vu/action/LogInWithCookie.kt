package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.LOG_IN
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import jces1209.vu.page.JiraCloudWelcome
import org.openqa.selenium.Cookie
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Properties

/**
 * Logs in via Cloud Session cookie. Reads from `cloud-session.properties` classpath resource.
 */
class LogInWithCookie(
    private val jira: WebJira,
    private val meter: ActionMeter
) : Action {

    override fun run() {
        meter.measure(LOG_IN) {
            logIn()
        }
    }

    private fun logIn() {
        val cloudSession = parseCookie()
        previsitCookieDomain(cloudSession.domain)
        jira.driver.manage().addCookie(cloudSession)
        jira.navigateTo("")
        JiraCloudWelcome(jira.driver).skipToJira()
    }

    private fun parseCookie(): Cookie {
        val resourceName = "cloud-session.properties"
        val cookieStream = this::class.java.getResourceAsStream("/$resourceName")
            ?: throw Exception("Copy cloud-session-template.properties as $resourceName and fill in the values")
        val props = Properties()
        cookieStream.use { props.load(it) }
        val cookieValue = props.requireProperty("cookie.value")
        val cookieExpiry = props
            .requireProperty("cookie.expiry")
            .let { ZonedDateTime.parse(it, DateTimeFormatter.RFC_1123_DATE_TIME) }
            .let { Date.from(it.toInstant()) }
        return Cookie("cloud.session.token", cookieValue, ".atlassian.net", "/", cookieExpiry, false, true)
    }

    private fun Properties.requireProperty(key: String): String {
        return getProperty(key) ?: throw Exception("Property $key is missing")
    }

    /**
     * You can only set cookies for a domain when you're already on a matching page.
     * Otherwise you'll get a [org.openqa.selenium.InvalidCookieDomainException].
     */
    private fun previsitCookieDomain(cookieDomain: String) {
        val previsit = when (cookieDomain) {
            ".atlassian.net" -> "https://ecosystem.atlassian.net/"
            else -> throw Exception("Don't know a previsit for $cookieDomain")
        }
        jira.driver.navigate().to(previsit)
    }
}
