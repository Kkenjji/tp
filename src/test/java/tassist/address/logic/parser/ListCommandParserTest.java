package tassist.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import tassist.address.logic.commands.ListCommand;
import tassist.address.logic.parser.exceptions.ParseException;

public class ListCommandParserTest {
    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_validArgs_returnsListCommand() throws Exception {
        // No arguments -> should return a ListCommand with null parameters
        ListCommand expectedCommand1 = new ListCommand(null, null, null, null);
        assertEquals(expectedCommand1, parser.parse(""));

        // Sort by name ascending
        ListCommand expectedCommand2 = new ListCommand("name", "asc", null, null);
        assertEquals(expectedCommand2, parser.parse(" s/name o/asc"));

        // Sort by progress descending
        ListCommand expectedCommand3 = new ListCommand("progress", "des", null, null);
        assertEquals(expectedCommand3, parser.parse(" s/progress o/des"));

        // Filter by course CS2103
        ListCommand expectedCommand4 = new ListCommand(null, null, "course", "CS2103");
        assertEquals(expectedCommand4, parser.parse(" f/course fv/CS2103"));

        // Sort by GitHub ascending, filter by team Alpha
        ListCommand expectedCommand5 = new ListCommand("github", "asc", "team", "Alpha");
        assertEquals(expectedCommand5, parser.parse(" s/github o/asc f/team fv/Alpha"));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // Invalid sort type
        assertThrows(ParseException.class, () -> parser.parse(" s/invalid o/asc"));

        // Invalid sort order
        assertThrows(ParseException.class, () -> parser.parse(" s/name o/random"));

        // Invalid filter type
        assertThrows(ParseException.class, () -> parser.parse(" f/invalid fv/CS2103"));

        // Missing sort order
        assertThrows(ParseException.class, () -> parser.parse(" s/name"));

        // Missing filter value
        assertThrows(ParseException.class, () -> parser.parse(" f/course"));
    }

    @Test
    public void parse_invalidProgressNonNumber_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(" f/progress fv/notANumber"));
    }

    @Test
    public void parse_invalidProgressOutOfBounds_throwsParseException() {
        assertThrows(ParseException.class, () -> parser.parse(" f/progress fv/150"));
    }
}
