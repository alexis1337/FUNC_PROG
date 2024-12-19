package com.example
import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.GreeterMain.SayHello

object Greeter { // Актор для отправки сообщения
  final case class Greet(whom: String, replyTo: ActorRef[Greeted]) // тот, кто получит ответ
  final case class Greeted(whom: String, from: ActorRef[Greet]) // тот, кто отсылает

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) => // создаётся поведение для актора
    context.log.info("Hello {}!", message.whom) // логер в консоли 
    //#greeter-send-messages
    message.replyTo ! Greeted(message.whom, context.self) // возвращаем сообщение обратно отправителю с информацией, с кем поздоровались
    //#greeter-send-messages
    Behaviors.same // возвращаем текущее поведение 
  }
}

object GreeterBot { // Актор для отпраки нескольких сообщений

  def apply(max: Int): Behavior[Greeter.Greeted] = { // создаётся поведение для актора
    bot(0, max) // начнём с нуля приветствий 
  }

  private def bot(greetingCounter: Int, max: Int): Behavior[Greeter.Greeted] = // принимет текущее и максимальное число сообщений
    Behaviors.receive { (context, message) =>
      val n = greetingCounter + 1 // увеличивается число сообщений
      context.log.info("Greeting {} for {}", n, message.whom) // логер для консоли
      if (n == max) {
        Behaviors.stopped // конец, если максимум
      } else {
        message.from ! Greeter.Greet(message.whom, context.self) // иначе отправка нового письма
        bot(n, max) // новое состояние актора с обновлённым счётчиком
      }
    }
}
 
object GreeterMain { // Актор, управляющий остальными акторами

  final case class SayHello(name: String) // сообщение, в котором содержится имя приветствия 

  def apply(): Behavior[SayHello] = // создаётся поведение для актора
    Behaviors.setup { context =>
      val greeter = context.spawn(Greeter(), "greeter") // создаём потомка у актора

      Behaviors.receiveMessage { message => // определяем поведение
        val replyTo = context.spawn(GreeterBot(max = 3), message.name) // создаём актора-бота (не более 3-х приветствий)
        greeter ! Greeter.Greet(message.name, replyTo) // от получателя к отправителю
        Behaviors.same // возвращаем текущее поведение
      }
    }
}

object AkkaQuickstart extends App {
  val greeterMain: ActorSystem[GreeterMain.SayHello] = ActorSystem(GreeterMain(), "AkkaQuickStart") // целая система акторов
  greeterMain ! SayHello("Charles") // привет, Чарльз 
}
