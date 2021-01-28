import lombok.Getter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketsIndex {

    private final String sourcePath;
    @Getter
    private final List<Ticket> tickets;


    public TicketsIndex(String sourcePath, int departureTimeShift, int arrivalTimeShift, String departureName, String arrivalName) {
        this.sourcePath = sourcePath;
        tickets = new ArrayList<>();
        createIndex(departureTimeShift, arrivalTimeShift, departureName, arrivalName);
    }

    private void createIndex(int departureTimeShift, int arrivalTimeShift, String departureName, String arrivalName) {
        try {
            File file = new File(sourcePath);
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(new FileReader(file));

            JSONArray ticketsArray = (JSONArray) jsonData.get("tickets");
            parseTickets(ticketsArray, departureName, arrivalName);

            calculateDuration(departureTimeShift, arrivalTimeShift);
        } catch (Exception e) {
            System.err.println(e.getMessage() != null ? e.getMessage() : "Wrong encoding! Use -Dfile.encoding=UTF-8");
            System.exit(1);
        }
    }

    private void parseTickets(JSONArray ticketsArray, String departureName, String arrivalName) {
        try {
            ticketsArray.forEach(lineObject -> {
                JSONObject ticketJsonObject = (JSONObject) lineObject;

                String originName = ticketJsonObject.get("origin_name").toString();
                String destinationName = ticketJsonObject.get("destination_name").toString();

                if (originName.equalsIgnoreCase(departureName) && destinationName.equalsIgnoreCase(arrivalName)) {

                    Ticket ticket = new Ticket(
                            ticketJsonObject.get("origin").toString(),
                            originName,
                            ticketJsonObject.get("destination").toString(),
                            destinationName,
                            LocalDate.parse(ticketJsonObject.get("departure_date").toString(), DateTimeFormatter.ofPattern("dd.MM.yy")),
                            LocalTime.parse(ticketJsonObject.get("departure_time").toString(), DateTimeFormatter.ofPattern("H:mm")),
                            LocalDate.parse(ticketJsonObject.get("arrival_date").toString(), DateTimeFormatter.ofPattern("dd.MM.yy")),
                            LocalTime.parse(ticketJsonObject.get("arrival_time").toString(), DateTimeFormatter.ofPattern("H:mm")),
                            ticketJsonObject.get("carrier").toString(),
                            Integer.parseInt(ticketJsonObject.get("stops").toString()),
                            Integer.parseInt(ticketJsonObject.get("price").toString())
                    );
                    tickets.add(ticket);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateDuration(int departureTimeShift, int arrivalTimeShift) {
        String departureTimeZone;
        String arrivalTimeZone;

        if (departureTimeShift <= 0) {
            departureTimeZone = "UTC" + departureTimeShift;
        } else {
            departureTimeZone = "UTC+" + departureTimeShift;
        }

        if (arrivalTimeShift <= 0) {
            arrivalTimeZone = "UTC" + arrivalTimeShift;
        } else {
            arrivalTimeZone = "UTC+" + arrivalTimeShift;
        }

        for (Ticket ticket : tickets) {
            long departure = LocalDateTime.of(ticket.getDepartureDate(), ticket.getDepartureTime())
                    .atZone(ZoneId.of(departureTimeZone)).toEpochSecond();
            long arrival = LocalDateTime.of(ticket.getArrivalDate(), ticket.getArrivalTime())
                    .atZone(ZoneId.of(arrivalTimeZone)).toEpochSecond();
            //разницу в часовых поясах (departureTimeShift - arrivalTimeShift) переводим из часов в секунды ->
            //к ней прибавляем разницу во времени (arrival - departure) ->
            //переводим в миллисекунды
            ticket.setDuration((arrival - departure + (departureTimeShift - arrivalTimeShift) * 3_600L) * 1_000);
        }
    }
}