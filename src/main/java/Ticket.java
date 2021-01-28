import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Ticket {

    private String origin;
    private String origin_name;
    private String destination;
    private String destinationName;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private String carrier;
    private Integer stops;
    private Integer price;
    private Long duration;

    public Ticket(String origin, String origin_name, String destination, String destinationName,
                  LocalDate departureDate, LocalTime departureTime,
                  LocalDate arrivalDate, LocalTime arrivalTime,
                  String carrier, Integer stops, Integer price) {
        this.origin = origin;
        this.origin_name = origin_name;
        this.destination = destination;
        this.destinationName = destinationName;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.carrier = carrier;
        this.stops = stops;
        this.price = price;
    }
}
