import java.util.*;

public class Utilities {
	public static List<Integer> getDivisors(int n) {
		List<Integer> divisors = new ArrayList<>();
		divisors.add(1);
		for (int i = 2; i <= n / 2; i++) {
			if ((n - 1) % i == 0) {
				divisors.add(i);
			}
		}
		divisors.add(n - 1);
		return divisors;
	}

	private Utilities() {
		// prevent instantiation
	}
}
