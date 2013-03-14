package myai;

/**
 * Why is java such a heart-breaker?
 * 
 * @author toriscope
 * 
 */
public class Tuples {
	public static class Tuple2<A, B> {
		final public A	a;
		final public B	b;

		public Tuple2(A a, B b) {
			this.a = a;
			this.b = b;
		}
	}

	public static class Tuple3<A, B, C> {
		final public A	a;
		final public B	b;
		final public C	c;

		public Tuple3(A a, B b, C c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}
	}

	public static class Tuple4<A, B, C, D> {
		final public A	a;
		final public B	b;
		final public C	c;
		final public D	d;

		public Tuple4(A a, B b, C c, D d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}
	}

	public static <A, B> Tuple2<A, B> T(A a, B b) {
		return new Tuple2<A, B>(a, b);
	}

	public static <A, B, C> Tuple3<A, B, C> T(A a, B b, C c) {
		return new Tuple3<A, B, C>(a, b, c);
	}

	public static <A, B, C, D> Tuple4<A, B, C, D> T(A a, B b, C c, D d) {
		return new Tuple4<A, B, C, D>(a, b, c, d);
	}
}
