import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.util.Random

object AddingServer {
  // запрос на сложение 2-х чисел 
  case class AddRequest(a: Float, b: Float, replyTo: ActorRef[AddResponse])
  
  // ответ
  case class AddResponse(result: Float)

  // поведение сервера
  def apply(): Behavior[AddRequest] = Behaviors.receive { (context, message) =>
    val result = message.a + message.b    
    message.replyTo ! AddResponse(result) 
    Behaviors.same
  }
}

object AddingClient {
  // команды, которые может отправить клиент
  enum CommandType:
    case Start, Response

  // команда для клиента
  case class Command(action: CommandType, data: Option[(Float, String)] = None)

  // поведение клиент, он отправляет запросы на сложение
  def apply(server: ActorRef[AddingServer.AddRequest], system: ActorSystem[_]): Behavior[AddingClient.Command] = Behaviors.setup { (context) =>
    def sendRandomNumbers(): Unit = {
      val a = Random.nextFloat() * 100
      val b = Random.nextFloat() * 100
      context.log.info(s"Sending numbers $a and $b to server")
      server ! AddingServer.AddRequest(a, b, context.messageAdapter(r => 
        Command(CommandType.Response, Some((r.result.toFloat, s"Numbers $a and $b")))
      ))
    }

    Behaviors.receiveMessage {
      case Command(CommandType.Start, _) =>
        sendRandomNumbers()
        Behaviors.same

      case Command(CommandType.Response, Some((result, message))) =>
        context.log.info(s"$message have a sum of $result")        
        system.terminate() // попытался выключить систему
        Behaviors.same

      case _ =>
        Behaviors.same
    }
  }
}

object AddingSystem {
  def apply(): Behavior[Unit] = Behaviors.setup { (context) =>
    val server = context.spawn(AddingServer(), "server")
    var responsesReceived = 0
    val totalClients = 3 

    (0 to totalClients - 1).foreach { i =>
      val client = context.spawn(AddingClient(server, context.system), s"client-$i")
      client ! AddingClient.Command(AddingClient.CommandType.Start)
    }

    // завершение системы
    Behaviors.receiveMessage {
      case () =>
        responsesReceived += 1
        Behaviors.empty
    }
  }
}

@main def newAddingSystem(): Unit = {
  val system = ActorSystem(AddingSystem(), "system")
}
