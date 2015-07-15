import java.math.*;

public class SimulationResult {
	private int count;
	private BigDecimal sum = BigDecimal.ZERO;
	private BigDecimal sum2 = BigDecimal.ZERO;
	private int min = Integer.MAX_VALUE;
	private int max = Integer.MIN_VALUE;

	public void accumulate(int specday) {
		count++;
		if (specday < min) {
			min = specday;
		}
		if (specday > max) {
			max = specday;
		}
		BigDecimal dec = new BigDecimal(specday);
		sum = sum.add(dec);
		sum2 = sum2.add(dec.multiply(dec));
	}

	public int getCount() {
		return count;
	}

	public double getMinDays() {
		return min;
	}
	public double getMinYears() {
		return daysToYear(getMinDays());
	}

	public double getMaxDays() {
		return max;
	}
	public double getMaxYears() {
		return daysToYear(getMaxDays());
	}

	public double getAvgDays() {
		return sum.divide(new BigDecimal(count), RoundingMode.HALF_UP).doubleValue();
	}
	public double getAvgYears() {
		return daysToYear(getAvgDays());
	}

	public double getStdDevDays() {
		// D^2 = E(X^2) - E^2(X)
		BigDecimal expX2 = sum2.divide(new BigDecimal(count), RoundingMode.HALF_UP);
		BigDecimal expX = sum.divide(new BigDecimal(count), RoundingMode.HALF_UP);
		return Math.sqrt(expX2.subtract(expX.multiply(expX)).doubleValue());
	}
	public double getStdDevYears() {
		return daysToYear(getStdDevDays());
	}

	private static double daysToYear(double days) {
		return days / 365.0d;
	}
}
