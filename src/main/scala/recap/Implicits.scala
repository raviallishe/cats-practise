package recap

object Implicits extends App {

  //1. implicit classes
  case class Person(name: String) {
    def greet() = s"my name is $name"
  }

  implicit class ImpersonableString(name: String) {
    def greet() = Person(name).greet()
  }

  val greeting = "Ravi".greet()   // new ImpersonableStrin("Ravi").greet()

  //importing implicits conversion in scope
  import scala.concurrent.duration._

  val oneSec: FiniteDuration = 1.second



  //2. implicit arguments and values -----------------------------------------------------------------------

  def increment(x: Int)(implicit amount: Int) = x + amount

  implicit val amount = 11

  increment(9)    // increment(9)(11) ... implicit here can be used as default value

  // more complex example for implicit arguments and values

  trait JSONSerializer[T] {
    def toJson(value: T): String
  }

  def listToJson[T](list: List[T])(implicit serializer: JSONSerializer[T]) = {
    list.map(person => serializer.toJson(person)).mkString(", ")
  }

  val list = List(Person("ravi"), Person("raj"))
  implicit val JSONSerializer = new JSONSerializer[Person] {
    override def toJson(person: Person): String =
      s"""
         |{"name": ${person.name}}
         |""".stripMargin
  }

  val personJsonList = listToJson(list)              //listToJson[Person](list)(JSONSerializer)
  // implicit argument is used to prove the existence of type



  //3. Implicits methods --------------------------------------------------------------------------------------------

  implicit def oneArgCaseClassSerializer[T <: Product] :JSONSerializer[T] = new JSONSerializer[T] {    //generic implementation for T as any case class
    override def toJson(value: T): String =
      s"""
         |${value.productElementName(0)} : ${value.productElement(0)}    //field name and value of case class
         |""".stripMargin
  }

  case class Cat(name: String)
  case class Dog(dogName: String)

  val catJsonList = listToJson(List(Cat("tom")))      // listToJson(List(Cat("tom"))(oneArgCaseClassSerializer[Cat]) is called in background
  //implicit method is used to prove the existence of type
  // for example implicit method is used in wen applying sorted method on int list it takes implicit sorting method from Int companion object
}
