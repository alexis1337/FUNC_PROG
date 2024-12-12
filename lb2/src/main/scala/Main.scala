import scala.io.StdIn.readLine

import scala.util.Try
import scala.util.Success
import scala.util.Failure

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

// Задание 1
def integralSimpson(f: Double => Double, l: Double, r: Double, steps: Int): Double = {
  require(steps % 2 == 0, "Число шагов должно быть чётным для метода Симпсона.")  
  val stepSize = (r - l) / steps // точки разбиения  
  val points = (0 to steps).map(i => l + i * stepSize) // значения функции в точках  
  val valuesForSimpson = points.map(f) // метод Симпсона:  
  val sum = valuesForSimpson.zipWithIndex.map {
    case (value, i) =>
      if (i == 0 || i == steps) value // крайние точки
      else if (i % 2 == 0) 2 * value // чётные точки
      else 4 * value // нечётные точки
  }.sum
  (stepSize / 3) * sum
}

// Для второго задания

val LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz"
val UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
val NUMBERS_LETTERS = "1234567890"
val SPECIAL_LETTERS = "!@#$%^&*()_+-=[]{}|;:'/\",.<>?"

// Задание 2 (Option)

def specialSymbols(passwordCandidate: String, someLetters: String, n: Int): Boolean = 
  passwordCandidate.count(char => someLetters.contains(char)) >= n

def goodEnoughPasswordOption(password: String): Boolean = {
  Seq(
    password.length >= 8,
    specialSymbols(password, LOWERCASE_LETTERS, 1),
    specialSymbols(password, UPPERCASE_LETTERS, 1),
    specialSymbols(password, NUMBERS_LETTERS, 1),
    specialSymbols(password, SPECIAL_LETTERS, 1)
  ).reduce(_ && _) // где-то видел штуку .forall(identity), но стоит ли использовать?
}

// Задание 2 (Try)

def goodEnoughPasswordTry(password: String): Either[Boolean, String] = {
  val requirements: Map[String, String => Boolean] = Map(
    "Пароль должен содержать минимум 8 символов." -> (_.length >= 8),
    "Пароль должен содержать минимум 1 латинскую строчную букву." -> (specialSymbols(_, LOWERCASE_LETTERS, 1)),
    "Пароль должен содержать минимум 1 латинскую заглавную букву." -> (specialSymbols(_, UPPERCASE_LETTERS, 1)),
    "Пароль должен содержать минимум 1 цифру." -> (specialSymbols(_, NUMBERS_LETTERS, 1)),
    "Пароль должен содержать минимум 1 специальный символ." -> (specialSymbols(_, SPECIAL_LETTERS, 1))
  )

  // проверяем и находим невыполненные условия
  val failedChecks = requirements.collect {
    case (message, check) if !check(password) => message
  }

  failedChecks.toList match {
    case Nil => Left(true) 
    case errors => Right(errors.mkString(" ")) 
  }
}

// Задание 2 (Future)

def readPassword(): Future[String] = {
  Future {
    println("Введите пароль: ")
    readLine()
  }.map { password =>
    (password, goodEnoughPasswordTry(password))
  }.map {
    case (password, Left(true)) =>
      Some(password) 
    case (_, Right(errors)) =>
      println(s"Не соблюдены все условия!: $errors")
      None 
    case _ =>
      println("Неизвестная ошибка")
      None
  }.flatMap {
    case Some(password) => Future.successful(password) 
    case None => readPassword()             
  }
}

// Задание 3

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

object optionFunctor extends Functor[Option] {
  def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
    case Some(x) => Some(f(x))
    case None => None
  }
}

trait Monad[F[_]] {
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
  def pure[A](value: A): F[A]
}

object optionMonad extends Monad[Option] {
  def flatMap[A, B](ma: Option[A])(f: A => Option[B]): Option[B] = ma match {
    case Some(x) => f(x)
    case None => None
  }
  def pure[A](value: A): Option[A] = Some(value)
}

@main def Integral(): Unit = {
  println("Первое задание:")
  val result = integralSimpson(x => x * x, 5, 20, 10) // интервал [5; 20] с 10 шагами
  println(s"Результат: $result")  
  println()  
}

@main def PasswordOption(): Unit = {
 println("Второе задание (Option):")
 println("Придумайте пароль из 8 символов.")
 println("Обязательно наличие: 1 латинской строчной буквы, 1 латинской заглавной буквы, 1 спец. символа, 1 цифры")

 val input = readLine()
 val result = goodEnoughPasswordOption(input)

 if (result) {
   println(s"${result}, Пароль подходит!")
 } else {
   println(s"${result}, Не соблюдены все условия!")
 }
}

@main def PasswordTry(): Unit = {
 println("Второе задание (Try):")
 println("Придумайте пароль из 8 символов.")
 println("Обязательно наличие: 1 латинской строчной буквы, 1 латинской заглавной буквы, 1 спец. символа, 1 цифры")

 val input = readLine()
 val result = goodEnoughPasswordTry(input)

 if (result.isLeft) {
   println(s"${result}, Пароль подходит!")
 } else {
   println(s"${result}, Не соблюдены все условия!")
 }
}

@main def PasswordFuture(): Unit = {
 println("Второе задание (Future):")
 println("Придумайте пароль из 8 символов.")
 println("Обязательно наличие: 1 латинской строчной буквы, 1 латинской заглавной буквы, 1 спец. символа, 1 цифры")

 val passwordFuture = readPassword()
 val result = Await.result(passwordFuture, Duration.Inf)
 println("Пароль подходит!")
}

@main def monadAndFunctor(): Unit = {
    println("Третье задание:")
    val someOption: Option[Int] = Some(7)
    val noneOption: Option[Int] = None
    val pureOption = optionMonad.pure(52)

    println(optionFunctor.map(someOption)(_ * 2)) // Some(14)
    println(optionFunctor.map(noneOption)(_ * 2)) // None
    
    println(pureOption)  

    println(optionMonad.flatMap(someOption)(x => Some(x + 3))) // Some(10)
    println(optionMonad.flatMap(noneOption)(x => Some(x + 3))) // None

}