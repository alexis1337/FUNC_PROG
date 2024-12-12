file:///D:/proj/FUNC_PROG/lb2/src/main/scala/Main.scala
### java.lang.AssertionError: assertion failed: position error, parent span does not contain child span
parent      = new Exception(_root_.scala.Predef.???) # -1,
parent span = <3039..3056>,
child       = _root_.scala.Predef.??? # -1,
child span  = [3053..3062..3062]

occurred in the presentation compiler.

presentation compiler configuration:


action parameters:
uri: file:///D:/proj/FUNC_PROG/lb2/src/main/scala/Main.scala
text:
```scala
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
  val stepSize = (r - l) / steps  
  // точки разбиения
  val points = (0 to steps).map(i => l + i * stepSize)  
  // значения функции в точках
  val valuesForSimpson = points.map(f)  
  // метод Симпсона:
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
      password 
    case (_, Right(errors)) =>
      println(s"Не соблюдены все условия!: $errors")
      throw new Exception(") 
    case _ =>
      throw new Exception("Неизвестная ошибка.")
  }.recoverWith { case _ => readPassword() }
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
```



#### Error stacktrace:

```
scala.runtime.Scala3RunTime$.assertFailed(Scala3RunTime.scala:8)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:177)
	dotty.tools.dotc.ast.Positioned.check$1$$anonfun$3(Positioned.scala:207)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.immutable.List.foreach(List.scala:333)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:207)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.check$1$$anonfun$3(Positioned.scala:207)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.immutable.List.foreach(List.scala:333)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:207)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.check$1$$anonfun$3(Positioned.scala:207)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.immutable.List.foreach(List.scala:333)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:207)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.check$1$$anonfun$3(Positioned.scala:207)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.immutable.List.foreach(List.scala:333)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:207)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:202)
	dotty.tools.dotc.ast.Positioned.check$1$$anonfun$3(Positioned.scala:207)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.immutable.List.foreach(List.scala:333)
	dotty.tools.dotc.ast.Positioned.check$1(Positioned.scala:207)
	dotty.tools.dotc.ast.Positioned.checkPos(Positioned.scala:228)
	dotty.tools.dotc.parsing.Parser.parse$$anonfun$1(ParserPhase.scala:39)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	dotty.tools.dotc.core.Phases$Phase.monitor(Phases.scala:477)
	dotty.tools.dotc.parsing.Parser.parse(ParserPhase.scala:40)
	dotty.tools.dotc.parsing.Parser.$anonfun$2(ParserPhase.scala:52)
	scala.collection.Iterator$$anon$6.hasNext(Iterator.scala:479)
	scala.collection.Iterator$$anon$9.hasNext(Iterator.scala:583)
	scala.collection.immutable.List.prependedAll(List.scala:152)
	scala.collection.immutable.List$.from(List.scala:684)
	scala.collection.immutable.List$.from(List.scala:681)
	scala.collection.IterableOps$WithFilter.map(Iterable.scala:898)
	dotty.tools.dotc.parsing.Parser.runOn(ParserPhase.scala:53)
	dotty.tools.dotc.Run.runPhases$1$$anonfun$1(Run.scala:315)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	scala.collection.ArrayOps$.foreach$extension(ArrayOps.scala:1323)
	dotty.tools.dotc.Run.runPhases$1(Run.scala:337)
	dotty.tools.dotc.Run.compileUnits$$anonfun$1(Run.scala:350)
	dotty.tools.dotc.Run.compileUnits$$anonfun$adapted$1(Run.scala:360)
	dotty.tools.dotc.util.Stats$.maybeMonitored(Stats.scala:69)
	dotty.tools.dotc.Run.compileUnits(Run.scala:360)
	dotty.tools.dotc.Run.compileSources(Run.scala:261)
	dotty.tools.dotc.interactive.InteractiveDriver.run(InteractiveDriver.scala:161)
	dotty.tools.pc.MetalsDriver.run(MetalsDriver.scala:47)
	dotty.tools.pc.PcCollector.<init>(PcCollector.scala:42)
	dotty.tools.pc.PcSemanticTokensProvider$Collector$.<init>(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector$lzyINIT1(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.Collector(PcSemanticTokensProvider.scala:63)
	dotty.tools.pc.PcSemanticTokensProvider.provide(PcSemanticTokensProvider.scala:88)
	dotty.tools.pc.ScalaPresentationCompiler.semanticTokens$$anonfun$1(ScalaPresentationCompiler.scala:109)
```
#### Short summary: 

java.lang.AssertionError: assertion failed: position error, parent span does not contain child span
parent      = new Exception(_root_.scala.Predef.???) # -1,
parent span = <3039..3056>,
child       = _root_.scala.Predef.??? # -1,
child span  = [3053..3062..3062]