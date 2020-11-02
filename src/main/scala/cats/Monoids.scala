package cats

object Monoids extends App {

  import cats.Semigroup
  import cats.instances.int._
  import cats.syntax.semigroup._       // import te |+| extension method
  val numbers = (1 to 100).toList
  // |+| is always associative meaning a |+| b == b |+| a is always same

  val sumLeft = numbers.foldLeft(0)(_ |+| _)
  val sumRight = numbers.foldLeft(0)(_ |+| _)  // both are same

  //general API
//  def combineFold[T](list: List[T])(implicit semigroup: Semigroup[T]) = {
//    list.foldLeft(/* what can be general neutral value for every type defined using semigroup?*/)(_ |+| _)
//  }   //  this is the drawback of semigroup not providing a default value.
  // Monoid overcomes the drawback by providing empty method to each type. It extends Semigroup

  //Monoids
  import cats.Monoid
  val intMonoid = Monoid[Int]    // having access to implict instance of Monoid[Int] type class
  val combineInt = intMonoid.combine(4,5)
  val zero = intMonoid.empty // 0

  import cats.instances.string._   //bring the implicit Monoid[String] in scope
  val emptyString = Monoid[String].empty      // ""

  import cats.instances.option._   //construct an implicit Monoid[Option[Int]]
  val emptyOption = Monoid[Option[Int]].empty // None
  val combineOption = Monoid[Option[Int]].combine(Option(3), Option.empty[Int])

  //solving above general api combineFold using Monoid

  def combineFold[T](list: List[T])(implicit monoid: Monoid[T]): T = {
    list.foldLeft(monoid.empty)(_ |+| _)
  }

  println(combineFold(numbers))
  println(combineFold(List("I ", "like ", "monoids")))

  // TODO - combine multiple phonebooks in one phonebook with Map[String, phone] using above general API combineFold
  val map1 = Map("ravi" -> 111, "olly" -> 222)
  val map2 = Map("Tina" -> 666)
  import cats.instances.map._

  println(combineFold(List(map1, map2)))

  // todo - shoppin cart and online store using Monoids

  case class ShoppingCart(items: List[String], total: Double)

  implicit val shoppingCartMonoid = Monoid.instance[ShoppingCart](ShoppingCart
  (List.empty, 0.0), (s1, s2) => ShoppingCart(s1.items ++ s2.items, s1.total + s2.total ))

  def checkout(shoppingCarts: List[ShoppingCart]): ShoppingCart = combineFold(shoppingCarts)

  
}
