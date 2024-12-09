// Задание 1
@main def hello() = {
  println("Первое задание:")
  println("Hello, world!")
}

// Задание 2
def printHelloX(n: Int): Unit = {
  for (i <- 0 until n) {
        val x = if (i % 2 == 0) i else n - i
        println(s"Hello $x")
    }
}

// Задание 3.1
def splitIndex(collection: Seq[Int]): (Seq[Int], Seq[Int]) = {
  val evens = collection.zipWithIndex.filter(p => p._2 % 2 == 0 ).map(p => p._1)
  val odds = collection.zipWithIndex.filter(p => p._2 % 2 != 0 ).map(p => p._1)
  (evens, odds)
}

// Задание 3.2
def lookingForMax(collection: Seq[Int]): Int = collection.reduce((x, y) => if (x > y) x else y)

// Задание 4 внизу

// Задание 5
def patternMatching(num: Int): String = num match {
  case n if n % 2 == 0  => "Чётное число"
  case n if n % 2 != 0  => "Нечётное число"
}

// Задание 6
def compose[A, B, C](f: B => C, g: A => B): A => C = {
  (x: A) => f(g(x))
}

@main def main(): Unit = {
  // Задание 2
  println("Второе задание:")
  printHelloX(7)
  println()

  // Задание 3.1
  println("Третье задание:")
  println("Исходная коллекция:")
  val arr = Seq(11, 32, 23, 44, 52, 69, 0)
  println(arr)
  val (evens, odds) = splitIndex(arr)
  println(s"Чётный индекс: $evens, Нечётный индекс: $odds")
  println()

  // Задание 3.2
  println("Всё ещё третье задание:")
  val maxEl = lookingForMax(arr)
  println(s"Максимальный элемент: $maxEl")
  println()

  // Задание 4
  println("Четвёртое задание:")
  val newprintHelloX: Int => Unit = printHelloX
  println(newprintHelloX)
  println("Вывод переменной показывает ссылку на функцию")
  println("Лямбда-функция / адрес объекта в памяти @ хэш-код объекта") 
  newprintHelloX(7)
  println()

  // Задание 5
  println("Пятое задание:")
  println(s"11 - ${patternMatching(11)}")
  println(s"32 - ${patternMatching(32)}")
  println(s"23 - ${patternMatching(23)}")
  println(s"44 - ${patternMatching(44)}")
  println(s"52 - ${patternMatching(52)}")
  println(s"69 - ${patternMatching(69)}")
  println(s"0 - ${patternMatching(0)}")
  println()

  // Задание 6
  println("Шестое задание:")
  val f: Int => String = x => s"Результат: ${x * 2}"
  val g: Double => Int = y => (y * 3).toInt
  val composedFunction = compose(f, g)
  println(composedFunction(8.7)) 
}