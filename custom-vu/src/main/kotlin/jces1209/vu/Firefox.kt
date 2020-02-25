package jces1209.vu

import com.atlassian.performance.tools.virtualusers.api.browsers.Browser
import com.atlassian.performance.tools.virtualusers.api.browsers.CloseableRemoteWebDriver
import org.openqa.selenium.Dimension
import org.openqa.selenium.firefox.FirefoxDriver

class Firefox : Browser {
    override fun start(): CloseableRemoteWebDriver {
        val driver = FirefoxDriver()
        driver.manage().window().size = Dimension(1920, 1080)
        return CloseableRemoteWebDriver(driver)
    }
}
