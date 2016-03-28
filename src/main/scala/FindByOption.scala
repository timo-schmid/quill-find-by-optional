import io.getquill._
import io.getquill.naming.SnakeCase
import io.getquill.sources.sql.idiom.H2Dialect
import com.typesafe.config.ConfigFactory

case class TestData(id: Int, value: Option[Int])

object FindByOption {

  lazy val db = source(new JdbcSourceConfig[H2Dialect, SnakeCase]("db") {
    override val config =
      ConfigFactory
        .parseString(
          """dataSourceClassName  = "org.h2.jdbcx.JdbcDataSource"
            |dataSource.url       = "jdbc:h2:mem:db"
          """.stripMargin
        )
  })

  val qCreate = quote {
    query[TestData]
      .insert
  }

  val qFindById = quote { (id: Int) =>
    query[TestData]
      .filter(_.id == id)
  }

  val qFindByOption = quote { (o: Option[Int]) =>
    query[TestData]
      .filter(_.value == o)
  }

  val qFindByNone = quote {
    query[TestData]
      .filter(_.value.isEmpty)
    // possible bug: camel case not converted to snake case (rename the property to optionValue to see this):
    // SELECT x3.id, x3.option_value FROM test_data x3 WHERE x3.optionValue IS NULL
  }

  def create(testData: TestData): Unit =
    db.run(qCreate)(testData)

  def findById(id: Int): Option[TestData] =
    db.run(qFindById)(id).headOption

  def findByOption(o: Option[Int]): Seq[TestData] =
    db.run(qFindByOption)(o)
    // possible bug: NULL check using = does not work in h2 / mysql:
    // SELECT x2.id, x2.value FROM test_data x2 WHERE x2.value = ?

  def findByOptionWorkaround(o: Option[Int]): Seq[TestData] =
    if(o.isDefined)
      db.run(qFindByOption)(o)
    else
      db.run(qFindByNone)

}

