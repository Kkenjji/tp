package tassist.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tassist.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static tassist.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static tassist.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static tassist.address.logic.commands.CommandTestUtil.PROGRESS_DESC_AMY;
import static tassist.address.logic.commands.CommandTestUtil.STUDENTID_DESC_AMY;
import static tassist.address.testutil.Assert.assertThrows;
import static tassist.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import tassist.address.logic.commands.AddCommand;
import tassist.address.logic.commands.CommandResult;
import tassist.address.logic.commands.DeleteCommand;
import tassist.address.logic.commands.ListCommand;
import tassist.address.logic.commands.OpenCommand;
import tassist.address.logic.commands.exceptions.CommandException;
import tassist.address.logic.parser.exceptions.ParseException;
import tassist.address.model.Model;
import tassist.address.model.ModelManager;
import tassist.address.model.ReadOnlyAddressBook;
import tassist.address.model.UserPrefs;
import tassist.address.model.person.Person;
import tassist.address.storage.JsonAddressBookStorage;
import tassist.address.storage.JsonUserPrefsStorage;
import tassist.address.storage.StorageManager;
import tassist.address.testutil.PersonBuilder;

/**
 * Contains tests for LogicManager, including handling command execution and storage exceptions.
 */
public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private final Model model = new ModelManager();
    private LogicManager logic;
    private TestBrowserService browserService;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        browserService = new TestBrowserService();
        logic = new LogicManager(model, storage, browserService);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        if (model.getFilteredPersonList().isEmpty()) {
            assertCommandSuccess(listCommand, ListCommand.MESSAGE_NO_STUDENTS, model);
        } else {
            assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
        }
    }

    @Test
    public void execute_deleteCommandRequiresConfirmation() throws Exception {
        Person personToDelete = new PersonBuilder(AMY).build();
        model.addPerson(personToDelete);

        String deleteCommand = "delete 1";

        // Step 1: Ensure confirmation is required
        CommandResult confirmationResult = logic.execute(deleteCommand);
        assertEquals(
                String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(personToDelete)),
                confirmationResult.getFeedbackToUser()
        );

        // Step 2: Confirm deletion
        CommandResult deletionResult = logic.execute("Y");
        assertEquals(
                String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)),
                deletionResult.getFeedbackToUser()
        );
    }

    @Test
    public void execute_deleteCommandCancelled() throws Exception {
        Person personToDelete = new PersonBuilder(AMY).build();
        model.addPerson(personToDelete);

        String deleteCommand = "delete 1";

        // Step 1: Ensure confirmation is required
        CommandResult confirmationResult = logic.execute(deleteCommand);
        assertEquals(
                String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(personToDelete)),
                confirmationResult.getFeedbackToUser()
        );

        // Step 2: Cancel deletion
        CommandResult cancelResult = logic.execute("N");
        assertEquals("Action cancelled.", cancelResult.getFeedbackToUser());

        // Ensure person still exists
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_invalidConfirmationInput_returnsErrorMessage() throws Exception {
        Person personToDelete = new PersonBuilder(AMY).build();
        model.addPerson(personToDelete);

        String deleteCommand = "delete 1";

        // Step 1: Ensure confirmation is required
        logic.execute(deleteCommand);

        // Step 2: Provide invalid input
        CommandResult invalidResponse = logic.execute("xyz");
        assertEquals("Invalid response. Please enter Y/N.", invalidResponse.getFeedbackToUser());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    @Test
    public void execute_openCommand_success() throws Exception {
        Person personToOpen = new PersonBuilder(AMY).build();
        model.addPerson(personToOpen);

        String openCommand = "open 1";
        CommandResult result = logic.execute(openCommand);
        assertEquals(
                String.format(OpenCommand.MESSAGE_OPEN_GITHUB_SUCCESS, Messages.format(personToOpen)),
                result.getFeedbackToUser()
        );
        assertEquals(personToOpen.getGithub().value, browserService.getLastUrlOpened());
    }

    @Test
    public void execute_openCommandInvalidIndex_failure() throws Exception {
        String openCommand = "open 1";
        assertCommandException(openCommand);
    }

    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    private void assertParseException(String inputCommand) {
        assertCommandFailure(inputCommand, ParseException.class, Messages.MESSAGE_UNKNOWN_COMMAND);
    }

    private void assertCommandException(String inputCommand) {
        assertCommandFailure(inputCommand, CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook, Path filePath)
                    throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + STUDENTID_DESC_AMY + PROGRESS_DESC_AMY;

        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
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
}
