import io.getquill._
import io.getquill.naming.SnakeCase
import io.getquill.sources.sql.idiom.H2Dialect
import com.typesafe.config.ConfigFactory

case class TestData(id: Int, optionValue: Option[Int])

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
      .filter(t => if(o.isDefined) t.optionValue == o else t.optionValue.isEmpty)
  }

  def create(testData: TestData): Unit =
    db.run(qCreate)(testData)

  def findById(id: Int): Option[TestData] =
    db.run(qFindById)(id).headOption

  def findByOption(o: Option[Int]): Seq[TestData] =
    db.run(qFindByOption)(o)

}

