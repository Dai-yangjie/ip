package ev.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import ev.EVException;
import ev.command.AddCommand;
import ev.command.Command;
import ev.command.DeleteCommand;
import ev.command.ExitCommand;
import ev.command.FindCommand;
import ev.command.FindCommand;
import ev.command.ListCommand;
import ev.command.MarkCommand;
import ev.command.OnCommand;

public class ParserTest {

    @Test
    public void parse_eachKeyword_matchingCommandReturned() throws EVException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-12-02"));
        assertInstanceOf(AddCommand.class, Parser.parse("event trip /from 2019-12-01 /to 2019-12-03"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    public void parse_bye_isExitCommand() throws EVException {
        assertTrue(Parser.parse("bye").isExit());
    }

    @Test
    public void parse_listCommand_isNotExitCommand() throws EVException {
        assertFalse(Parser.parse("list").isExit());
    }

    @Test
    public void parse_unknownKeyword_messageListsKeywords() {
        EVException thrown = assertThrows(EVException.class, () -> Parser.parse("blah"));
        assertTrue(thrown.getMessage().contains("blah"));
        assertTrue(thrown.getMessage().contains(CommandWord.listKeywords()));
    }

    @Test
    public void parse_keywordAsPrefixOfAnotherWord_notRecognised() {
        assertThrows(EVException.class, () -> Parser.parse("listing"));
        assertThrows(EVException.class, () -> Parser.parse("todos read book"));
    }

    @Test
    public void parse_descriptionContainingKeyword_treatedAsDescription() throws EVException {
        Command command = Parser.parse("todo list the books");
        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void parseTodo_description_kept() throws EVException {
        assertEquals("[T][ ] read book", Parser.parseTodo("read book").toString());
    }

    @Test
    public void parseTodo_emptyDescription_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseTodo(""));
    }

    @Test
    public void parseDeadline_descriptionAndTime_bothKept() throws EVException {
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)",
                Parser.parseDeadline("return book /by 2019-12-02 1800").toString());
    }

    @Test
    public void parseDeadline_extraSpaces_trimmed() throws EVException {
        assertEquals("[D][ ] return book (by: Dec 2 2019)",
                Parser.parseDeadline("  return book   /by   2019-12-02  ").toString());
    }

    @Test
    public void parseDeadline_missingBy_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_missingDescription_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseDeadline("/by 2019-12-02"));
    }

    @Test
    public void parseDeadline_missingTime_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseDeadline("return book /by"));
    }

    @Test
    public void parseDeadline_unreadableTime_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseDeadline("return book /by Sunday"));
    }

    @Test
    public void parseEvent_descriptionAndBothTimes_allKept() throws EVException {
        assertEquals("[E][ ] trip (from: Dec 1 2019, 2:00 PM to: Dec 3 2019, 4:00 PM)",
                Parser.parseEvent("trip /from 2019-12-01 1400 /to 2019-12-03 1600").toString());
    }

    @Test
    public void parseEvent_missingFrom_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseEvent("trip /to 2019-12-03"));
    }

    @Test
    public void parseEvent_missingTo_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseEvent("trip /from 2019-12-01"));
    }

    @Test
    public void parseEvent_toBeforeFrom_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseEvent("trip /to 2019-12-03 /from 2019-12-01"));
    }

    @Test
    public void parseEvent_missingDescription_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseEvent("/from 2019-12-01 /to 2019-12-03"));
    }

    @Test
    public void parseTaskNumber_number_returnsSameNumber() throws EVException {
        assertEquals(2, Parser.parseTaskNumber("2"));
    }

    @Test
    public void parseTaskNumber_notANumber_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseTaskNumber("two"));
    }

    @Test
    public void parseTaskNumber_empty_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseTaskNumber(""));
    }

    @Test
    public void parseDate_dateWithTime_timeDropped() throws EVException {
        assertEquals(LocalDate.of(2019, 12, 2), Parser.parseDate("2019-12-02 1800"));
    }

    @Test
    public void parseDate_empty_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseDate(""));
    }

    @Test
    public void parseKeyword_word_returnedUnchanged() throws EVException {
        assertEquals("book", Parser.parseKeyword("book"));
    }

    @Test
    public void parseKeyword_severalWords_keptTogether() throws EVException {
        assertEquals("read book", Parser.parseKeyword("read book"));
    }

    @Test
    public void parseKeyword_empty_exceptionThrown() {
        assertThrows(EVException.class, () -> Parser.parseKeyword(""));
    }
}
