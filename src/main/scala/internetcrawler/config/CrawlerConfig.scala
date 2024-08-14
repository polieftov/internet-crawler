package internetcrawler.config

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*

case class CrawlerConfig(searchTag: String)derives ConfigReader
