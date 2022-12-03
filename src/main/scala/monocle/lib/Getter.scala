package monocle.lib

trait Getter[S, A] { self =>
  def get(s: S): A

  def andThen[B](getter: Getter[A, B]): Getter[S, B] =
    (s: S) => (self.get _).andThen(getter.get)(s)
}
object Getter {
  def apply[S, A](f: S => A): Getter[S, A] =
    (s: S) => f(s)
}