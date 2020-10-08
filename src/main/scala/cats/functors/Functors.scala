package cats.functors

import cats._
import cats.implicits.catsStdInstancesForList

/**
  def map[A, B](fa: F[A])(f: A => B): F[B]

  explanation map function in Functor.
  (fa : F[A]) — type constructor: a data type that take some other data type e.g List, Future , Options
  (f: A => B ) — a function that transform a type A to type B
  F[B] — the final return type of the Functor's map function
*/


object Functors extends App {

  //using List as functor
  val skills = List("scala", "akka", "kafka", "fp")
  val wordLength: String => Int = (a: String) => a.length
  Functor[List].map(skills)(wordLength)
    .foreach(println(_))

}
