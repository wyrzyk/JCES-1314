package jces1209.vu

import com.atlassian.performance.tools.virtualusers.api.browsers.HeadlessChromeBrowser
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.chrome.ChromeOptions

class EagerChromeBrowser  : HeadlessChromeBrowser() {

    override fun configure(options: ChromeOptions, service: ChromeDriverService.Builder) {
        options.setPageLoadStrategy(PageLoadStrategy.EAGER)
        super.configure(options, service)
    }

}
