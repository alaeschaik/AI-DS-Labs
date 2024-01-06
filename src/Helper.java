import java.util.List;

public class Helper {
    public static double calculateMean(List<Integer> numbers) {
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();
        return (double) sum / numbers.size();
    }

    public static double calculateStandardDeviation(List<Integer> numbers) {
        double mean = calculateMean(numbers);

        double sumOfSquares = numbers.stream()
                .mapToDouble(num -> Math.pow(num - mean, 2))
                .sum();

        return Math.sqrt(sumOfSquares / numbers.size());
    }
}