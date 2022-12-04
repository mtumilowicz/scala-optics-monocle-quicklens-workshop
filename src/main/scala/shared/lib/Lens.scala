package shared.lib

trait Lens[S, A] extends Getter[S, A] with Setter[S, A] { self =>
  def combine[B](lens: Lens[A, B]): Lens[S, B] =
    new Lens[S, B] {
      def modify(f: B => B): S => S =
        (lens.modify _).andThen(self.modify)(f)
      def get(s: S): B =
        (self.get _).andThen(lens.get)(s)
    }
}

object Lens {
  def apply[S, A](g: S => A, update: (A => A) => S => S): Lens[S, A] = new Lens[S, A] {
    override def modify(f: A => A): S => S = s => update(f)(s)

    override def get(s: S): A = g(s)
  }
}