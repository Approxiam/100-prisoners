public class SimulationResult {
	private int count;
	private double sum = 0;
	private double min = Double.POSITIVE_INFINITY;
	private double max = Double.NEGATIVE_INFINITY;

	public SimulationResult(int count) {
		this.count = count;
	}

	public void accumulate(int specday) {
		if (specday < min) {
			min = specday;
		}
		if (specday > max) {
			max = specday;
		}
		sum += specday;
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
		return sum / count;
	}
	public double getAvgYears() {
		return daysToYear(getAvgDays());
	}

	private static double daysToYear(double days) {
		return days / 365.0d;
	}
}
