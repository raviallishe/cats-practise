package recap

object TypeClasses extends App {

  case class Person(name: String, age: Int)
  //type class can be defined in three parts

  //1. define trait with generic type
  trait JsonSerializer[T] {
    def toJson(value: T): String
  }

  //2. create implicit type instance by providing implementation of trait above

  implicit object StringSerializer extends JsonSerializer[String] {
    override def toJson(value: String) = "\"" + value + "\""
  }

  implicit object IntSerializer extends JsonSerializer[Int] {
    override def toJson(value: Int): String = value.toString
  }

  implicit object PersonSerializer extends JsonSerializer[Person] {
    override def toJson(value: Person): String =
      s"""
         |"name" : ${value.name},
         |"age" : ${value.age }
         |""".stripMargin
  }


  //3. offer some API

  def listToJson[T](list: List[T])(implicit serializer: JsonSerializer[T]) = {
    list.map(x => serializer.toJson(x)).mkString("[", ",", "]")
  }

  println(listToJson(List(Person("ravi", 28))))


  //4. can also extend the existing types via extension methods

  object JsonSyntax {
    implicit class JsonSerializable[T](value: T)(implicit serializer: JsonSerializer[T]) {
      def toJson() = serializer.toJson(value)
    }
  }

}
