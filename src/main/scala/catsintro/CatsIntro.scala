package catsintro

object CatsIntro extends App {

  //Eq
  val comparison = 4 == "ravi"           // will compile with warnings but why in the first place if type is not same

  //solution
  //1. import type class
  import cats.Eq

  //2. import TypeClass instances for above int
  import cats.instances.int._      // imports int type instances for all type class includin Eq

  //3. make use of Eq API
  val inEquality = Eq[Int]

  val aTypeSafeComp = inEquality.eqv(2,4)
  //val invalid = inEquality.eqv(2, "ravi")                    //does not compile


  //4. use extension methods if applicable by importing
  import cats.syntax.eq._         // syntax package has extension methods of all type classes

  val typeSafeComp = 2 === 4 // false
  val typeSafeNotEqComp = 2 =!= 4 //true
  //val invalidTypeSafeComp = 2 === "ravi"       // does not compile

  // will only be able to access to the types instance that are accessible
  //5. use extension methods for composite types. e.g - List

  //val listTypeSafeComp = List(2) === List(3)  --- will not compile as List instance type class is not imported
  import cats.instances.list._              // we bring Eq[List[Int]]] in scope
  val listTypeSafeComp = List(2) === List(3)


  //6. create a type class instance for custom type
  case class ToyCar(name: String, price: Double)        //never use a double for price (use BigDecimal instead)

  implicit val toyCarComp = Eq.instance[ToyCar]{(car1, car2) => car1.price == car2.price}   //cats provides instance api to create type class Eq[ToyCar]

  ToyCar("tesla", 29.99) === ToyCar("Lamborini", 29.9)    /// === is present under defined above implicit Eq[ToyCar]
}
