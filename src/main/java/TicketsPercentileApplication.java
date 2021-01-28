import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public class TicketsPercentileApplication {

    private static String sourcePath = "src/main/resources/ticketswin.json";
    private static String departureName = "Владивосток";
    private static String arrivalName = "Тель-Авив";
    private static int departureTimeShift = 10;
    private static int arrivalTimeShift = 2;
    private static int percentile = 90;

    public static void main(String[] args) {
        TicketsIndex ticketsIndex =
                new TicketsIndex(sourcePath, departureTimeShift, arrivalTimeShift, departureName, arrivalName);

        System.out.print("Average time: ");
        printResult(calculateMiddleFlightTime(ticketsIndex.getTickets()));

        System.out.print(percentile + " percentile: ");
        printResult(calculatePercentile(percentile, ticketsIndex.getTickets()));
    }

    private static long calculateMiddleFlightTime(List<Ticket> tickets) {
        OptionalDouble averageOptional = tickets.stream().mapToLong(Ticket::getDuration).average();
        return averageOptional.isPresent() ? Math.round(averageOptional.getAsDouble()) : 0L;
    }

    private static long calculatePercentile(int percentile, List<Ticket> tickets) {
        return tickets.stream().sorted(new DurationComparator())
                .skip((long) percentile * (tickets.size() - 1) / 100L).limit(1)
                .findFirst().orElseThrow().getDuration();
    }

    private static void printResult(long durationInMilliseconds) {
        Duration duration = Duration.ofMillis(durationInMilliseconds);
        long days = duration.toDays();
        long hours = duration.minusDays(days).toHoursPart();
        int minutes = duration.minusDays(days).minusHours(hours).toMinutesPart();
        System.out.println(days + " days, " + hours + " hours, " + minutes + " minutes");
    }
}

class DurationComparator implements Comparator<Ticket> {

    @Override
    public int compare(Ticket o1, Ticket o2) {
        return o1.getDuration().compareTo(o2.getDuration());
    }
}
