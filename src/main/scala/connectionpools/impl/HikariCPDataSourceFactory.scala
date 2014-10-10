package connectionpools.impl

import java.util
import javax.sql.DataSource

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import connectionpools.DataSources._
import connectionpools._

import scala.collection.JavaConverters._

/**
 * HikariCPDataSourceFactory
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
class HikariCPDataSourceFactory extends DataSourceFactorySupport {

  /**
   * HikariCP DataSource를 생성합니다.
   * @param dataSourceClassName dataSourceClassName
   *                            ( 기존 driverClass 가 아닙니다 : mysql용은 com.mysql.jdbc.jdbc2.optional.MysqlDataSource 입니다 )
   * @param url         Database 주소
   * @param username    사용자 명
   * @param passwd      사용자 패스워드
   * @return [[javax.sql.DataSource]] 인스턴스
   */
  override def createDataSource(dataSourceClassName: String = "",
                                driverClass: String = JdbcDrivers.DRIVER_CLASS_H2,
                                url: String = "jdbc:h2:mem:test",
                                username: String = "",
                                passwd: String = "",
                                props: util.Map[String, String] = new util.HashMap(),
                                maxPoolSize: Int = MAX_POOL_SIZE): DataSource = {
    log.info("Hikari DataSource를 빌드합니다... " +
             s"dataSourceClassName=[$dataSourceClassName], driverClass=[$driverClass] url=[$url], username=[$username]")

    val config = new HikariConfig()

    config.setInitializationFailFast(true)
    config.setIdleTimeout(30000)

    // AutoCommit은 Driver 기본 값을 사용하도록 합니다. (mysql 은 auto commit = true)
    //config.setAutoCommit(false)

    if (dataSourceClassName != null && dataSourceClassName.length > 0) {
      config.setDataSourceClassName(dataSourceClassName)
      config.addDataSourceProperty("url", url)
      config.addDataSourceProperty("user", username)
      config.addDataSourceProperty("password", passwd)
    } else {
      config.setDriverClassName(driverClass)
      config.setJdbcUrl(url)
      config.setUsername(username)
      config.setPassword(passwd)
    }

    // MySQL 인 경우 성능을 위해 아래 설정을 사용합니다.
    val isMySQL = JdbcDrivers.DATASOURCE_CLASS_MYSQL.equals(dataSourceClassName) ||
                  JdbcDrivers.DRIVER_CLASS_MYSQL.equals(driverClass)
    if (isMySQL) {
      config.addDataSourceProperty("cachePrepStmts", "true")
      config.addDataSourceProperty("prepStmtCacheSize", "500")
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096")
      config.addDataSourceProperty("useServerPrepStmts", "true")
    }

    config.setConnectionTestQuery("SELECT 1")
    val poolSize = maxPoolSize max MIN_POOL_SIZE
    config.setMaximumPoolSize(poolSize)

    // NOTE: 이게 상당히 중요하다!!!
    // NOTE: 설정하지 않으면 max pool size 와 같게 둬서 connection pool 을 고갈 시킨다. 최소 갯수만 남겨둬야 한다.
    config.setMinimumIdle(processCount min MIN_IDLE_SIZE)

    if (props != null) {
      props.asScala foreach {
        case (name, value) => config.addDataSourceProperty(name, value)
      }
    }


    new HikariDataSource(config)
  }

}
