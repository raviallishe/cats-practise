package datamanipulation

object Writers extends App {
  import cats.data.Writer
  // 1. define them at first
  val aWriter: Writer[List[String], Int] = Writer(List("started something"), 5)
  val anIncrementWriter = aWriter.map(_ + 1)   // value increases, los stay the same
  val aLogWriter = aWriter.mapWritten(_ :+ "finished something")         // log appended, value stays same
  val aWriterWithBoth = aWriter.bimap(_ :+ "finished something", _ + 3)
  val aWriterWithBoth2 = aWriter.mapBoth((logs, value) => (logs :+ "finished something", value + 1))

  // flatMap
  import cats.instances.vector._                            //imports a Semigroup[Vector]
  val writerA = Writer(Vector("Log A1", "Log A2"), 10)
  val writerB = Writer(Vector("Log B1"), 40)
  val compositeWriter = for {
    va <- writerA
    vb <- writerB
  } yield va + vb

  //reset the logs
  import cats.instances.list._         // a Monoid[List[Int]]
  val anEmptyWriter = aWriter.reset

  //3 dump either the value or the logs
  val desiredValue = aWriter.value
  val logs = aWriter.written
  val (l, v) = aWriter.run


  //todo 1: rewrite a function which prints things with writers
  def countAndSay(n: Int): Unit = {
    if(n <= 0) println("starting")
    else {
      countAndSay(n-1)
      println(n)
    }
  }

  def countAndLog(n: Int): Writer[List[String], Int] = {
    if(n <= 0) Writer(Vector("starting"), 0)
    else countAndLog(n -1).flatMap( _ => Writer(Vector(s"$n"), n))
  }

  countAndLog(10).written.foreach(println(_))
  // Benefit: we work wit pure FP by pushing side effects like printing on console to the end of the world


  //todo 2: rewrite below function with Writers
  def naiveSum(n: Int): Int = {
    if( n == 0 ) 0
    else {
      println(s"now at $n")
      val lowerSum = naiveSum(n-1)
      println(s"computed sum($n - 1) = $lowerSum")
      lowerSum + n
    }
  }

//  def naiveSumWriter(n: Int) = {
//
//  }









}
