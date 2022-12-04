package shared.lib

trait Setter[S, A] { self =>
  def modify(f: A => A): S => S
  def set(a: A): S => S = modify(_ => a)

  def compose[B](setter: Setter[A, B]): Setter[S, B] =
    (f: B => B) => (setter.modify _).andThen(self.modify)(f)
}

object Setter {
  def apply[S, A](update: (A => A) => S => S): Setter[S, A] =
    (f: A => A) => update(f)
}