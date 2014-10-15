package connectionpools

import connectionpools.impl.{BoneCPDataSourceFactory, HikariCPDataSourceFactory, TomcatCPDataSourceFactory}
import org.slf4j.LoggerFactory

/**
 * DataSources
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
object DataSources {

  private lazy val log = LoggerFactory.getLogger(getClass)

  lazy val processCount = sys.runtime.availableProcessors()
  val MAX_POOL_SIZE = processCount * 8
  val MIN_POOL_SIZE = processCount
  val MIN_IDLE_SIZE = processCount

  lazy val hikariFactory = new HikariCPDataSourceFactory()
  lazy val bonecpFactory = new BoneCPDataSourceFactory()
  lazy val tomcatFactory = new TomcatCPDataSourceFactory()


}
