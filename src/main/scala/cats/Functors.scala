package cats

import scala.util.Try

/**
  def map[A, B](fa: F[A])(f: A => B): F[B]

  explaining map function in Functor.
  (fa : F[A]) — type constructor: a data type that take some other data type e.g List, Future , Options
  (f: A => B ) — a function that transform a type A to type B
  F[B] — the final return type of the Functor's map function
*/


object Functors extends App {

  // normal map function usage
  val aModifiedList = List(1,2,3).map(_ + 1)   // List(2,3,4)
  val aModifiedOption = Option(2).map(_ + 1)   // Some(3)
  val aModifiedTry = Try(33).map((_ + 1))  // Success(34

  //simplified definition of Functor by creating a similar
  trait MyFunctor[F[_]] {      // F[_] is a higher-kinded(composite type/type constructor) where a generic data type take another type
    def map[A,B](container: F[A])(f: A => B): F[B]
  }

  // cats Functor
  import cats.Functor
  import cats.instances.list._            // includes Functor[List] and all other types
  val listFunctor = Functor[List]         // returns instance of List type by calling apply
  val incrementedNumbers = listFunctor.map((1 to 5).toList)(_ + 1)

  import cats.instances.option._         // includes Functor[Option] among all other types
  val optionFunctor = Functor[Option]     // returns instance of Option type by calling apply
  val incrementdOption= optionFunctor.map(Option(2)) (_ + 1)

  import cats.instances.try_._         // try._ has underscore to differentiate with java try keyword
  val tryFunctor = Functor[Try]
  val incrementedTry = tryFunctor.map(Try(33))(_ + 1)


  //repetition of API when we use normal map and so using Functor map to generalize API
  def do10xList(list: List[Int]) = list.map(_ * 10)
  def do10xOption(option: Option[Int]) = option.map(_ * 10)
  def do10xTry(attempt: Try[Int]) = attempt.map(_ * 10)

  //generalize for above problem
  def do10x[F[_]](container: F[Int])(implicit functor: Functor[F]) = {
    functor.map(container)(_ * 10)
  }

  println(do10x(List(1,2,3)))
  println(do10x(Option(1)))
  println(do10x(Try(2)))



//----------------------------------------------------------


  //using List as functor
  val skills = List("scala", "akka", "kafka", "fp")
  val wordLength: String => Int = (a: String) => a.length
  Functor[List]
    .map(skills)(wordLength)
    .foreach(println(_))

//calling fproduct of Functor to give the element with value resulted after applying function on it.
  Functor[List]
    .fproduct(skills)(wordLength)
    .foreach(println(_))

  // after importing 'import cats.syntax.functor._' to allow methods on datatype using extension method
  import cats.syntax.functor._
  skills
    .fproduct(wordLength)
    .foreach(println(_))

}
