package internetcrawler.config

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*

final case class AppConfig(crawler: CrawlerConfig, emberConfig: EmberConfig)derives ConfigReader