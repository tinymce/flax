package com.ephox.flax
package api.elem

import org.openqa.selenium
import selenium.WebDriver
import selenium.chrome.ChromeDriver
import selenium.edge.EdgeDriver
import selenium.firefox.FirefoxDriver
import selenium.ie.InternetExplorerDriver
import selenium.phantomjs.PhantomJSDriver
import selenium.safari.SafariDriver

/**
  * Wrapper around [[WebDriver]].
  * Doesn't expose any methods - use [[Driver]] and [[com.ephox.flax.api.action.SeleniumActions]] for these.
  */
final case class Driver private (private[flax] val d: WebDriver)

object Driver {
  def driver(d: WebDriver): Driver = new Driver(d)

  def driverForBrowser(b: Browser): Driver = driver {
    b match {
      case IE        => new InternetExplorerDriver()
      case Firefox   => new FirefoxDriver()
      case Chrome    => new ChromeDriver()
      case Edge      => new EdgeDriver()
      case Safari    => new SafariDriver()
      case PhantomJs => new PhantomJSDriver()
    }
  }
}
