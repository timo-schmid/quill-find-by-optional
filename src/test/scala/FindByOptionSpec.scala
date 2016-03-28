
import org.specs2.mutable.Specification

object FindByOptionSpec extends Specification {

  val db                      = FindByOption.db
  val find                    = FindByOption.findById _
  val create                  = FindByOption.create _
  val findByOption            = FindByOption.findByOption _
  val findByOptionWorkaround  = FindByOption.findByOptionWorkaround _

  step {
    db.execute(
      """ CREATE TABLE IF NOT EXISTS test_data (
        |   id INT AUTO_INCREMENT,
        |   value INT
        |)
        |""".stripMargin,
      None
    )
    if(find(1).isEmpty) create(TestData(1, Some(1)))
    if(find(2).isEmpty) create(TestData(2, Some(2)))
    if(find(3).isEmpty) create(TestData(3, Some(3)))
    if(find(4).isEmpty) create(TestData(4, Some(4)))
    if(find(5).isEmpty) create(TestData(5, None))
    if(find(6).isEmpty) create(TestData(6, None))
  }

  "A quote" should {

    "be able to find by Some" in {
      val result = findByOption(Some(1))
      result.size must equalTo(1)
    }

    "be able to find by None" in {
      val result = findByOption(None) // returns Seq() instead of Seq(TestData(5, None), TestData(6, None))
      result.size must equalTo(2)
    }

    "be able to find by None using workaround" in {
      val result = findByOptionWorkaround(None)
      result.size must equalTo(2)
    }

  }

}

