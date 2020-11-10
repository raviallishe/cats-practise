package datamanipulation

import cats.Id
import cats.data.Kleisli

object Readers extends App {
  /*project structure
  - config file
  - DB layer
  - Http client layer
  - business logic layer
   */

  case class Configuration(user: String, pass: String, host: String, port: Int, emailReplyTo: String)
  case class DbConnection(user: String, pass: String) {
    def getOrderStatus(orderId: Long) : String = "dispatced"
    def getLastOrderId(user: String): Long = 12321
  }
  case class HttpService(host: String, port: Int) {
    def start() = "server started"
  }

  //
  val config = Configuration("ravi", "ravi.cats!", "localhost", 8080, "rave@aol.com")
  //cats reader
  import cats.data.Reader
  val dbReader: Reader[Configuration, DbConnection] = Reader(conf => DbConnection(conf.user, conf.pass))
  val dbConn = dbReader.run(config)

  // Reader[I, O]
  val orderStatusReader: Reader[Configuration, String] = dbReader.map(dbConn => dbConn.getOrderStatus(66))
  val orderStatus: String = orderStatusReader.run(config)

  def getLastOrderStatus(user: String): String = {
    val userLastOrderIdReader: Reader[Configuration, String] = dbReader
      .map(_.getLastOrderId(user))
      .flatMap(lastId => dbReader.map(_.getOrderStatus(lastId)))

    // identical

    val userOrderFor = for {
      lastId <- dbReader.map(_.getLastOrderId(user))
      orderStatus <- dbReader.map(_.getOrderStatus(lastId))
    } yield orderStatus

    userLastOrderIdReader.run(config)
  }

  /* Pattern
  1. you create the initial data structure
  2. you create a reader which specifies of that data structure will be manipulated later
  3. you can then map and flatMap the reader to produce derived information
  4. when you need the final piece of inf, you call run on the reader with the initial data structure
   */

case class EmailService(emailReplyTo: String) {
  def sendMail(address: String, contents: String) = s"from: $emailReplyTo; to: $address; contents: $contents"
}

  //todo:
  def emailUser(userName: String, userEmail: String) = {
    // fetch the status of their last order
    // email them with the email service: "your last order has the status: (status)"
    val emailServiceReader: Reader[Configuration, EmailService] = Reader(conf => EmailService(conf.emailReplyTo))
    val emailReader: Reader[Configuration, String] =
      for {
        lastId <- dbReader.map(_.getLastOrderId(userName))
        orderStatus <- dbReader.map(_.getOrderStatus(lastId))
        emailService <- emailServiceReader
      }yield emailService.sendMail(userEmail, s"your last order has the status: ($orderStatus)")
  }

  println(getLastOrderStatus("ravi"))
  println(emailUser("ravi", "rave@aol.com"))
}

