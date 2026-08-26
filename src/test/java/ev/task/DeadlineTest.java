package ev.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    private static final LocalDateTime DEC_2_6PM = LocalDateTime.of(2019, 12, 2, 18, 0);

    @Test
    public void toString_withTime_timeShown() {
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)",
                new Deadline("return book", DEC_2_6PM).toString());
    }

    @Test
    public void toString_midnight_onlyDateShown() {
        assertEquals("[D][ ] return book (by: Dec 2 2019)",
                new Deadline("return book", LocalDateTime.of(2019, 12, 2, 0, 0)).toString());
    }

    @Test
    public void toFileFormat_withTime_isoDateStored() {
        assertEquals("D | 0 | return book | 2019-12-02T18:00",
                new Deadline("return book", DEC_2_6PM).toFileFormat());
    }

    @Test
    public void toFileFormat_doneDeadline_doneFlag() {
        Deadline deadline = new Deadline("return book", DEC_2_6PM);
        deadline.markAsDone();
        assertEquals("D | 1 | return book | 2019-12-02T18:00", deadline.toFileFormat());
    }

    @Test
    public void occursOn_sameDayDifferentTime_true() {
        assertTrue(new Deadline("return book", DEC_2_6PM).occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void occursOn_dayBeforeOrAfter_false() {
        Deadline deadline = new Deadline("return book", DEC_2_6PM);
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 1)));
        assertFalse(deadline.occursOn(LocalDate.of(2019, 12, 3)));
    }
}
