package ev.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class EventTest {

    private Event camp() {
        return new Event("camp",
                LocalDateTime.of(2019, 12, 1, 9, 0),
                LocalDateTime.of(2019, 12, 3, 17, 0));
    }

    @Test
    public void toString_bothTimes_shown() {
        assertEquals("[E][ ] camp (from: Dec 1 2019, 9:00 AM to: Dec 3 2019, 5:00 PM)", camp().toString());
    }

    @Test
    public void toFileFormat_bothTimes_isoDatesStored() {
        assertEquals("E | 0 | camp | 2019-12-01T09:00 | 2019-12-03T17:00", camp().toFileFormat());
    }

    @Test
    public void occursOn_firstDay_true() {
        assertTrue(camp().occursOn(LocalDate.of(2019, 12, 1)));
    }

    @Test
    public void occursOn_dayInBetween_true() {
        assertTrue(camp().occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_lastDay_true() {
        assertTrue(camp().occursOn(LocalDate.of(2019, 12, 3)));
    }

    @Test
    public void occursOn_dayBefore_false() {
        assertFalse(camp().occursOn(LocalDate.of(2019, 11, 30)));
    }

    @Test
    public void occursOn_dayAfter_false() {
        assertFalse(camp().occursOn(LocalDate.of(2019, 12, 4)));
    }

    @Test
    public void occursOn_singleDayEvent_onlyThatDay() {
        Event meeting = new Event("meeting",
                LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 2, 16, 0));
        assertTrue(meeting.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(meeting.occursOn(LocalDate.of(2019, 12, 1)));
        assertFalse(meeting.occursOn(LocalDate.of(2019, 12, 3)));
    }
}
