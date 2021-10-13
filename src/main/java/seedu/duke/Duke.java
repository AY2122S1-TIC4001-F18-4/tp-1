package seedu.duke;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.lang.System.exit;
import static java.time.LocalDateTime.parse;
import static java.util.Comparator.comparing;

public class Duke {
    /**
     * Main entry-point for the java.duke.Duke application.
     */
    static final List<Bookings> appointments = new ArrayList<>();
    static final SortedMap<LocalDate, LocalDate> BLOCKLIST = new TreeMap<>();

    public static void main(String[] args) {
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println("Hello from\n" + logo);
        System.out.println("What is your name?");

        Scanner in = new Scanner(System.in);
        String userName = in.nextLine();
        System.out.printf("Hello %s,%n", userName);

        // write your code here
        System.out.printf("Welcome back!%n");
        System.out.printf("You can now proceed to do your booking :)%n");

        while (true) {
            System.out.printf("%n%s: ", userName);
            String input = in.nextLine().replace("\\s+", " ").trim();

            if (input.equals("exit") || input.equals("bye")) {
                System.out.println("Bye! See you again!");
                in.close();
                exit(0);
            }
            //parses user input
            String[] tokens = input.split(" ", 2);
            String command = tokens[0];

            switch (command) {
            case "add":
                String[] arguments = tokens[1].split("/[s,e] ");

                DateTimeFormatter format = DateTimeFormatter.ofPattern("[yyyy-M-d K:mm a][yyyy-M-d HH:mm]");
                LocalDateTime start = parse(arguments[1].trim().toUpperCase(), format);

                Map.Entry<Boolean, Map.Entry<LocalDate, LocalDate>> result = isInRange(start.toLocalDate());

                if (result.getKey()) {
                    System.out.printf("'%s' denied by current BLOCKLIST = StartDate: [%s] - EndDate: [%s]%n",
                            start.toLocalDate(), result.getValue().getKey(), result.getValue().getValue());
                    continue;
                }

                LocalDateTime end = parse(arguments[2].toUpperCase(), format);

                appointments.add(new Bookings(arguments[0].trim(), start, end));
                break;
            case "edit":

                break;
            case "save":

                break;
            case "del":
                if (tokens[1].contains("all")) {
                    appointments.clear();
                    System.out.println("Successfully deleted all appointment records.");
                }
                break;
            case "show":
                int listNum = 1;

                if (tokens[1].contains("all")) {
                    if (appointments.size() == 0) {
                        System.out.println("Current appointments list is : empty");
                    }
                    appointments.sort(comparing(Bookings::getStartDateTime));
                    int total = appointments.size();
                    //displays user's complete list of bookings in the database
                    System.out.println();

                    for (int i = 0; i < total; ) {
                        LocalDate startDate = appointments.get(i).getStartDate();
                        String dateRep = String.valueOf(startDate).replaceAll("-", "/");
                        System.out.printf("Date: %s%n", dateRep);

                        while (i < total && appointments.get(i).getStartDate().equals(startDate)) {
                            System.out.printf("%d. %s%n", listNum++, appointments.get(i));
                            i++;
                        }
                        System.out.println();
                    }
                } else if (tokens[1].matches("^(.*)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$")) {
                    String date = tokens[1];
                    LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-M-d"));
                    String putDate = String.valueOf(localDate).replaceAll("-", "/");

                    //displays the specified date booking(s) of user
                    LocalDate dateIso;
                    System.out.println();
                    System.out.printf("Date: %s%n", putDate);

                    if (date.matches("^((2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")) {
                        dateIso = LocalDate.parse(date);
                    } else {
                        dateIso = getLocalDate(date);
                    }

                    for (Bookings item : appointments) {

                        if (dateIso.equals(item.getStartDate())) {
                            System.out.printf("%d. %s%n", listNum++, item);
                        }
                    }
                    System.out.println();
                }
                break;
            case "block":
                String[] parts = tokens[1].split(" - ");
                LocalDate commence = getLocalDate(parts[0]);
                LocalDate terminate = getLocalDate(parts[1]);
                BLOCKLIST.put(commence, terminate);
                break;
            case "unblock":
                String[] items = tokens[1].split(" - ");
                LocalDate unLockDate = getLocalDate(items[0]);
                BLOCKLIST.remove(unLockDate);
                break;
            case "help":

                break;
            default:
                System.out.println("You have entered an unknown or invalid command, please try again!");
            }
        }
    }

    private static Map.Entry<Boolean, Map.Entry<LocalDate, LocalDate>> isInRange(LocalDate testDate) {
        boolean flag = false;
        Map.Entry<LocalDate, LocalDate> member = null;

        for (Map.Entry<LocalDate, LocalDate> entry : BLOCKLIST.entrySet()) {

            if (!(testDate.isBefore(entry.getKey()) || testDate.isAfter(entry.getValue()))) {
                member = entry;
                flag = true;
                break;
            }
        }
        return new SimpleEntry<Boolean, Map.Entry<LocalDate, LocalDate>>(flag, member);
    }

    private static LocalDate getLocalDate(String date) {
        LocalDate dateIso;
        int[] figures = Arrays.stream(date.split("-"))
                .mapToInt(Integer::parseInt)
                .toArray();
        dateIso = LocalDate.of(figures[0], figures[1], figures[2]);

        return dateIso;
    }
}