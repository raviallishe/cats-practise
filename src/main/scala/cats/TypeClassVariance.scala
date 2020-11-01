package cats

object TypeClassVariance extends App {

  import cats.Eq
  import cats.instances.int._     // Eq[Int] TC instance
  import cats.instances.option._  // Eq[Option[Int]] TC instance
  import cats.syntax.eq._

  val aComparision = Option(2) === Option(2)
  // val anInvalidComparision = Some(2) === None        // Eq[Some[Int]] not found ... this is related to variance

  //Variance
  class Animal
  class Cat extends Animal

  //Covariant type: subtyping is propagated to generic type
  class Cage[+T]
  val cage: Cage[Animal] = new Cage[Cat]     // Cat <: (subtype) Animal, so Cage[Cat] <: Cage[Animal]

  //ContraVariant type: subtyping is propagated backwards to generic type
  class Vet[-T]
  val vet: Vet[Cat] = new Vet[Animal]     // Cat <: (subtype) Animal, so Vet[Animal] <: Vet[Cat]

  //Thumb rule: "Has a T"= Covariant, "Acts on T" = contravariant
  // variance affect how TC instances are fetched

//contravariant
  trait SoundMaker[-T]
  implicit object AnimalSoundMaker extends SoundMaker[Animal]
  def makeSound[T](implicit soundMaker: SoundMaker[T]) = println("meow")
  makeSound[Animal]     // ok - TC instance defined above
  makeSound[Cat]        // ok - TC instance for animal is also applicable for cat
  // rule 1: contravariant type can use super class TC instance if nothing is available strictly for that instance

  // has implications for subtypes
  implicit object OptionSoundMaker extends SoundMaker[Option[Int]]
  makeSound[Option[Int]]
  makeSound[Some[2]]


  // covariant TC

  trait AnimalShow[+T] {
    def show: String
  }

  implicit object GeneralAnimalShow extends AnimalShow[Animal] {
    override def show: String = "animals everywhere"
  }
  implicit object CatsShow extends AnimalShow[Cat] {
    override def show: String = "so many cats"
  }

  def organizeShow[T](implicit event: AnimalShow[T]) = {
    event.show
  }

  //rule 2 : covariant TCs will always use the more specific TC instance for that type
  // but may confuse the compiler if the general TC is also present
  println(organizeShow[Cat])  // ok - the compiler will inject CatsShow as implicit
  // println(organizeShow[Animal])  // will not compile - ambiguous values

    // cant have both, then use invariance with smart constructors
  //example
  Option(2) === Option.empty[Int]








}
