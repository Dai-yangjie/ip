package ev;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateTimes {

    public static final String ACCEPTED_FORMATS =
            "2019-12-02, 2019-12-02 1800, 2/12/2019 or 2/12/2019 1800";

    private static final DateTimeFormatter[] DATE_TIME_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d/M/yyyy HHmm", Locale.ENGLISH)
    };

    private static final DateTimeFormatter[] DATE_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)
    };

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a", Locale.ENGLISH);

    public static LocalDateTime parse(String text) throws EvException {
        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(text, format);
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return LocalDate.parse(text, format).atStartOfDay();
            } catch (DateTimeParseException e) {
                continue;
            }
        }
        throw new EvException("I don't understand the date \"" + text + "\".\n"
                + "Please use one of: " + ACCEPTED_FORMATS + ".");
    }

    public static String format(LocalDateTime dateTime) {
        boolean hasTime = !dateTime.toLocalTime().equals(LocalTime.MIDNIGHT);
        return dateTime.format(hasTime ? DISPLAY_DATE_TIME : DISPLAY_DATE);
    }

    public static String format(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    public static String toFileFormat(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    public static LocalDateTime fromFileFormat(String text) throws EvException {
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException e) {
            throw new EvException("Not a saved date: " + text);
        }
    }
}
