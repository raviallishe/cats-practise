package cats

object Semigroups extends App {

  // semigroups combine elements of same type
  import cats.Semigroup
  import cats.instances.int._
  val naturalIntSemigroup = Semigroup[Int]
  val intCombination = naturalIntSemigroup.combine(2, 6)   //addition
  import cats.instances.string._
  val naturalStringSemigroup = Semigroup[String]
  val stringCombination = naturalStringSemigroup.combine("hello ",  "cats")    //concatenation

  println(intCombination)
  println(stringCombination)

  val numbers = (1 to 10).toList
  val strings = List("I ", "like ", "cats")

  // specific API
  def reduceInts(list: List[Int]) = list.reduce(naturalIntSemigroup.combine)
  def reduceString(list: List[String]) = list.reduce(naturalStringSemigroup.combine)

  //general API
  def reduce[T](list: List[T])(implicit semigroup: Semigroup[T]) = list.reduce(semigroup.combine)
  println(reduceInts(numbers))
  println(reduceString(strings))
  println(reduce(numbers))  //compiler injects the implicit Semigroup[Int]
  println(reduce(strings)) //compiler injects the implicit Semigroup[String]

  import cats.instances.option._
  // compiler will produce an implicit Semigroup[Option[Int] - combine will produce another option with the summed elements
  // compiler will produce an implicit Semigroup[Option[String] - combine will produce another option with the concatenated elements
  val numberOptions = numbers.map(Option(_))
  println(reduce(numberOptions))   // an Option[Int] containing the sum of all the numbers

  //supporting a new type
  case class Expense(id: Int, amount: Double)

  implicit val expenseType: Semigroup[Expense] = Semigroup.instance[Expense]{ (e1, e2) => Expense(Math.max(e1.id, e2.id), e1.amount + e2.amount)}
  val expenses = List(Expense(1, 44), Expense(2, 22))

  println(reduce(expenses))


}
