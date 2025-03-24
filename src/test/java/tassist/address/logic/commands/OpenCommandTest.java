package tassist.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tassist.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static tassist.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static tassist.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static tassist.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static tassist.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tassist.address.commons.core.index.Index;
import tassist.address.logic.Messages;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code OpenCommand}.
 */
public class OpenCommandTest {

    private Model model;
    private TestBrowserService browserService;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        browserService = new TestBrowserService();
    }

    @Test
    public void execute_validIndexUnfilteredList_success() throws CommandException {
        Person personToOpen = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        OpenCommand openCommand = new OpenCommand(INDEX_FIRST_PERSON, browserService);

        String expectedMessage = String.format(OpenCommand.MESSAGE_OPEN_GITHUB_SUCCESS,
                Messages.format(personToOpen));

        CommandResult result = openCommand.execute(model);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(personToOpen.getGithub().value, browserService.getLastUrlOpened());
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        OpenCommand openCommand = new OpenCommand(outOfBoundIndex, browserService);

        assertCommandFailure(openCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person personToOpen = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        OpenCommand openCommand = new OpenCommand(INDEX_FIRST_PERSON, browserService);

        String expectedMessage = String.format(OpenCommand.MESSAGE_OPEN_GITHUB_SUCCESS,
                Messages.format(personToOpen));

        CommandResult result = openCommand.execute(model);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(personToOpen.getGithub().value, browserService.getLastUrlOpened());
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        OpenCommand openCommand = new OpenCommand(outOfBoundIndex, browserService);

        assertCommandFailure(openCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_browserServiceThrowsException_returnsFailureMessage() throws CommandException {
        Person personToOpen = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        OpenCommand openCommand = new OpenCommand(INDEX_FIRST_PERSON, new FailingBrowserService());

        String expectedMessage = String.format(OpenCommand.MESSAGE_OPEN_GITHUB_FAILURE,
                Messages.format(personToOpen));

        CommandResult result = openCommand.execute(model);
        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    @Test
    public void executeBrowserServiceThrowsException_returnsFailureMessage_filteredList() throws CommandException {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Person personToOpen = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        OpenCommand openCommand = new OpenCommand(INDEX_FIRST_PERSON, new FailingBrowserService());

        String expectedMessage = String.format(OpenCommand.MESSAGE_OPEN_GITHUB_FAILURE,
                Messages.format(personToOpen));

        CommandResult result = openCommand.execute(model);
        assertEquals(expectedMessage, result.getFeedbackToUser());
    }

    @Test
    public void toString_returnsCorrectFormat() {
        OpenCommand openCommand = new OpenCommand(INDEX_FIRST_PERSON, browserService);
        String expected = new StringBuilder()
                .append(OpenCommand.class.getName())
                .append("{")
                .append("targetIndex=")
                .append(INDEX_FIRST_PERSON)
                .append("}")
                .toString();
        assertEquals(expected, openCommand.toString());
    }

    @Test
    public void equals() {
        OpenCommand openFirstCommand = new OpenCommand(INDEX_FIRST_PERSON, browserService);
        OpenCommand openSecondCommand = new OpenCommand(INDEX_SECOND_PERSON, browserService);

        // same object -> returns true
        assertEquals(openFirstCommand, openFirstCommand);

        // same values -> returns true
        OpenCommand openFirstCommandCopy = new OpenCommand(INDEX_FIRST_PERSON, browserService);
        assertEquals(openFirstCommand, openFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, openFirstCommand);

        // null -> returns false
        assertNotEquals(null, openFirstCommand);

        // different person -> returns false
        assertNotEquals(openFirstCommand, openSecondCommand);
    }

    /**
     * Test implementation of BrowserService that records URLs instead of opening them.
     */
    private static class TestBrowserService implements OpenCommand.BrowserService {
        private List<String> urlsOpened = new ArrayList<>();

        @Override
        public void openUrl(String url) throws IOException, URISyntaxException {
            urlsOpened.add(url);
        }

        /**
         * Returns the list of URLs that would have been opened.
         */
        public List<String> getUrlsOpened() {
            return urlsOpened;
        }

        /**
         * Returns the last URL that would have been opened, or null if none.
         */
        public String getLastUrlOpened() {
            return urlsOpened.isEmpty() ? null : urlsOpened.get(urlsOpened.size() - 1);
        }

        /**
         * Clears the list of opened URLs.
         */
        public void clear() {
            urlsOpened.clear();
        }
    }

    /**
     * Test implementation of BrowserService that always throws an exception.
     */
    private static class FailingBrowserService implements OpenCommand.BrowserService {
        @Override
        public void openUrl(String url) throws IOException, URISyntaxException {
            throw new IOException("Test exception");
        }
    }
}
