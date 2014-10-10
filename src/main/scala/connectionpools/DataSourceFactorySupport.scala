package connectionpools

import java.util
import javax.sql.DataSource

import org.slf4j.LoggerFactory

/**
 * DataSourceFactorySupport
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
trait DataSourceFactorySupport {

  protected lazy val log = LoggerFactory.getLogger(getClass)

  /**
   * DataSource를 생성합니다.
   * @param dataSourceClassName dataSourceClassName
   *                            ( 기존 driverClass 가 아닙니다 : mysql용은 com.mysql.jdbc.jdbc2.optional.MysqlDataSource 입니다 )
   * @param url         Database 주소
   * @param username    사용자 명
   * @param passwd      사용자 패스워드
   * @return [[javax.sql.DataSource]] 인스턴스
   */
  def createDataSource(dataSourceClassName: String = "",
                       driverClass: String = JdbcDrivers.DRIVER_CLASS_H2,
                       url: String = "jdbc:h2:mem:test",
                       username: String = "",
                       passwd: String = "",
                       props: util.Map[String, String] = new util.HashMap(),
                       maxPoolSize: Int = DataSources.MAX_POOL_SIZE): DataSource
}
