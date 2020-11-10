package cats

object UsingMonads extends App {

  import cats.Monad
  import cats.instances.list._
  val monadList = Monad[List]        // fetch the implicit Monand[List] instance
  val aSimpleList = monadList.pure(5)    // List(2)
  val anExtendedList = monadList.flatMap(aSimpleList)(l => List(l, l + 2))
  // applicable to Option, Try, Future

  //Either is also monad
  val aManualEither: Right[String, Int] = Right(33)
  type LoadingOr[T] = Either[String, T]
  type ErrorOr[T] = Either[Throwable, T]
  import cats.instances.either._
  val loadingMonad = Monad[LoadingOr]
  val anEither: LoadingOr[Int] = loadingMonad.pure(44)
  val aChangedLoading: LoadingOr[Int] = loadingMonad.flatMap(anEither)(e => if(e % 2 ==0) Right(e + 4) else Left("Loading meaning of Life"))


  //imaginary online store
  case class OrderStatus(orderId: Long, status: String)
  def getOrderStatus(orderId: Long): LoadingOr[OrderStatus] = Right(OrderStatus(orderId, "Ready to ship"))
  def getTrackStatus(orderStatus: OrderStatus): LoadingOr[String] = if(orderStatus.orderId > 666) Left("Not available yet")
  else Right("Pune")

  val orderId = 55L
  val orderLocation: LoadingOr[String] = loadingMonad.flatMap(getOrderStatus(orderId))(orderStatus => getTrackStatus(orderStatus))

  //use extension method
  val orderLocationWithExtensionMethod = getOrderStatus(orderId).flatMap(orderStatus => getTrackStatus(orderStatus))
  val orderLocationFor = for {
    orderStatus <- getOrderStatus(orderId)
    location <- getTrackStatus(orderStatus)
  } yield location

  //service layer of webapp
  case class Connection(port: String, host: String)
  val config = Map( "port" -> "9000", "host" -> "localhost")

  trait HttpService[M[_]] {
    def getConnection(cfg: Map[String, String]): M[Connection]
    def issueRequest(connection: Connection, payload: String): M[String]
  }

   object OptionHttpService extends HttpService[Option] {
     override def getConnection(cfg: Map[String, String]): Option[Connection] = for {
       port <- cfg.get("port")
       host <- cfg.get("host")
     } yield Connection(port, host)

     override def issueRequest(connection: Connection, payload: String): Option[String] = if(payload.length > 20) None
     else Some("Http Request accepted")
  }

  // 1. different impl for Option
  val responseOption = for {
    conn <- OptionHttpService.getConnection(config)
    response <- OptionHttpService.issueRequest(conn, "Hello, http service")
  } yield response
  println(responseOption)

  object AggressiveHttpService extends HttpService[ErrorOr] {
    override def getConnection(cfg: Map[String, String]): ErrorOr[Connection] = {
      if(!cfg.contains("host") || !cfg.contains("port")) Left(new RuntimeException("something went wrong"))
      else Right(Connection(cfg.get("port").get, cfg.get("host").get))
    }

    override def issueRequest(connection: Connection, payload: String): ErrorOr[String] = {
      if(payload.length > 20) Left(new RuntimeException("length exceeds"))
      else Right("Http Request accepted")
    }
  }

  // 2. different impl for ErrorOr(Either) type
  val errorOrResponse = for {
    conn <- AggressiveHttpService.getConnection(config)
    response <- AggressiveHttpService.issueRequest(conn, "Hello, http service")
  } yield response
  println(errorOrResponse)


  // generalize using extension methods from Monad
  import cats.syntax.flatMap._
  import cats.syntax.functor._
  def getResponse[M[_]](httpService: HttpService[M], payload: String)(implicit monad: Monad[M]): M[String] = for {
    conn <- httpService.getConnection(config)
    response <- httpService.issueRequest(conn, payload)
  } yield response

  import cats.instances.option._
  println(getResponse(OptionHttpService, "Hello"))
  println(getResponse(AggressiveHttpService, "Halo"))
}
