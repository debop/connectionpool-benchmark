package benchmark

import javax.sql.DataSource

import connectionpools.{DataSources, JdbcDrivers}
import org.scalameter.{Gen, PerformanceTest}
import org.scalatest.Matchers

/**
 * MySQLBenchmark
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
object MySQLBenchmark extends PerformanceTest.Quickbenchmark with Matchers {

  val sizes = Gen.range("size")(5000, 20000, 5000)

  val DriverClass = JdbcDrivers.DRIVER_CLASS_MYSQL
  val JdbcUrl = "jdbc:mysql://localhost/test"
  val Username = "root"
  val Password = "root"

  val hikariDs = DataSources.hikariFactory.createDataSource(driverClass = DriverClass,
                                                             url = JdbcUrl,
                                                             username = Username,
                                                             passwd = Password)
  val boneDs = DataSources.bonecpFactory.createDataSource(driverClass = DriverClass,
                                                           url = JdbcUrl,
                                                           username = Username,
                                                           passwd = Password)
  val tomcatDs = DataSources.tomcatFactory.createDataSource(driverClass = DriverClass,
                                                             url = JdbcUrl,
                                                             username = Username,
                                                             passwd = Password)

  performance of "cycle connection" in {

    measure method "hikariCP" in {
      using(sizes) in { size =>
        cycleConnection(hikariDs, size)
      }
    }

    measure method "boneCP" in {
      using(sizes) in { size =>
        cycleConnection(boneDs, size)
      }
    }

    measure method "tomcatCP" in {
      using(sizes) in { size =>
        cycleConnection(tomcatDs, size)
      }
    }
  }

  performance of "cycle statement" in {

    measure method "hikariCP" in {
      using(sizes) in { size =>
        cycleStatement(hikariDs, size)
      }
    }

    measure method "boneCP" in {
      using(sizes) in { size =>
        cycleStatement(boneDs, size)
      }
    }

    measure method "tomcatCP" in {
      using(sizes) in { size =>
        cycleStatement(tomcatDs, size)
      }
    }
  }

  private def cycleConnection(ds: DataSource, size: Int): Unit = {

    (0 until size).par.foreach { i =>
      val connection = ds.getConnection
      connection should not be null
      connection.close()
    }
  }

  private def cycleStatement(ds: DataSource, size: Int): Unit = {
    (0 until size).par.foreach { i =>
      val connection = ds.getConnection
      val statement = connection.createStatement()
      statement.execute("select 1")
      statement.close()
      connection.close()
    }
  }
}