package ev.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TodoTest {

    @Test
    public void toString_newTodo_emptyStatusBox() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_doneTodo_tickedStatusBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toString_unmarkedAgain_backToEmptyStatusBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileFormat_newTodo_notDoneFlag() {
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
    }

    @Test
    public void toFileFormat_doneTodo_doneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void occursOn_anyDate_false() {
        assertFalse(new Todo("read book").occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void hasKeyword_wordInDescription_true() {
        assertTrue(new Todo("read book").hasKeyword("book"));
    }

    @Test
    public void hasKeyword_partOfWord_true() {
        assertTrue(new Todo("read book").hasKeyword("oo"));
    }

    @Test
    public void hasKeyword_differentCase_true() {
        assertTrue(new Todo("Read Book").hasKeyword("book"));
        assertTrue(new Todo("read book").hasKeyword("BOOK"));
    }

    @Test
    public void hasKeyword_wordNotInDescription_false() {
        assertFalse(new Todo("read book").hasKeyword("plants"));
    }
}
