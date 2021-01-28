import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class TicketsPercentileApplication {

    private static String sourcePath = "src/main/resources/tickets.json";
    private static String departureName = "Владивосток";
    private static String arrivalName = "Тель-Авив";
    private static int departureTimeShift = 10;
    private static int arrivalTimeShift = 2;
    private static int percentile = 90;

    public static void main(String[] args) {
        initArgs(args);

        TicketsIndex ticketsIndex =
                new TicketsIndex(sourcePath, departureTimeShift, arrivalTimeShift, departureName, arrivalName);

        System.out.println("Flight duration from " + departureName + " to " + arrivalName + ":");

        System.out.print("Average time: ");
        printResult(calculateMiddleFlightTime(ticketsIndex.getTickets()));

        System.out.print(percentile + " percentile: ");
        printResult(calculatePercentile(percentile, ticketsIndex.getTickets()));
    }

    private static void initArgs(String[] args) {
        int i = 0;
        try {
            if (args.length > 0) {
                for (i = 0; i < args.length - 1; i += 2) {
                    switch (args[i]) {
                        case "path":
                            sourcePath = args[i + 1];
                            break;
                        case "from":
                            departureName = args[i + 1];
                            break;
                        case "to":
                            arrivalName = args[i + 1];
                            break;
                        case "from-zone":
                            departureTimeShift = Integer.parseInt(args[i + 1]);
                            break;
                        case "to-zone":
                            arrivalTimeShift = Integer.parseInt(args[i + 1]);
                            break;
                        case "percentile":
                            percentile = Integer.parseInt(args[i + 1]);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Argument " + args[i] + " must be an integer.");
            System.exit(1);
        }
    }

    private static long calculateMiddleFlightTime(List<Ticket> tickets) {
        OptionalDouble averageOptional = tickets.stream().mapToLong(Ticket::getDuration).average();
        return averageOptional.isPresent() ? Math.round(averageOptional.getAsDouble()) : 0L;
    }

    private static long calculatePercentile(int percentile, List<Ticket> tickets) {
        Optional<Ticket> optional = tickets.stream().sorted(new DurationComparator())
                .skip((long) percentile * (tickets.size() - 1) / 100L).limit(1)
                .findFirst();
        if (optional.isPresent()) {
            return optional.get().getDuration();
        }
        return 0L;
    }

    private static void printResult(long durationInMilliseconds) {
        if (durationInMilliseconds == 0L) {
            System.out.println("not found!");
        } else {
            Duration duration = Duration.ofMillis(durationInMilliseconds);
            long days = duration.toDays();
            long hours = duration.minusDays(days).toHoursPart();
            int minutes = duration.minusDays(days).minusHours(hours).toMinutesPart();
            System.out.println(days + " days, " + hours + " hours, " + minutes + " minutes");
        }
    }
}

class DurationComparator implements Comparator<Ticket> {

    @Override
    public int compare(Ticket o1, Ticket o2) {
        return o1.getDuration().compareTo(o2.getDuration());
    }
}