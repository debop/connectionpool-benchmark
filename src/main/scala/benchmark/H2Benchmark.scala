package benchmark

import javax.sql.DataSource

import connectionpools.DataSources
import org.scalameter.{Gen, PerformanceTest}
import org.scalatest.Matchers

/**
 * H2Benchmark
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
object H2Benchmark extends PerformanceTest.Microbenchmark with Matchers {

  val sizes = Gen.range("size")(10000, 40000, 10000)

  lazy val hikariDs = DataSources.hikariFactory.createDataSource()
  lazy val boneDs = DataSources.bonecpFactory.createDataSource()
  lazy val tomcatDs = DataSources.tomcatFactory.createDataSource()

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
