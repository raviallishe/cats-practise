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
  - the flatMap defination
   */
  //Monads
  trait  MyMonad[M[_]] {
    def pure[A]: M[A]
    def flatMap[A, B](initialValue: M[A])(f: A => M[B]): M[B]
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

  
  println(getPairs(numbersList, charsList))
  println(getPairs(numOption, charOption))
  println(getPairs(numFuture, charFuture))
}
