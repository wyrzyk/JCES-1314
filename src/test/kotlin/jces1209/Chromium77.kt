package jces1209

import com.atlassian.performance.tools.infrastructure.api.browser.Browser
import com.atlassian.performance.tools.infrastructure.api.browser.chromium.CustomChromium
import java.net.URI

class Chromium77 : Browser  by CustomChromium(
    chromiumUri = URI("https://www.googleapis.com/download/storage/v1/b/chromium-browser-snapshots/o/Linux_x64%2F681090%2Fchrome-linux.zip?generation=1564102126574765&alt=media"),
    chromedriverUri = URI("https://chromedriver.storage.googleapis.com/77.0.3865.10/chromedriver_linux64.zip")
)
