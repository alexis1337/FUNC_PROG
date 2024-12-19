import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import scala.math._

// Актор для вычисления интеграла
object IntegrateActor {
  case class Message(integral: DefiniteIntegral, replyTo: ActorRef[Double])

  def apply(): Behavior[Message] = Behaviors.receive { (context, message) =>
    message match {
      // обработка сообщения (вычисление интеграла, отправка результата)
      case Message(DefiniteIntegral(f, l, r, i), replyTo) =>
        val result = integrate(f, l, r, i) 
        replyTo ! result 
    }
    Behaviors.same 
  }

  // Метод Симпсона
  private def integrate(f: Double => Double, l: Double, r: Double, i: Int): Double = {
    val n = i * 2 // кол-во отрезков
    val step = (r - l) / n // шаг разбиения

    // суммы значений функции в нечетных и четных точках
    val sumOdd = (1 until n by 2).map(k => f(l + k * step)).sum
    val sumEven = (2 until n by 2).map(k => f(l + k * step)).sum

    // сама формула Симпсона
    (f(l) + 4 * sumOdd + 2 * sumEven + f(r)) * step / 3
  }
}

// Актор для получения всего результата от "маленьких" интегралов
object SumActor {
  def apply(maxSteps: Int, replyTo: ActorRef[Double]): Behavior[Double] =
    Behaviors.setup(_ => receiver(0.0, maxSteps, replyTo))

  // Поведение актора с сохранением промежуточного результата
  private def receiver(sum: Double, remainingSteps: Int, replyTo: ActorRef[Double]): Behavior[Double] =
    Behaviors.receiveMessage { message =>
      val newSum = sum + message 
      if (remainingSteps > 1) // если есть результаты, требующие обработки
        receiver(newSum, remainingSteps - 1, replyTo)
      else {
        replyTo ! newSum 
        Behaviors.stopped 
      }
    }
}

// Актор для вывода логеров результатов
object DoubleLogger {
  def apply(): Behavior[Double] = Behaviors.receive { (context, message) =>
    context.log.info(s"Received result: $message") 
    Behaviors.same
  }
}

// Актор для управления системой 
object IntegrateSystem {
  case class Message(integral: DefiniteIntegral, t: Int, replyTo: ActorRef[Double])

  def apply(): Behavior[Message] = Behaviors.setup { context =>
    val integrateActors = Vector.tabulate(4)(i => context.spawn(IntegrateActor(), s"integrateActor$i"))

    Behaviors.receiveMessage { case Message(integral, t, replyTo) =>
      val sumActor = context.spawn(SumActor(t, replyTo), s"sumActor-${System.nanoTime()}")
      val step = (integral.r - integral.l) / t // Шаг разбиения по количеству частей

      // разбиваем интеграл на подынтегралы и распределяем между акторами
      (0 until t).foreach { i =>
        val subIntegral = DefiniteIntegral(
          integral.f,
          integral.l + i * step, // левая граница
          integral.l + (i + 1) * step, // правая граница
          integral.i // кол-во шагов внутри подынтеграла
        )
        val actor = integrateActors(i % integrateActors.length) // выбираем актор
        actor ! IntegrateActor.Message(subIntegral, sumActor) // отправляем сообщение
      }
      Behaviors.same
    }
  }
}

case class DefiniteIntegral(f: Double => Double, l: Double, r: Double, i: Int)

@main def main(): Unit = {
  val integrateSystem = ActorSystem(IntegrateSystem(), "integrateSystem")
  val doubleLogger = ActorSystem(DoubleLogger(), "doubleLogger")

  integrateSystem ! IntegrateSystem.Message(DefiniteIntegral(sin, 0, 2 * Pi, 100), 100, doubleLogger)
  integrateSystem ! IntegrateSystem.Message(DefiniteIntegral(x => x * 0.5, 0, 2, 100), 100, doubleLogger)
}
