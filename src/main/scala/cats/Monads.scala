package cats

import java.util.concurrent.{ExecutorService, Executors}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}

object Monads {

  // lists
  val numbersList = List(1,2,3)
  val charsList = List('a', 'b', 'c')
  // todo 1.1 how do you create all the combinations of (number, char)
  val combinationsList : List[(Int, Char)] = numbersList.flatMap(n => charsList.map(c => (n, c)))
  val combinationListFor = for {
    n <- numbersList
    c <- charsList
  } yield (n, c)

  //options
  val numOption = Option(2)
  val charOption = Option('a')
  // todo 1.2: creating combination of (number, char)
  val combinationOption = numOption.flatMap(n => charOption.map(c => (n, c)))
  val combinationOptionFor = for {
    n <- numOption
    c <- charOption
  } yield (n, c)

  //similarly for futures
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
  val numFuture = Future(2)
  val charFuture = Future('a')
  // todo 1.3: creating combination of (number, char)
  val combinationFuture = numFuture.flatMap(n => charFuture.map(c => (n, c)))
  val combinationFutureFor = for {
    n <- numFuture
    c <- charFuture
  } yield (n, c)

  /*
  Pattern
  - wrapping a value into M value
  - the flatMap definition
   */
  //Monads
  trait  MyMonad[M[_]] {
    def pure[A](value: A): M[A]
    def flatMap[A, B](initialValue: M[A])(f: A => M[B]): M[B]
    //implement map using pure and flatMap above -- cats Monad map is also implemented in the same way after extending from Functor
    def map[A,B](value: M[A])(f: A => B) : M[B] = flatMap(value)(x => pure(f(x)))
  }

  // cats Monad
  import cats.Monad
  import cats.instances.option._    // implicit Monad[Option]
  val optionMonad = Monad[Option]
  val anOption = optionMonad.pure(6)
  val aTransformedOption = anOption.flatMap(o => Option(o))

  import cats.instances.list._    // implicit Monad[List]
  val listMonad = Monad[List]
  val aList = listMonad.pure(6)
  val aTransformedList = aList.flatMap(l => List(l))

  import cats.instances.future._    // implicit Monad[Future]
  val futureMonad = Monad[Future]
  val aFuture = futureMonad.pure(6)
  val aTransformedFuture = aFuture.flatMap(f => Future(f))

  //specialized API  --- you can see the duplication below
  def getPairsList(numbers: List[Int], chars: List[Char]): List[(Int, Char)] = numbers.flatMap(n => chars.map(c => (n, c)))
  def getPairsOption(numbers: Option[Int], chars: Option[Char]): Option[(Int, Char)] = numbers.flatMap(n => chars.map(c => (n, c)))
  def getPairsFuture(numbers: Future[Int], chars: Future[Char]): Future[(Int, Char)] = numbers.flatMap(n => chars.map(c => (n, c)))

  // generalize using API with Monads tc
  def getPairs[M[_], A, B](numbers: M[A], chars: M[B])(implicit monads: Monad[M]) =
    monads.flatMap(numbers)(n => monads.map(chars)(c => (n, c)))

  //extension methods - weirder imports - pure and flatMap
  //pure
  import cats.syntax.applicative._     // pure is here
  val oneOption = 1.pure[Option]       // implicit Monad[Option] will be used => Some(1)
  val oneList = 1.pure[List]

  // flatMap
  import cats.syntax.flatMap._        // flatMap is here
  val oneOptionTransformed = oneOption.flatMap(x => (x + 4).pure[Option])

  // Monad extends Functor             // Monad extends Applicative extends Apply extends Functor
  import cats.syntax.functor._         // map is here
  val oneOptionMapped = oneOption.map(_ + 2)
  // for comprehensions
  val composedOptionFor = for {
    one <- 1.pure[Option]
    two <- 2.pure[Option]
  } yield (one + two)

  println(getPairs(numbersList, charsList))
  println(getPairs(numOption, charOption))
  println(getPairs(numFuture, charFuture))
}
